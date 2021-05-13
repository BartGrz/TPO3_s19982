package zad1;

import javax.sound.sampled.Port;
import java.io.IOException;
import java.net.ServerSocket;

public class AvailablePort {


    public static int getAvailablePort() throws IOException {

        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        serverSocket.close();

        return port;



    }

}
