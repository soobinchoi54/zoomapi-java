package zoomapi;

public class JwtZoomClient extends ZoomClient{
    public JwtZoomClient(String client_id, String client_secret, int PORT, String redirect_url, String browser_path){
        super(client_id, client_secret);
    }
}
