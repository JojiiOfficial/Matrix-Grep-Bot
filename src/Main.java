import de.jojii.matrixclientserver.Bot.Client;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        //TODO custom configfile
        String configFile = "./config.json";
        new Main(configFile);
    }

    public Main(String sConfigFile) {
        try {
            File configFile = new File(sConfigFile);
            ConfigData configData = null;
            if (!configFile.exists()) {
                System.out.println("Configfile not exists. Creating default-config");
                if (configFile.createNewFile()) {
                    FileHelper.writeFile(sConfigFile, "{\n" +
                            "  \"host\": \"http://matrix.org\",\n" +
                            "  \"username\": \"grepbot\",\n" +
                            "  \"password\": \"wordpass\",\n" +
                            "  \"usertoken\": \"user70ken\"\n" +
                            "}\n");
                    System.out.println("Default-config created!\r\n" + "You can pass username and password OR usertoken\r\nAdjust the config and restart the bot\r\nPassword will be cleaned and replaced with usertoken");
                    System.exit(0);
                } else {
                    System.out.println("Couldn't create default-config! Exiting");
                    System.exit(1);
                }
            } else {
                String configContent = FileHelper.readFile(sConfigFile);
                try {
                    JSONObject config = new JSONObject(configContent);
                    if (!config.has("host") || !config.has("username") || !config.has("password") || !config.has("usertoken")) {
                        throw new JSONException("missing paramter!");
                    }
                    String host = config.getString("host");
                    String username = config.getString("username");
                    String password = config.getString("password");
                    String usertoken = config.getString("usertoken");
                    configData = new ConfigData(host, username, password, usertoken);
                } catch (JSONException e) {
                    System.out.println("Configfile in from format! Watch for correct json-content!\r\nYou can delete the config and restart the bot to create a blank config");
                    System.exit(1);
                }
            }

            Client c = new Client(configData.getHost());
            if (configData.getPassword().trim().length() > 0) {
                final ConfigData finalConfigData = configData;

                c.login(configData.getUsername(), configData.getPassword(), data -> {
                    if (data.isSuccess()) {

                        JSONObject newConfig = new JSONObject();
                        newConfig.put("host", finalConfigData.getHost());
                        newConfig.put("username", finalConfigData.getUsername());
                        newConfig.put("password", "");
                        newConfig.put("usertoken", data.getAccess_token());
                        //save new configfile
                        FileHelper.writeFile(sConfigFile, newConfig.toString());

                        clientLoggedIn(c);
                    } else {
                        System.err.println("error logging in! Check your credentials");
                    }
                });
            } else {
                c.login(configData.getToken(), data -> {
                    clientLoggedIn(c);
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clientLoggedIn(Client c) {
        System.out.println(c.getLoginData().getAccess_token());
        c.registerRoomEventListener(roomEvents -> {
            for (RoomEvent event : roomEvents) {
                System.out.println(event.getRaw().toString());

                if (event.getType().equals("m.room.member") && event.getContent().has("membership") && event.getContent().getString("membership").equals("invite")) {
                    try {
                        //make bot autojoin
                        c.joinRoom(event.getRoom_id(), null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (event.getType().equals("m.room.message")) {
                    try {
                        c.sendReadReceipt(event.getRoom_id(), event.getEvent_id(), "m.read", null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (event.getContent().has("body")) {
                        String msg = RoomEvent.getBodyFromMessageEvent(event);
                        if (msg != null && msg.trim().length() > 0) {
                            if ((msg.contains("!grep") || msg.contains("! grep")) && event.getContent().has("m.relates_to") && event.getContent().getJSONObject("m.relates_to").has("m.in_reply_to")) {
                                c.getRoomEventFromId(event.getRoom_id(), event.getContent().getJSONObject("m.relates_to").getJSONObject("m.in_reply_to").getString("event_id"), roomEvent -> {
                                    if (roomEvent != null) {
                                        String msgToCut = roomEvent.getContent().getString("body");

                                        String toSearch = getSearchTermFromCommand(msg);

                                        String[] lines = msgToCut.split("\n");
                                        StringBuilder message = new StringBuilder();
                                        StringBuilder formattedMessage = new StringBuilder();
                                        for (String line : lines) {
                                            if (line.contains(toSearch)) {
                                                message.append(line).append("\r\n");
                                                formattedMessage.append(line.toLowerCase().replace(toSearch.toLowerCase(), "<code>" + toSearch + "</code>")).append("<br>");
                                            }
                                        }
                                        String messagetoSend = message.toString().trim();
                                        if (messagetoSend.length() == 0) {
                                            c.sendText(event.getRoom_id(), "Not found", null);
                                        } else {
                                            c.sendText(event.getRoom_id(), messagetoSend, true, formattedMessage.toString(), null);
                                        }
                                    } else {
                                        System.err.println("roomevent is null");
                                    }
                                });

                            }
                        }
                    }
                }
            }
        });
    }

    private String getSearchTermFromCommand(String command) {
        command = command.substring(command.lastIndexOf("\n")).trim();
        System.out.println(command);

        String[] commandArgsRaw = command.split(" ");
        int startIndex = 0;
        int i = 0;
        for (String arg : commandArgsRaw) {
            if (arg.equalsIgnoreCase("grep") || arg.equalsIgnoreCase("!grep")) {
                startIndex = i + 1;
                break;
            }
            i++;
        }

        String[] commandArgs = new String[commandArgsRaw.length - startIndex];
        i = 0;
        for (int j = startIndex; j < commandArgs.length + startIndex; j++) {
            commandArgs[i] = commandArgsRaw[j];
            i++;
        }

        StringBuilder cmdBuider = new StringBuilder();
        for (String sss : commandArgs) {
            cmdBuider.append(sss).append(" ");
        }

        System.err.println(cmdBuider.toString().trim());

        return cmdBuider.toString().trim();
    }

}
