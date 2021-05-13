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
import netscape.javascript.JSObject;


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


public class Client {
    @Getter
    @Setter
    static private String message;

    private static List<String> preferences = new ArrayList<>();


    public static void main(String[] args) {

        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 8089));


            while (true) {


                    if (preferences.isEmpty()) {

                    } else {

                        byte [] tab = converArrayToByte(preferences);
                        buffer.limit(tab.length);
                        buffer.put(tab);
                        buffer.flip();
                        preferences.removeAll(preferences);
                        client.write(buffer);
                        buffer.clear();

                }

               // while (!buffer.hasRemaining()) {

              //  }
                String mes = null;
                    buffer.flip();
                client.read(buffer); //odczyt od serwera
                mes = new String(buffer.array()).trim();
                if (mes.length() != 0) {
                    // System.out.println("CLIENT received from server " + mes);
                    setMessage(mes);
                    buffer.clear();
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void start(Stage primaryStage) throws Exception {
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
        subscribe.setOnAction(event -> preferences.add(comboBox.getValue().toString()));


    }

    private static byte[] converArrayToByte(List<String> list) {

        StringBuilder stb = new StringBuilder();
        for (String s : list) {
            stb.append(s + " ");
        }

        return stb.toString().getBytes();
    }

}
