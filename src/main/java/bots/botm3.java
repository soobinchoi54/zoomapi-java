package bots;

import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.OauthZoomClient;
import zoomapi.botAPIs.OauthMessage;
import zoomapi.components.ChatChannelsComponent;
import zoomapi.components.ChatMessagesComponent;
import zoomapi.components.UserComponent;
import zoomapi.utils.OauthEvent;
import zoomapi.utils.Util;

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
        OauthMessage messages = new OauthMessage(client);
        System.out.println("Enter channel name: ");
        Scanner sc1 = new Scanner(System.in);
        String to_channel = sc1.nextLine();
        System.out.println("Enter message to send: ");
        Scanner sc2 = new Scanner(System.in);
        String message = sc2.nextLine();
        boolean response = messages.sendChatToGivenChannel(to_channel, message);
        if (response) {
            System.out.println("Message Sent.");
        } else {
            System.out.println("Message Not Sent.");
        }
    }

    private static void getChatHistory(OauthZoomClient client) {
        OauthMessage message = new OauthMessage(client);
        System.out.println("Enter chat channel: ");
        Scanner sc = new Scanner(System.in);
        String to_channel = sc.nextLine();
        List<String> history_list = message.getChatHistory(to_channel);
        System.out.println(history_list);
    }

    private static void searchEvent(OauthZoomClient client) {

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
            ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
            ChatMessagesComponent chat_messages = (ChatMessagesComponent) client.getChatMessages();
            JSONObject channels = chat_channels.listChannels();
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
        System.out.println("========== Channels ===========");
        System.out.println("1. List User's Channels");
        System.out.println("2. Create a Channel");
        System.out.println("3. Get a Channel");
        System.out.println("4. Update a Channel");
        System.out.println("5. Delete a Channel");
        System.out.println("6. List Channel Members");
        System.out.println("7. Invite Channel Members");
        System.out.println("8. Join a Channel");
        System.out.println("9. Leave a Channel");
        System.out.println("10. Remove a Member");
        System.out.println("========== Messages ===========");
        System.out.println("11. List User's Chat Messages");
        System.out.println("12. Send a Chat Messages");
        System.out.println("13. Update a Message");
        System.out.println("14. Delete a Message");
        System.out.println("0. Exit");
    }
}
