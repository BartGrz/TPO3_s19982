package myTest;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebHistory;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;


public class Admin   {
    @Getter
    @Setter
    private static String message;



    public static void main(String[] args) {

        try {
            SocketChannel admin = SocketChannel.open(new InetSocketAddress("localhost", 8089));
            Scanner scanner = new Scanner(System.in);
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (true) {

                    while (getMessage() == null) {
                        Thread.sleep(150);
                    }

                    buffer.put(getMessage().getBytes());
                    buffer.flip();
                    admin.write(buffer); //wysylanie do serwera
                    buffer.clear();
                    System.out.println("ADMIN sending message " + message);
                    setMessage(null);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void start(Stage primaryStage) throws Exception {

        Pane pane = new Pane();
        TextField textField = new TextField();
        Button button = new Button("SEND");
        button.setLayoutY(40);
        ComboBox comboBox = new ComboBox();
        ObservableList<String> pref = FXCollections.observableArrayList("celebryci", "kino");
        comboBox.getItems().addAll(pref);
        button.setLayoutY(40);
        comboBox.setLayoutY(80);

        pane.getChildren().addAll(textField, button,comboBox);
        pane.setMaxSize(200,200);
        pane.setMaxHeight(200);
        pane.setMaxWidth(200);
        primaryStage.setScene(new Scene(pane, 200, 200));
        primaryStage.setResizable(false);
        primaryStage.show();

        textField.setText(getMessage());
        button.setOnAction(event -> setMessage(textField.getText()));

    }
}
