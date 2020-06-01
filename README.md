# zoomapi-java
Zoom API implementation in Java

### Project Description
This project was based on the python implementation of Zoom API forked from https://github.com/crista/zoomapi and was completed as part of a group project for the SWE262P Programming Styles course at UCI. 

### Team members:
Duo Chai</br>
Soobin Choi

## To build the program:
**MAKE SURE you have java 13 installed and set as default SDK, or please change the maven pom.xml configuration from 13 to your version of java**
<br> In the root folder, run the following in the terminal to build the project properly in your local repository:
<br> Run the package goal to compile and package the code in a JAR file: 
```
mvn clean package
```
![Package Success](/pics/mvn-package.png)
<br> Install the project's JAR file into the local repository:
```
mvn clean install
```
The output on terminal should look like this: 
<br> ![Install Success](/pics/mvn-install.png)
## To use your client id and secret for configuration: 
Go to `src/main/java/bots/bot.ini`
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
ngrok http 4000
```
<br>
You may change the port number but you will also need to change the port in bot.ini configuration file.
<br>

To run the bot, first make sure you are currently in the root folder<br>
then either click the run button from IDE navigation bar (you may need to specify the main class as `src/main/java/bots/botm#.java` depending on which bot you want to run) or run the following in terminal<br>
```
java -cp target/zoomapi-java-0.1.0.jar src/main/java/bots/botm1.java
```
```
java -cp target/zoomapi-java-0.1.0.jar src/main/java/bots/botm3.java
```
```
java -cp target/zoomapi-java-0.1.0.jar src/main/java/bots/botm4.java
```
Once the bot runs successfully, you will receive Status Code 200 with a zoom link.<br>
<br>
**Click on the link** (in IDE) or **copy the Zoom redirect_url and paste it in your browser**(Zoom redirect_url starts with https://zoom.us/oauth/authorize?...).
<br>
The bot application should appear as below:<br> 
<br> ![Zoom Link](/pics/zoom-link.png)
### Expected Result:
The expected output in the terminal for a successful run looks like this: 
<br> ![Success](/pics/run-success.png) 
<br> Sometimes, the socket we setup cannot receive a response from Zoom due to network issues, in which case we throw an Exception acknowledging the situation, resulting in an output that looks like this:
<br> ![Fail](/pics/run-fail.png)
<br> Please try rerunning the program to fix the issue. 
## Throttle:
To throttle the call rate at 10 calls per second, we have wrapped all related methods in the Throttle class in the utils folder. The throttle timestamp apparent in the above screenshot is a long-type number calculated using
```
Date().getTime()
```
method and we throttle at the request level. 

## Botm3 Result:
The expected output in the terminal for a successful run on botm3.java looks like this: 
<br> ![Success](/pics/m3/botm3-success.png) 
<br> Once the application shows up in the terminal, you can pick one of the three options:
<br> 1. Send Chat to a Channel
<br> 2. Get Chat History
<br> 3. Search an Event
<br> The expected output for option 1 looks like this:
<br> ![Success](/pics/m3/botm3-option1.png) 
<br> The expected output for option 2 looks like this, with a list of formatted messages from start to end date:
<br> ![Success](/pics/m3/botm3-option2.png) 
<br> The expected output for option 3 looks like this, with a list of formatted messages with the specified sender, and another list of messages with the specified messages string:
<br> ![Success](/pics/m3/botm3-option3.png) 

## Botm4 Set Up and Result:
This bot exercises events that are subscribed to specific channels, or observables, within a specific time frame. As such, when we initialize the subscribeAgency and instantiate channel observers, we define the channel name in our parameter and call setObservingDate on each observer to specify the start date (fromDate) and end date (toDate) of our observation.
<br> Keep in mind that the Zoom API treats the timezone as UTC/GMT. Adjust your start and end date accordingly.
<br> **To run the bot properly, make sure that you pass a valid channel name as the third parameter in the ChannelObserver instantiation. Additionally, ensure that the observation start and end date are valid dates as well.** Below is a sample of the ChannelObserver intialization: 
<br> ![ChannelObserver](/pics/m4/set-observation.png)
<br>The expected output in the terminal for a successful run on botm4.java looks like this. Please wait until all event listeners have started working before triggering new events: 
<br> ![Success m4](/pics/m4/m4-result.png) 
<br> The last three lines indicate the output from the 'event-based subscription' mechanism, where the observers in bot code (Soobin & Duo) received notification when
<br> 1. A new member was added to the channel;
<br> 2. A new message was received in the channel; and
<br> 3. A message in a channel was updated. 

## Milestone 5 Project Set Up and Result: 
### Structural Changes: 
There was a major restructuring and refactoring of our project to address the feedback provided in the previous milestones and to optimize the library based on the requirements of the final milestone. <br>The architecture of our final library system is provided in the following diagram. 
<br> ![Zoom API Architecture](/pics/m5/zoom-api-architectureREPLACE.png)
### Cache System: 
<br> For the final milestone, we modified one of our previous bots, botm1, to test the integration of a new cache system to our library. Instead of executing HTTP requests each time we want to test our zoom Channel and Message comoponents, we cache the queried data in a local cache memory organized into 4 different tables (Credentials, Channel, ChannelMember, and ChannelMessage). Below is a screenshot of our TableHelper.java class, which is a generically typed helper class that each of the four tables extends to implement its own table.  
<br> ![Cache Table](/pics/m5/table_helper.png)
<br> The botm1 application provides options to exercise each of the Channel and Message components, but with an additional option to query directly from local cache in relevant cases. By testing the application, you will notice that the response time is significantly reduced.
<br> Below is what a successful request looks like.
<br> ![Successful Query](/pics/m5/successful_query.png)
<br> It is important to note that Zoom API's message components can be performed in two contexts: messages sent to a channel and messages sent to a member. For the purpose of this project, we provide one table that stores message data sent to both a channel and a member by treating a member as a channel (channelId and channelName columns will store the member's email address). We will be able to query both types of messsages from our cache system using our bot application. 
