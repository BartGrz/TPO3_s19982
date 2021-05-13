package TestPack;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Admin  extends Application {

    private static int port;

    public Admin(int port) {
        this.port = port;
    }

    public Admin() {
    }

    public static void main(String[] args) throws IOException {

    //launch(args);

       // Selector admin = Selector.open();
            Socket socket = new Socket("localhost", port);
        System.out.println("adming" + port);
        SocketChannel socketChannel = SocketChannel.open(socket.getRemoteSocketAddress());
     //   socketChannel.register(admin, SelectionKey.OP_WRITE);
       socketChannel.configureBlocking(false);

            char tab[] = args[0].toCharArray();
            byte tab_byte[] = new byte[tab.length];
            for (int i = 0; i < tab.length; i++) {
                tab_byte[i] = (byte) tab[i];
            }
           socketChannel.write(ByteBuffer.wrap(tab_byte));

    }


    public void start(Stage primaryStage) throws Exception {

        Pane pane =new Pane();
        Button button = new Button("send");
        button.setLayoutY(40);
        TextField textField = new TextField();

        pane.getChildren().addAll(button,textField);
        primaryStage.setScene(new Scene(pane,100,100));
        primaryStage.setTitle("ADMIN");
        primaryStage.show();

        button.setOnAction(event -> {
            try {
                main(new String[]{textField.getText()});
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}
