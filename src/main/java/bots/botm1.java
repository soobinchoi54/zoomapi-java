package bots;

import lib.cache.databaseData.Channel;
import lib.cache.databaseData.ChannelMember;
import lib.cache.databaseData.ChannelMessage;
import lib.oauth.OauthChannel;
import lib.oauth.OauthMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import lib.clients.OauthZoomClient;
import lib.components.ChatChannelsComponent;
import lib.components.ChatMessagesComponent;
import lib.utils.Util;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Member;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class botm1{
    public static void main (String args[]){
        OauthBot bot1 = new OauthBot("src/main/java/bots/bot.ini");
        while (true) {
            printOptions();
            Scanner sc = new Scanner(System.in);
            String option = sc.nextLine();
            switch (option) {
                case "0":
                    return;
                case "1":
                    listUserChannels(bot1);
                    break;
                case "2":
                    createChannel(bot1);
                    break;
                case "3":
                    getChannel(bot1);
                    break;
                case "4":
                    updateChannel(bot1);
                    break;
                case "5":
                    deleteChannel(bot1);
                    break;
                case "6":
                    listChannelMembers(bot1);
                    break;
                case "7":
                    inviteChannelMembers(bot1);
                    break;
                case "8":
                    joinChannel(bot1);
                    break;
                case "9":
                    leaveChannel(bot1);
                    break;
                case "10":
                    removeMember(bot1);
                    break;
                case "11":
                    // listMessages(bot1);
                    break;
                case "12":
                    // sendMessage(bot1);
                    break;
                case "13":
                    // updateMessage(bot1);
                    break;
                case "14":
                    // deleteMessage(bot1);
                    break;
                default:
                    System.out.println("Please provide a valid input...\n");
                    break;
            }
        System.out.println("Press any to continue.");
        sc.nextLine();
        }
    }

    private static void listUserChannels(OauthBot bot) {
        OauthChannel oc = bot.getChannel();
        List<Channel> channels = null;
        System.out.println("Using cache? (1. no 2. yes)");
        Scanner sc = new Scanner(System.in);
        String option = sc.nextLine();

        if(option.equals("1")) channels = oc.listChannels();
        else if(option.equals("2")) channels = oc.listChannels(true);
        else {
            System.out.println("Please provide a valid input...");
            listUserChannels(bot);
            return;
        }
        System.out.println("=== All Channels ===");
        if(channels==null){
            System.out.println("failed");
            return;
        }
        for(int i = 0; i<channels.size(); i++){
            System.out.println(channels.get(i));
        }
    }

    private static void createChannel(OauthBot bot) {
        OauthChannel oc = bot.getChannel();
        System.out.println("Provide a name for your new channel: ");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        System.out.println("Provide a type for your new channel: ");
        System.out.println("1. Private & Invite only");
        System.out.println("2. Private & People from same organization invited only ");
        System.out.println("3. Public ");
        System.out.println("0. exit ");
        String type = sc.nextLine();
        Channel c = null;
        if (type.equals("0")) {
            return;
        } else if (type.equals("1")||type.equals("2")||type.equals("3")) {
            c = oc.createChannel(name, type);
            if(c!=null) {
                System.out.println("Channel created.");
                System.out.println(c.toString());
            }
            return;
        } else {
            System.out.println("Please provide a valid input...");
            return;
        }

    }

    private static void getChannel(OauthBot bot) {
        OauthChannel oc = bot.getChannel();
        System.out.println("Provide a valid Channel Name to get channel: ");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        System.out.println("Using cache? (1. no 2. yes)");
        String option = sc.nextLine();
        Channel c = null;
        if(option.equals("1")){
            c = oc.getChannel(name);
        }else if(option.equals("2")){
            c = oc.getChannel(name, true);
        }else {
            System.out.println("Please provide a valid input...");
            getChannel(bot);
            return;
        }
        if(c!=null) System.out.println(c.toString());
        else{
            System.out.println("Failed");
        }
    }

    private static void updateChannel(OauthBot bot) {
        OauthChannel oc = bot.getChannel();
        System.out.println("Provide a valid channel Name to update channel: ");
        Scanner sc = new Scanner(System.in);
        String cName = sc.nextLine();
        System.out.println("Enter new channel name: ");
        String newCName = sc.nextLine();

        Channel c = oc.updateChannel(cName, newCName);
        if(c!=null){
            System.out.println("Channel Updated.");
            System.out.println(c.toString());
        }else{
            System.out.println("Failed");
        }
    }

    private static void deleteChannel(OauthBot bot) {
        OauthChannel oc = bot.getChannel();
        System.out.println("Provide a valid channel name to delete channel: ");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        Channel c = oc.deleteChannel(name);
        if(c!=null){
            System.out.println("Channel Deleted.");
            System.out.println(c.toString());
        }else{
            System.out.println("Failed");
        }
    }

    private static void listChannelMembers(OauthBot bot) {
        OauthChannel oc = bot.getChannel();
        System.out.println("Provide a valid channel name to list current members: ");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        System.out.println("Using cache? (1. no 2. yes)");
        String option = sc.nextLine();
        List<ChannelMember> members = null;
        if(option.equals("1")){
            members = oc.listChannelMembers(name);
        }else if(option.equals("2")){
            members = oc.listChannelMembers(name, true);
        }else {
            System.out.println("Please provide a valid input...");
            getChannel(bot);
            return;
        }
        if(members!=null){
            System.out.println("=== All Members ===");
            for(int i=0; i<members.size(); i++){
                System.out.println(members.get(i));
            }
        }else{
            System.out.println("Failed");
        }
    }

    private static void inviteChannelMembers(OauthBot bot) {
        OauthChannel oc = bot.getChannel();
        System.out.println("Provide a valid channel Name to invite new members: ");
        Scanner sc = new Scanner(System.in);
        String cName = sc.nextLine();
        List<String> members = new ArrayList<>();
        int member_length = 0;
        while (member_length < 5) {
            System.out.println(String.format("Provide a valid email address to invite members (type 'stop' to stop adding). Current members %s: ", member_length+1));
            String member = sc.nextLine();
            if (member.toLowerCase().equals("stop")) {
                break;
            }else {
                members.add(member);
                member_length++;
            }
        }
        List<ChannelMember> res = oc.inviteChannelMembers(cName, (String[])members.toArray());
        if(res!=null){
            for(int i = 0; i<res.size();i++){
                System.out.println(res.get(i).toString());
            }
        }else{
            System.out.println("Failed");
        }
    }

    private static void joinChannel(OauthBot bot) {
        OauthChannel oc = bot.getChannel();
        System.out.println("Provide a valid channel ID of the channel that you want to join: ");
        Scanner sc = new Scanner(System.in);
        String cid = sc.nextLine();
        Channel c = oc.joinChannel(cid);
        if(c!=null){
            System.out.println("Succeed");
            System.out.println(c.toString());
        }else{
            System.out.println("Failed");
        }
    }

    private static void leaveChannel(OauthBot bot) {
        OauthChannel oc = bot.getChannel();
        System.out.println("Provide a valid channel name of the channel that you want to leave: ");
        Scanner sc = new Scanner(System.in);
        String name = sc.nextLine();
        Channel c = oc.leaveChannel(name);
        if(c!=null){
            System.out.println("Succeed");
            System.out.println(c.toString());
        }else{
            System.out.println("Failed");
        }

    }

    private static void removeMember(OauthBot bot) {
        OauthChannel oc = bot.getChannel();
        System.out.println("Provide a valid channel name of the channel to remove a member from: ");
        Scanner sc = new Scanner(System.in);
        String cName = sc.nextLine();
        System.out.println("Provide a valid member email of the member to remove: ");
        String email = sc.nextLine();
        ChannelMember cm = oc.removeMember(cName, email);
        if(cm!=null){
            System.out.println("Succeed");
            System.out.println(cm.toString());
        }else{
            System.out.println("Failed");
        }
    }
//    private static void listMessages(OauthBot bot) {
//        OauthMessage om = bot.getMessage();
//        System.out.println("List messages:");
//        System.out.println("1. Between you and a contact");
//        System.out.println("2. In a chat channel");
//        System.out.println("0. Exit");
//        Scanner sc = new Scanner(System.in);
//        String input = sc.nextLine();
//        switch (input) {
//            case "0": break;
//            case "1":
//                System.out.println("Enter a contact email: ");
//                String email = sc.nextLine();
//                System.out.println("Use cache? (1. no 2. yes) ");
//                String option = sc.nextLine();
//                List<ChannelMessage> messages = null;
//                if(option.equals("1")) messages = om.getChannelMessages()
//                System.out.println("=== All Messages ===");
//
//                break;
//            case "2":
//                JSONArray response = (JSONArray) chat_channels.listChannels(null).get("channels");
//                System.out.println("=== All Channels ===");
//                Iterator<Object> it = response.iterator();
//                while (it.hasNext()){
//                    System.out.println(it.next());
//                }
//                System.out.println("Enter a channel ID: ");
//                Scanner sc2 = new Scanner(System.in);
//                String cid = sc2.nextLine();
//                params.put("to_channel", cid);
//                params.put("userId", "me");
//                JSONArray response2 = (JSONArray) chat_messages.listMessages(params).get("messages");
//                System.out.println("=== All Messages ===");
//                Iterator<Object> it2 = response2.iterator();
//                while (it2.hasNext()){
//                    System.out.println(it2.next());
//                }
//                break;
//            default:
//                System.out.println("Pick from available options");
//                listMessages(client);
//        }
//    }
//
//    private static void sendMessage(OauthBot bot) {
//        ChatMessagesComponent chat_messages = (ChatMessagesComponent) client.getChatMessages();
//        ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
//        Map<String, String> params = new HashMap<>();
//        System.out.println("List messages:");
//        System.out.println("1. Between you and a contact");
//        System.out.println("2. In a chat channel");
//        System.out.println("0. Exit");
//        Scanner sc = new Scanner(System.in);
//        String input = sc.nextLine();
//        switch (input) {
//            case "0": break;
//            case "1":
//                System.out.println("Enter a contact email: ");
//                Scanner sc1 = new Scanner(System.in);
//                String email = sc1.nextLine();
//                params.put("to_contact", email);
//                System.out.println("Enter message: ");
//                Scanner sc2 = new Scanner(System.in);
//                String msg1 = sc2.nextLine();
//                params.put("message", msg1);
//                chat_messages.sendMessage(params);
//                break;
//            case "2":
//                JSONArray response = (JSONArray) chat_channels.listChannels(null).get("channels");
//                System.out.println("=== All Channels ===");
//                Iterator<Object> it = response.iterator();
//                while (it.hasNext()){
//                    System.out.println(it.next());
//                }
//                System.out.println("Enter a channel ID: ");
//                Scanner sc3 = new Scanner(System.in);
//                String cid = sc3.nextLine();
//                params.put("to_channel", cid);
//                System.out.println("Enter message: ");
//                Scanner sc4 = new Scanner(System.in);
//                String msg2 = sc4.nextLine();
//                params.put("message", msg2);
//                chat_messages.sendMessage(params);
//                break;
//            default:
//                System.out.println("Pick from available options");
//                sendMessage(client);
//        }
//    }
//
//    private static void updateMessage(OauthBot bot) {
//        ChatMessagesComponent chat_messages = (ChatMessagesComponent) client.getChatMessages();
//        ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
//        Map<String, String> params = new HashMap<>();
//        System.out.println("List messages:");
//        System.out.println("1. Between you and a contact");
//        System.out.println("2. In a chat channel");
//        System.out.println("0. Exit");
//        Scanner sc = new Scanner(System.in);
//        String input = sc.nextLine();
//        switch (input) {
//            case "0": break;
//            case "1":
//                System.out.println("Enter a contact email: ");
//                Scanner sc1 = new Scanner(System.in);
//                String email = sc1.nextLine();
//                params.put("to_contact", email);
//                params.put("userId", "me");
//                JSONArray response1 = (JSONArray) chat_messages.listMessages(params).get("messages");
//                System.out.println("=== All Messages ===");
//                Iterator<Object> it1 = response1.iterator();
//                while (it1.hasNext()){
//                    System.out.println(it1.next());
//                }
//                System.out.println("Enter message ID to edit a message: ");
//                Scanner sc2 = new Scanner(System.in);
//                String mid1 = sc2.nextLine();
//                params.put("messageId", mid1);
//                System.out.println("Enter message: ");
//                Scanner sc3 = new Scanner(System.in);
//                String msg1 = sc3.nextLine();
//                params.put("message", msg1);
//                chat_messages.updateMessage(params);
//                break;
//            case "2":
//                JSONArray response = (JSONArray) chat_channels.listChannels(null).get("channels");
//                System.out.println("=== All Channels ===");
//                Iterator<Object> it = response.iterator();
//                while (it.hasNext()){
//                    System.out.println(it.next());
//                }
//                System.out.println("Enter a channel ID: ");
//                Scanner sc4 = new Scanner(System.in);
//                String cid = sc4.nextLine();
//                params.put("to_channel", cid);
//                params.put("userId", "me");
//                JSONArray response2 = (JSONArray) chat_messages.listMessages(params).get("messages");
//                System.out.println("=== All Messages ===");
//                Iterator<Object> it2 = response2.iterator();
//                while (it2.hasNext()){
//                    System.out.println(it2.next());
//                }
//                System.out.println("Enter message ID to edit a message: ");
//                Scanner sc5 = new Scanner(System.in);
//                String mid2 = sc5.nextLine();
//                params.put("messageId", mid2);
//                System.out.println("Enter message: ");
//                Scanner sc6 = new Scanner(System.in);
//                String msg2 = sc6.nextLine();
//                params.put("message", msg2);
//                chat_messages.updateMessage(params);
//                break;
//            default:
//                System.out.println("Pick from available options");
//                updateMessage(client);
//        }
//    }
//
//    private static void deleteMessage(OauthBot bot) {
//        ChatMessagesComponent chat_messages = (ChatMessagesComponent) client.getChatMessages();
//        ChatChannelsComponent chat_channels = (ChatChannelsComponent) client.getChatChannels();
//        Map<String, String> params = new HashMap<>();
//        System.out.println("List messages:");
//        System.out.println("1. Between you and a contact");
//        System.out.println("2. In a chat channel");
//        System.out.println("0. Exit");
//        Scanner sc = new Scanner(System.in);
//        String input = sc.nextLine();
//        switch (input) {
//            case "0": break;
//            case "1":
//                System.out.println("Enter a contact email: ");
//                Scanner sc1 = new Scanner(System.in);
//                String email = sc1.nextLine();
//                params.put("to_contact", email);
//                params.put("userId", "me");
//                JSONArray response1 = (JSONArray) chat_messages.listMessages(params).get("messages");
//                System.out.println("=== All Messages ===");
//                Iterator<Object> it1 = response1.iterator();
//                while (it1.hasNext()){
//                    System.out.println(it1.next());
//                }
//                System.out.println("Enter message ID to delete a message: ");
//                Scanner sc2 = new Scanner(System.in);
//                String mid1 = sc2.nextLine();
//                params.put("messageId", mid1);
//                chat_messages.deleteMessage(params);
//                break;
//            case "2":
//                JSONArray response = (JSONArray) chat_channels.listChannels(null).get("channels");
//                System.out.println("=== All Channels ===");
//                Iterator<Object> it = response.iterator();
//                while (it.hasNext()){
//                    System.out.println(it.next());
//                }
//                System.out.println("Enter a channel ID: ");
//                Scanner sc3 = new Scanner(System.in);
//                String cid = sc3.nextLine();
//                params.put("to_channel", cid);
//                params.put("userId", "me");
//                JSONArray response2 = (JSONArray) chat_messages.listMessages(params).get("messages");
//                System.out.println("=== All Messages ===");
//                Iterator<Object> it2 = response2.iterator();
//                while (it2.hasNext()){
//                    System.out.println(it2.next());
//                }
//                System.out.println("Enter message ID to delete a message: ");
//                Scanner sc4 = new Scanner(System.in);
//                String mid2 = sc4.nextLine();
//                params.put("messageId", mid2);
//                chat_messages.deleteMessage(params);
//                break;
//            default:
//                System.out.println("Pick from available options");
//                deleteChannel(client);
//        }
//    }

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