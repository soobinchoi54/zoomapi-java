package bots;

import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.OauthZoomClient;
import zoomapi.botAPIs.OauthMessage;
import zoomapi.botAPIs.subscribe.ChannelObserver;
import zoomapi.botAPIs.subscribe.SubscribeAgency;
import zoomapi.components.ChatChannelsComponent;
import zoomapi.components.ChatMessagesComponent;
import zoomapi.utils.Message;
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

public class botm4 {

    private static String client_id;
    private static String client_secret;
    private static int PORT;
    private static String browser_path;
    private static String redirect_url;
    private static OauthZoomClient client;

    public static void main (String args[]) throws IOException {
        botm4 ini = new botm4();
        ini.parse();
    }

    private static void sendChatToGivenChannel(OauthZoomClient client) {
        OauthMessage oauth_message = new OauthMessage(client);
        System.out.println("Enter channel name: ");
        Scanner sc1 = new Scanner(System.in);
        String to_channel = sc1.nextLine();
        System.out.println("Enter message to send: ");
        Scanner sc2 = new Scanner(System.in);
        String message = sc2.nextLine();
        boolean response = oauth_message.sendChatToGivenChannel(to_channel, message);
        if (response) {
            System.out.println("Message Sent.");
        } else {
            System.out.println("Message Not Sent.");
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
            SubscribeAgency.initialize();
            ChannelObserver observer = new ChannelObserver("Soobin on new messages", client, "updated channel");
            observer.setObservingDate("2020-05-10", "2020-05-10");
            observer.subscribeTo(SubscribeAgency.NOTIFY_NEW_MESSAGES);

            ChannelObserver observer2 = new ChannelObserver("Duo on updates", client, "updated channel");
            observer2.setObservingDate("2020-05-10", "2020-05-10");
            observer2.subscribeTo(SubscribeAgency.NOTIFY_MESSAGE_UPDATES);

            ChannelObserver observer3 = new ChannelObserver("Soobin on new members", client, "updated channel");
            observer3.setObservingDate("2020-05-10", "2020-05-10");
            observer3.subscribeTo(SubscribeAgency.NOTIFY_NEW_MEMBERS);

        } catch (NumberFormatException | FileNotFoundException ne) {
            System.out.println("Number Format Exception: " + ne);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}