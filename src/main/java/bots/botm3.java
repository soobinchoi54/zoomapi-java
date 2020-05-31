package bots;

import org.json.JSONArray;
import org.json.JSONObject;
import lib.clients.OauthZoomClient;
import lib.oauth.OauthMessage;
import lib.cache.databaseData.ChannelMessage;
import lib.components.ChatChannelsComponent;
import lib.components.ChatMessagesComponent;
import lib.utils.Util;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class botm3 {

    private static String client_id;
    private static String client_secret;
    private static int PORT;
    private static String browser_path;
    private static String redirect_url;
    private static OauthZoomClient client;
    static boolean stop;

    public static void main (String args[]) throws IOException {
        botm3 ini = new botm3();
        ini.parse();

        stop = false;
        while (!stop) {
            printOptions();
            Scanner sc = new Scanner(System.in);
            String option = sc.nextLine();
            switch (option) {
                case "0":
                    stop = true;
                    return;
                case "1":
                    sendChatToGivenChannel(client);
                    break;
                case "2":
                    getChatHistory(client);
                    break;
                case "3":
                    searchEvent(client);
                    break;
                default:
                    System.out.println("Please provide a valid input...\n");
                    break;
            }
            System.out.println("Press any to continue.");
            sc.nextLine();
        }
    }

    private static void sendChatToGivenChannel(OauthZoomClient client) {
        OauthMessage oauth_message = new OauthMessage(client);
        System.out.println("Enter channel name: ");
        Scanner sc1 = new Scanner(System.in);
        String to_channel = sc1.nextLine();
        System.out.println("Enter message to send: ");
        Scanner sc2 = new Scanner(System.in);
        String message = sc2.nextLine();
        boolean response = oauth_message.sendChatToChannel(to_channel, message);
        if (response) {
            System.out.println("Message Sent.");
        } else {
            System.out.println("Message Not Sent.");
        }
    }

    private static void getChatHistory(OauthZoomClient client) {
        OauthMessage oauth_message = new OauthMessage(client);
        System.out.println("Enter chat channel: ");
        Scanner sc = new Scanner(System.in);
        String to_channel = sc.nextLine();
        System.out.println("Make sure start date and to date are no more than 5 days apart!");
        System.out.println("Enter start date (yyyy-mm-dd): ");
        Scanner sc1 = new Scanner(System.in);
        String from_date = sc1.nextLine();
        System.out.println("Enter end date (yyyy-mm-dd): ");
        Scanner sc2 = new Scanner(System.in);
        String to_date = sc2.nextLine();
        List<ChannelMessage> history_list = oauth_message.getChannelMessages(to_channel, from_date, to_date);
        int number = 0;
        for(ChannelMessage m:history_list){
            number++;
            System.out.println(number + ". " + m.toString());
        }
    }

    private static void searchEvent(OauthZoomClient client) {
        OauthMessage oauth_message = new OauthMessage(client);
        System.out.println("Enter chat channel: ");
        Scanner sc = new Scanner(System.in);
        String to_channel = sc.nextLine();
        System.out.println("Make sure start date and to date are no more than 5 days apart!");
        System.out.println("Enter start date (yyyy-mm-dd): ");
        Scanner sc2 = new Scanner(System.in);
        String from_date = sc2.nextLine();
        System.out.println("Enter end date (yyyy-mm-dd): ");
        Scanner sc3 = new Scanner(System.in);
        String to_date = sc3.nextLine();

        List<ChannelMessage> ans1 = oauth_message.searchEvent(to_channel, from_date, to_date, (message)->{
            if(message.getSender().contains("soobinchoi54+zoombot@gmail.com")) return true;
            else return false;
        });

        System.out.println("========== Search by Sender ===========");
        int number1 = 0;
        for(ChannelMessage event1:ans1){
            number1++;
            System.out.println(number1 + ". " + event1.toString());
        }

        List<ChannelMessage> ans2 = oauth_message.searchEvent(to_channel, from_date, to_date, (message)->{
            if(message.getMessage().contains("hello")) return true;
            else return false;
        });

        System.out.println("========== Search by Message String ===========");
        int number2 = 0;
        for(ChannelMessage event2:ans2){
            number2++;
            System.out.println(number2 + ". " + event2.toString());
        }
    }

    private void parse() {
        try {
            Properties p = new Properties();
            p.load(new FileInputStream("src/main/java/bots/bot.ini"));
            client_id = p.getProperty("client_id");
            client_secret = p.getProperty("client_secret");
            PORT = Integer.parseInt(p.getProperty("port"));
            browser_path = p.getProperty("browser_path");
//            redirect_url = p.getProperty("redirect_url");
            System.out.println("id: " + client_id + " browser: " + browser_path);

            String url = "http://localhost:4040/api/tunnels";
            HttpURLConnection conn = Util.httpRequest(url, "GET", 150);
            JSONObject response = Util.readResponse(conn);
            JSONArray arr = response.getJSONArray("tunnels");
            redirect_url = arr.getJSONObject(0).getString("public_url");
            System.out.println(redirect_url);
            conn.disconnect();

            /********************************************
             * connect to ngrok tunnel externally via url
             ********************************************/
            Desktop.getDesktop().browse(new URL(redirect_url).toURI());
            client = new OauthZoomClient(client_id, client_secret, PORT, redirect_url, browser_path);
            ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
            ChatMessagesComponent chat_messages = (ChatMessagesComponent) client.getChatMessages();
            JSONObject channels = chat_channels.listChannels(null);
            System.out.println(channels.toString());

        } catch (NumberFormatException | FileNotFoundException ne) {
            System.out.println("Number Format Exception: " + ne);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void printOptions() {
        System.out.println("Please type in your command with a valid numeric index(0 ~ 14): ");
        System.out.println("========== Channel Messages ===========");
        System.out.println("1. Send Chat to a Channel");
        System.out.println("2. Get Chat History");
        System.out.println("3. Search an Event");
        System.out.println("0. Exit");
    }
}