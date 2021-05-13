package TestPack;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import zad1.AvailablePort;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;

public class TestClient extends Application   {


    static int port ;

    public TestClient(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Throwable {

        //Socket s = new Socket("localhost", port);
        Socket socket = new Socket("localhost", port);
        System.out.println("client" + port);
        ByteBuffer buf = ByteBuffer.allocate(256);
        SocketChannel socketChannel = SocketChannel.open(socket.getRemoteSocketAddress());
        socketChannel.configureBlocking(false);
        Selector selector = Selector.open();



        // time to read from the server

                StringBuilder stb = new StringBuilder();
                StringBuilder sb = new StringBuilder();
        System.out.println("INSIDE");
                buf.clear();
                int read = 0;
                while ((read = socketChannel.read(buf)) > 0) {
                    buf.flip();
                    byte[] bytes = new byte[buf.limit()];
                    buf.get(bytes);
                    sb.append(new String(bytes));
                    System.out.println("sb.toString()"+sb.toString());
                    buf.clear();
                }

                if (read < 0) {

                 //   socketChannel.close();
                } else {

                }


                System.out.print(sb.toString());

    }
        public void start(Stage primaryStage) throws Exception {

            Pane pane = new Pane();
            Button button = new Button();
            button.setLayoutY(40);
          //  TextField textField = new TextField();
            pane.getChildren().addAll(button); //textField
            primaryStage.setScene(new Scene(pane,100,100));
            primaryStage.setTitle("CLIENT");
            primaryStage.show();

           button.setOnAction(event -> {
               try {
                   main(new String[]{""});
               } catch (Throwable throwable) {
                   throwable.printStackTrace();
               }
           });


        }
}
