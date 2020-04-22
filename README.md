# zoomapi-java
Zoom API implementation in Java

### Project Description
This project was based on the python implementation of Zoom API forked from https://github.com/crista/zoomapi and was completed as part of a group project for the SWE262P Programming Styles course at UCI. 

### Team members:
Duo Chai</br>
Soobin Choi

## To build the program:
Open the project in an IDE 
<br> In the root folder, run the following in the terminal to build the project properly in your local repository:
<br> Run the package goal to compile and package the code in a JAR file: 
```
mvn package
```
![Package Success](/pics/mvn-package.png)
<br> Install the project's JAR file into the local repository:
```
mvn install
```
The output on terminal should look like this: 
<br> ![Install Success](/pics/mvn-install.png)
## To use your client id and secret for configuration: 
Go to bots/bot.ini
<br> Insert your credentials in
```
client_id =
```
and 
```
client_secret =
```
And your local browser path in
```
browser_path =
```
## To run the program:
Open an ngrok tunnel from terminal: 
```
ngrok http 4001
```
<br>You may change the port number but you will also need to change the port in bot.ini configuration file.
To run the bot, first navigate to the correct folder: 
```
cd src/main/java/
```
Then, run with:
```
java bots/botm1.java
```
Once the bot runs successfully, you will receive Status Code 200 with a zoom link.<br>
<br>
**Click on the link** and the bot application should appear:<br> 
![Zoom Link](/pics/zoom-link.png)
## Throttle:
To throttle the call rate at 10 calls per second, we have wrapped all related methods in the Throttle class in the utils folder. The throttle timestamp apparent in the above screenshot is a long-type number calculated using
```
Date().getTime()
```
method and we throttle at the request level.  

