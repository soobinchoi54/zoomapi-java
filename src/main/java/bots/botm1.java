package bots;

import org.json.JSONObject;
import zoomapi.OauthZoomClient;
import zoomapi.components.ChatChannelsComponent;
import zoomapi.components.ChatMessagesComponent;
import zoomapi.components.UserComponent;
import zoomapi.utils.Util;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class botm1{

    String client_id;
    String client_secret;
    int PORT;
    String browser_path;
    String redirect_url;
    OauthZoomClient client;

    public static void main (String args[]) {
        botm1 ini = new botm1();
        ini.parse();

    }

    private void parse() {
        try {
            Properties p = new Properties();
            p.load(new FileInputStream("src/main/java/bots/bot.ini"));
            client_id = p.getProperty("client_id");
            client_secret = p.getProperty("client_secret");
            PORT = Integer.parseInt(p.getProperty("port"));
            browser_path = p.getProperty("browser_path");
            redirect_url = p.getProperty("redirect_url");
            System.out.println("id: " + client_id + " browser: " + browser_path);

            /********************************************
            * connect to ngrok tunnel extenrally via url
            ********************************************/
            Desktop.getDesktop().browse(new URL(redirect_url).toURI());
            client = new OauthZoomClient(client_id, client_secret, PORT, redirect_url, browser_path);
            ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
            ChatMessagesComponent chat_messages = (ChatMessagesComponent) client.getChatMessages();
            JSONObject channels = chat_channels.listChannels();
            System.out.println(channels.toString());

//            Map<String, String> data = new HashMap<>();
////            data.put("name", "Soob piggy");
////            data.put("type", "1");
////            JSONObject response = chat_channels.createChannel(data);
////            System.out.println(response.toString());

            Map<String, String> data = new HashMap<>();
            data.put("name", "Soobin Piggyyyyyyy");
            data.put("channelId", "115dc1f1-c600-458d-9b9d-8bae5b0eab46");
            JSONObject response = chat_channels.updateChannel(data);
            System.out.println(response.toString());

//            Map<String, String> params = new HashMap<>();
//            params.put("channelId", "63e36574-78a7-4d1c-a860-7b52bd3adf91");
//            JSONObject response2 = chat_channels.deleteChannel(params);
//            System.out.println(response2.toString());

//            Map<String, String> data = new HashMap<>();
//            data.put("messageId", "F405F4F0-A494-4A90-A8C1-5BEBB0B7E1E3");
//            data.put("to_contact", "soobinchoi54+zoombot@gmail.com");
//            data.put("message", "java fixed message");
//            JSONObject response = chat_messages.updateMessage(data);
//            System.out.println(response.toString());

        } catch (NumberFormatException | FileNotFoundException ne) {
            System.out.println("Number Format Exception: " + ne);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
