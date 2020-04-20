package bots;

import org.json.JSONArray;
import org.json.JSONObject;
import zoomapi.OauthZoomClient;
import zoomapi.components.ChatChannelsComponent;
import zoomapi.components.ChatMessagesComponent;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class botm1{

    private static String client_id;
    private static String client_secret;
    private static int PORT;
    private static String browser_path;
    private static String redirect_url;
    private static OauthZoomClient client;
    static boolean stop;

    public static void main (String args[]) throws IOException {
        botm1 ini = new botm1();
        ini.parse();

        Scanner sc = new Scanner(System.in);
        String option = sc.nextLine();
        stop = false;
        while (!stop) {
            printOptions();

            switch (option) {
                case "0":
                    stop = true;
                    break;
                case "1":
                    listUserChannels(client);
                    break;
                case "2":
                    createChannel(client);
                    break;
                case "3":
                    getChannel(client);
                    break;
                case "4":
                    updateChannel(client);
                    break;
                case "5":
                    deleteChannel(client);
                    break;
                case "6":
                    listChannelMembers(client);
                    break;
                case "7":
                    inviteChannelMembers(client);
                    break;
                case "8":
                    joinChannel(client);
                    break;
                case "9":
                    leaveChannel(client);
                    break;
                case "10":
                    removeMember(client);
                    break;
                case "11":
                    listMessages(client);
                    break;
                case "12":
                    sendMessage(client);
                    break;
                case "13":
                    updateMessage(client);
                    break;
                case "14":
                    deleteMessage(client);
                    break;
                default:
                    System.out.println("Please provide a valid input...\n");
                    break;
            }
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
            redirect_url = p.getProperty("redirect_url");
            System.out.println("id: " + client_id + " browser: " + browser_path);

            /********************************************
             * connect to ngrok tunnel externally via url
             ********************************************/
            Desktop.getDesktop().browse(new URL(redirect_url).toURI());
            client = new OauthZoomClient(client_id, client_secret, PORT, redirect_url, browser_path);
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

    private static void listUserChannels(OauthZoomClient client) {
        ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
        JSONArray response = (JSONArray) chat_channels.listChannels().get("channels");
        System.out.println("=== All Channels ===");
        Iterator<Object> it = response.iterator();
        while (it.hasNext()){
            System.out.println(it.next());
        }
    }

    private static void createChannel(OauthZoomClient client) {
        ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
        Map<String, String> data = new HashMap<>();
        System.out.println("Provide a name for your new channel: ");
        Scanner sc1 = new Scanner(System.in);
        String name = sc1.nextLine();
        data.put("name", name);
        System.out.println("Provide a type for your new channel: ");
        System.out.println("1. Private & Invite only");
        System.out.println("2. Private & People from same organization invited only ");
        System.out.println("3. Public ");
        System.out.println("0. exit ");
        Scanner sc2 = new Scanner(System.in);
        String type = sc2.nextLine();
        String members = null;
        while (true) {
            if (type.equals("0")) {
                break;
            } else if (type.equals("1")||type.equals("2")||type.equals("3")) {
                data.put("type",type);
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                int member_length = 0;
                while (member_length < 5) {
                    System.out.println(String.format("Provide a valid email address to invite members (type 'stop' to stop adding). Current members %s: ", member_length+1));
                    Scanner sc = new Scanner(System.in);
                    String member = sc.nextLine();
                    if (member.toLowerCase().equals("stop")) {
                        break;
                    }else {
                        if(member_length == 0) sb.append("email:"+member);
                        else sb.append(",email:"+member);
                        member_length++;
                    }
                }
                sb.append("}");
                members = sb.toString();
                data.put("members", members);
            } else {
                System.out.println("Please provide a valid input...");
                createChannel(client);
            }
        }
        if((!members.equals("{}")) || (members != null)) {
            chat_channels.createChannel(data);
            System.out.println("Channel created");
        } else {
            System.out.println("Channel created without members.");
        }
    }

    private static void getChannel(OauthZoomClient client) {
        ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
        Map<String,String> params = new HashMap<>();
        System.out.println("Provide a valid Channel ID to get channel: ");
        Scanner sc = new Scanner(System.in);
        String cid = sc.nextLine();
        params.put("channelId", cid);
        JSONObject response = (JSONObject) chat_channels.getChannel(params).get("channelId");
        System.out.println(response.toString());
    }

    private static void updateChannel(OauthZoomClient client) {
        ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
        Map<String, String> data = new HashMap<>();
        System.out.println("Provide a valid channel ID to update channel: ");
        Scanner sc1 = new Scanner(System.in);
        String cid = sc1.nextLine();
        System.out.println("Enter new channel name: ");
        Scanner sc2 = new Scanner(System.in);
        String name = sc2.nextLine();
        data.put("channelId", cid);
        data.put("name", name);
        chat_channels.updateChannel(data);
        System.out.println("Channel Updated.");
    }

    private static void deleteChannel(OauthZoomClient client) {
        ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
        Map<String, String> params = new HashMap<>();
        System.out.println("Provide a valid channel ID to delete channel: ");
        Scanner sc = new Scanner(System.in);
        String cid = sc.nextLine();
        params.put("channelId", cid);
        chat_channels.deleteChannel(params);
    }

    private static void listChannelMembers(OauthZoomClient client) {
        ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
        Map<String, String> params = new HashMap<>();
        System.out.println("Provide a valid channel ID to list current members: ");
        Scanner sc = new Scanner(System.in);
        String cid = sc.nextLine();
        params.put("channelId", cid);
        chat_channels.listChannelMembers(params);
        JSONArray response = (JSONArray)chat_channels.listChannelMembers(params).get("members");
        System.out.println("=== All Members ===");
        Iterator<Object> it = response.iterator();
        while (it.hasNext()){
            System.out.println(it.next());
        }
    }

    private static void inviteChannelMembers(OauthZoomClient client) {
        ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
        Map<String, String> params = new HashMap<>();
        System.out.println("Provide a valid channel ID to invite new members: ");
        Scanner sc = new Scanner(System.in);
        String cid = sc.nextLine();
        params.put("channelId", cid);
        String members;
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int member_length = 0;
        while (member_length < 5) {
            System.out.println(String.format("Provide a valid email address to invite members (type 'stop' to stop adding). Current members %s: ", member_length+1));
            Scanner sc1 = new Scanner(System.in);
            String member = sc1.nextLine();
            if (member.toLowerCase().equals("stop")) {
                break;
            }else {
                if(member_length == 0) sb.append("email:"+member);
                else sb.append(",email:"+member);
                member_length++;
            }
        }
        sb.append("}");
        members = sb.toString();
        params.put("members", members);
        chat_channels.inviteChannelMembers(params);
    }

    private static void joinChannel(OauthZoomClient client) {
        ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
        Map<String, String> data = new HashMap<>();
        System.out.println("Provide a valid channel ID of the channel that you want to join: ");
        Scanner sc = new Scanner(System.in);
        String cid = sc.nextLine();
        data.put("channelId", cid);
        chat_channels.joinChannel(data);
    }

    private static void leaveChannel(OauthZoomClient client) {
        ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
        Map<String, String> params = new HashMap<>();
        System.out.println("Provide a valid channel ID of the channel that you want to leave: ");
        Scanner sc = new Scanner(System.in);
        String cid = sc.nextLine();
        params.put("channelId", cid);
        chat_channels.leaveChannel(params);
    }

    private static void removeMember(OauthZoomClient client) {
        ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
        Map<String, String> params = new HashMap<>();
        System.out.println("Provide a valid channel ID of the channel to remove a member from: ");
        Scanner sc1 = new Scanner(System.in);
        String cid = sc1.nextLine();
        params.put("channelId", cid);
        System.out.println("Provide a valid member ID of the member to remove: ");
        Scanner sc2 = new Scanner(System.in);
        String mid = sc2.nextLine();
        params.put("memberId", mid);
        chat_channels.removeMember(params);
    }

    private static void listMessages(OauthZoomClient client) {
        ChatMessagesComponent chat_messages = (ChatMessagesComponent) client.getChatMessages();
        Map<String, String> params = new HashMap<>();
        System.out.println("List messages:");
        System.out.println("1. Between you and a contact");
        System.out.println("2. In a chat channel");
        System.out.println("0. Exit");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        switch (input) {
            case "0": break;
            case "1":
                System.out.println("Enter a contact email: ");
                Scanner sc1 = new Scanner(System.in);
                String email = sc1.nextLine();
                params.put("to_contact", email);
                JSONArray response1 = (JSONArray) chat_messages.listMessages(params).get("messages");
                System.out.println("=== All Messages ===");
                Iterator<Object> it1 = response1.iterator();
                while (it1.hasNext()){
                    System.out.println(it1.next());
                }
                break;
            case "2":
                System.out.println("Enter a channel ID: ");
                Scanner sc2 = new Scanner(System.in);
                String cid = sc2.nextLine();
                params.put("to_channel", cid);
                JSONArray response2 = (JSONArray) chat_messages.listMessages(params).get("messages");
                System.out.println("=== All Messages ===");
                Iterator<Object> it2 = response2.iterator();
                while (it2.hasNext()){
                    System.out.println(it2.next());
                }
                break;
            default:
                System.out.println("Pick from available options");
                listMessages(client);
        }
    }

    private static void sendMessage(OauthZoomClient client) {
        ChatMessagesComponent chat_messages = (ChatMessagesComponent) client.getChatMessages();
        Map<String, String> params = new HashMap<>();
        System.out.println("List messages:");
        System.out.println("1. Between you and a contact");
        System.out.println("2. In a chat channel");
        System.out.println("0. Exit");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        switch (input) {
            case "0": break;
            case "1":
                System.out.println("Enter a contact email: ");
                Scanner sc1 = new Scanner(System.in);
                String email = sc1.nextLine();
                params.put("to_contact", email);
                System.out.println("Enter message: ");
                Scanner sc2 = new Scanner(System.in);
                String msg1 = sc2.nextLine();
                params.put("message", msg1);
                chat_messages.sendMessage(params);
                break;
            case "2":
                System.out.println("Enter a channel ID: ");
                Scanner sc3 = new Scanner(System.in);
                String cid = sc3.nextLine();
                params.put("to_channel", cid);
                System.out.println("Enter message: ");
                Scanner sc4 = new Scanner(System.in);
                String msg2 = sc4.nextLine();
                params.put("message", msg2);
                chat_messages.sendMessage(params);
                break;
            default:
                System.out.println("Pick from available options");
                sendMessage(client);
        }
    }

    private static void updateMessage(OauthZoomClient client) {
        ChatMessagesComponent chat_messages = (ChatMessagesComponent) client.getChatMessages();
        Map<String, String> params = new HashMap<>();
        System.out.println("List messages:");
        System.out.println("1. Between you and a contact");
        System.out.println("2. In a chat channel");
        System.out.println("0. Exit");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        switch (input) {
            case "0": break;
            case "1":
                System.out.println("Enter a contact email: ");
                Scanner sc1 = new Scanner(System.in);
                String email = sc1.nextLine();
                params.put("to_contact", email);
                JSONArray response1 = (JSONArray) chat_messages.listMessages(params).get("messages");
                System.out.println("=== All Messages ===");
                Iterator<Object> it1 = response1.iterator();
                while (it1.hasNext()){
                    System.out.println(it1.next());
                }
                System.out.println("Enter message ID to edit a message: ");
                Scanner sc2 = new Scanner(System.in);
                String mid1 = sc2.nextLine();
                params.put("messageId", mid1);
                System.out.println("Enter message: ");
                Scanner sc3 = new Scanner(System.in);
                String msg1 = sc3.nextLine();
                params.put("message", msg1);
                chat_messages.updateMessage(params);
                break;
            case "2":
                System.out.println("Enter a channel ID: ");
                Scanner sc4 = new Scanner(System.in);
                String cid = sc4.nextLine();
                params.put("to_channel", cid);
                JSONArray respone2 = (JSONArray) chat_messages.listMessages(params).get("messages");
                System.out.println("=== All Messages ===");
                Iterator<Object> it2 = respone2.iterator();
                while (it2.hasNext()){
                    System.out.println(it2.next());
                }
                System.out.println("Enter message ID to edit a message: ");
                Scanner sc5 = new Scanner(System.in);
                String mid2 = sc5.nextLine();
                params.put("messageId", mid2);
                System.out.println("Enter message: ");
                Scanner sc6 = new Scanner(System.in);
                String msg2 = sc6.nextLine();
                params.put("message", msg2);
                chat_messages.updateMessage(params);
                break;
            default:
                System.out.println("Pick from available options");
                updateMessage(client);
        }
    }

    private static void deleteMessage(OauthZoomClient client) {
        ChatMessagesComponent chat_messages = (ChatMessagesComponent) client.getChatMessages();
        Map<String, String> params = new HashMap<>();
        System.out.println("List messages:");
        System.out.println("1. Between you and a contact");
        System.out.println("2. In a chat channel");
        System.out.println("0. Exit");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        switch (input) {
            case "0": break;
            case "1":
                System.out.println("Enter a contact email: ");
                Scanner sc1 = new Scanner(System.in);
                String email = sc1.nextLine();
                params.put("to_contact", email);
                JSONArray response1 = (JSONArray) chat_messages.listMessages(params).get("messages");
                System.out.println("=== All Messages ===");
                Iterator<Object> it1 = response1.iterator();
                while (it1.hasNext()){
                    System.out.println(it1.next());
                }
                System.out.println("Enter message ID to delete a message: ");
                Scanner sc2 = new Scanner(System.in);
                String mid1 = sc2.nextLine();
                params.put("messageId", mid1);
                chat_messages.deleteMessage(params);
                break;
            case "2":
                System.out.println("Enter a channel ID: ");
                Scanner sc3 = new Scanner(System.in);
                String cid = sc3.nextLine();
                params.put("to_contact", cid);
                JSONArray response2 = (JSONArray) chat_messages.listMessages(params).get("messages");
                System.out.println("=== All Messages ===");
                Iterator<Object> it2 = response2.iterator();
                while (it2.hasNext()){
                    System.out.println(it2.next());
                }
                System.out.println("Enter message ID to delete a message: ");
                Scanner sc4 = new Scanner(System.in);
                String mid2 = sc4.nextLine();
                params.put("messageId", mid2);
                chat_messages.deleteMessage(params);
                break;
            default:
                System.out.println("Pick from available options");
                deleteChannel(client);
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
