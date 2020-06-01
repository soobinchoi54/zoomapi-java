package bots;

import lib.clients.OauthZoomClient;
import lib.oauth.OauthChannel;
import lib.oauth.OauthMessage;
import lib.utils.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

class OauthBot{
    private String client_id;
    private String client_secret;
    private int PORT;
    private String browser_path;
    private String redirect_url;
    private OauthZoomClient client;
    private OauthChannel oauthChannel;
    private OauthMessage oauthMessage;

    public OauthBot(String path){
        parse(path);
        refresh();
    }
    private void parse(String path) {
        try {
            Properties p = new Properties();
            p.load(new FileInputStream(path));
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
        } catch (NumberFormatException | FileNotFoundException ne) {
            System.out.println("Number Format Exception: " + ne);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void refresh(){
        this.oauthChannel = new OauthChannel(this.client);
        this.oauthMessage = new OauthMessage(this.client);
    }

    public OauthZoomClient getClient(){
        return this.client;
    }

    public OauthChannel getChannel(){
        return this.oauthChannel;
    }

    public OauthMessage getMessage(){
        return this.oauthMessage;
    }
}