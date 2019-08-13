# Matrix-Grep-Bot
A [Matrix](https://matrix.org/) bot to use grep for searching in large messages using my own [API](https://github.com/JojiiOfficial/Matrix-ClientServer-API-java)

## Download

You can download the bot [here](https://jojii.de/files/matrix/bots/MatrixGrepBot.jar)

## Install
Just run the bot once (`java -jar MatrixGrepBot.jar`) to create the config.json or create a file in the same directory and paste

```json
{
  "host": "http://matrix.com",
  "username": "grepbot",
  "password": "",
  "usertoken": ""
}
```
then fill the json with your data and save it. Then start the bot again. Thats it.
<br>
Note: `If a password is given, the config removes it and replace the token with the token of the given user`

## Usage
Invite the bot to a room. It will join automatically. Then reply on a message with <br>`!grep <search pattern>` or `! grep <search pattern>` <br>
<br><br>Note `Currently the bot only supports casesensitive search`
<br><br>The nextBatch-file is a file you shouldnt delete unless you want to run all past !grep commands again because it saves the sync-state
