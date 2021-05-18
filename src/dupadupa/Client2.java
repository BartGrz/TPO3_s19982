package dupadupa;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import netscape.javascript.JSObject;


import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collector;


public class Client2 {
    @Getter
    @Setter
    static private String message;
    @Getter
    public static SocketChannel client;
    @Getter
    @Setter
    private String preferences = null;

    public static List<String> preferencesList = new ArrayList<>();
    Info info = new Info();

    public static void main(String[] args) {
        StringBuilder stb = new StringBuilder();
        try {
            client = SocketChannel.open(new InetSocketAddress("localhost", 8089));
/*
            ByteBuffer buf = ByteBuffer.wrap("ping".getBytes());
            buf.rewind();
            client.write(buf);
            buf.clear();

*/
            while (true) {

                ByteBuffer buffer = ByteBuffer.allocate(256);
                String mes = null;
                client.read(buffer); //odczyt od serwera
                buffer.rewind();
                buffer.clear();
                mes = new String(buffer.array()).trim();
                if (mes.length() > 0) {
                    setMessage(mes);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void start(Stage primaryStage) throws Exception {
        Pane pane = new Pane();
        TextField textField = new TextField();
        ComboBox comboBox = new ComboBox();
        ObservableList<String> pref = FXCollections.observableArrayList("celebryci", "kino");
        comboBox.getItems().addAll(pref);
        Button button = new Button("REFRESH");
        Button subscribe = new Button("SUBSCRIBE");
        button.setLayoutY(40);
        comboBox.setLayoutY(80);
        subscribe.setLayoutY(120);
        pane.getChildren().addAll(textField, button, comboBox, subscribe);
        pane.setMaxSize(200, 200);
        pane.setMaxHeight(200);
        pane.setMaxWidth(200);
        pane.setCenterShape(true);
        primaryStage.setScene(new Scene(pane, 200, 200));
        primaryStage.setResizable(false);
        primaryStage.show();

        button.setOnAction(event -> textField.setText(getMessage()));
        subscribe.setOnAction(event -> {
            try {

                String[] tab = this.getClient().getLocalAddress().toString().split(":");
                subscribe(comboBox.getValue().toString() + " " + tab[1]);
                comboBox.setValue(null);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void subscribe(String sub) throws IOException {


        this.preferencesList.add(sub);
        ByteBuffer buffer = null;
        for (int i = 0; i < preferencesList.size(); i++) {
            buffer = ByteBuffer.wrap(preferencesList.get(i).getBytes());
        }
        buffer.rewind();
        this.client.write(buffer);
        buffer.clear();

        // return;


    }

}