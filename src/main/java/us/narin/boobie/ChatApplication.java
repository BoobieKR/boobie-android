package us.narin.boobie;

import android.app.Application;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class ChatApplication extends Application {

    private Socket mSocket;
    private static final String SOCKET_ENDPOINT = "http://10.0.2.2:8080";

    {
        try {
            mSocket = IO.socket(SOCKET_ENDPOINT);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
