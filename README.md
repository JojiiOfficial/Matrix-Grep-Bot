# Matrix-Grep-Bot
A Matrix bot to use grep for searching in large messages using my own [API](https://github.com/JojiiOfficial/Matrix-ClientServer-API-java)

## Install
Just run the bot once to create the config.json or create a file in the same directory and paste

```json
{
  "host": "http://matrix.com",
  "username": "grepbot",
  "password": "",
  "usertoken": ""
}
```
then fill the json with your data and save it.

Note: `If a password is given, the config removes it and replace the token with the token of the give user`

## Usage
Invite the bot to a room. It will join automatically. Then reply on a message with `!grep <search pattern>` or `! grep <search pattern>`
<br>Note `Currently the bot only supports casesensitive search`