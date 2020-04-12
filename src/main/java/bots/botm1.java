package bots;

import zoomapi.OauthZoomClient;

import java.io.FileInputStream;
import java.util.Properties;

public class botm1 extends OauthZoomClient {

    String client_id;
    String client_secret;
    int PORT;
    String browser_path;

    public static void main (String args[]) {
        botm1 ini = new botm1();
        ini.parse();

    }

    private void parse() {
        try {
            Properties p = new Properties();
            p.load(new FileInputStream("src/bots/bot.ini"));
            client_id = p.getProperty("client_id");
            client_secret = p.getProperty("client_secret");
            PORT = Integer.parseInt(p.getProperty("port"));
            browser_path = p.getProperty("browser_path");
            System.out.println("id: " + client_id + " browser: " + browser_path);
        } catch (NumberFormatException ne) {
            System.out.println("Number Format Exception: " + ne);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
    }
}
