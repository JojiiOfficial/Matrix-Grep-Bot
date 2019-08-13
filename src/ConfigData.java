public class ConfigData {
    private String host, username, password, token;

    public ConfigData(String host, String username, String password, String token) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.token = token;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }
}
