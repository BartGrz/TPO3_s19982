package zad1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.stream.Collectors;

/**
 * Admin class
 * sending messages to server , update/delete topics
 */
public class Admin   {
    @Getter
    @Setter
    private static String message;
    private static SocketChannel admin;


    public static void main(String[] args) {

        try {
             admin = SocketChannel.open(new InetSocketAddress("localhost", 8089));

            while (true) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO : trzeba dodac mozliwosc edycji(dodawania) i usuwania tematów
    public void start(Stage primaryStage) throws Exception {

        Pane pane = new Pane();
        TextField textField = new TextField();
        Button confirm = new Button("CONFIRM");

        ComboBox comboBox = new ComboBox();
        ComboBox operation = new ComboBox();

        ObservableList<String> pref = FXCollections.observableArrayList("politics","celebrities","sport","economy");
        ObservableList<String> possibleOperations = FXCollections.observableArrayList("SEND","DELETE","ADD");

        comboBox.getItems().addAll(pref);
        operation.getItems().addAll(possibleOperations);
        confirm.setLayoutX(330);
        comboBox.setLayoutX(150);
        operation.setLayoutX(250);

        pane.getChildren().addAll(textField, operation,comboBox,confirm);
        pane.setMaxSize(200,200);
        pane.setMaxHeight(200);
        pane.setMaxWidth(200);
        primaryStage.setScene(new Scene(pane, 400, 100));
        primaryStage.setResizable(false);
        primaryStage.setTitle("ADMIN");
        primaryStage.show();

        operation.setOnAction(event -> {
         if(operation.getValue().toString().equals("ADD")) {
             textField.setVisible(true);
             comboBox.setVisible(false);
             operation.setLayoutX(150);
             confirm.setLayoutX(250);
         }else if (operation.getValue().toString().equals("DELETE")) {
             textField.setVisible(false);
             confirm.setLayoutX(200);
             comboBox.setLayoutX(0);
             operation.setLayoutX(100);
         }else {
             validateCateogories(comboBox);
             textField.setVisible(true);
             comboBox.setVisible(true);
             confirm.setLayoutX(330);
             comboBox.setLayoutX(150);
             operation.setLayoutX(250);
         }
        });
        textField.setText(getMessage());
        confirm.setOnAction(event -> {
            try {
                switch (operation.getValue().toString()){
                    case "DELETE" :
                        writeMesage("admin;" + comboBox.getValue().toString() + ";" + operation.getValue().toString() + ";" + null);
                        break;
                    case "ADD" :
                        writeMesage("admin;" + null + ";" + operation.getValue().toString() + ";" + textField.getText());
                        break;
                    case "SEND":
                        writeMesage("admin;" + comboBox.getValue().toString() + ";" + operation.getValue().toString() + ";" + textField.getText());
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
    private static void writeMesage(String message) throws IOException {

        ByteBuffer buffer= ByteBuffer.wrap(message.getBytes());
        buffer.rewind();
        admin.write(buffer); //wysylanie do serwera
        buffer.clear();
    }
    private void validateCateogories (ComboBox comboBox){
        for(String s : Server.getListInfo().get(0).getActualCategories()){
            if(!comboBox.getItems().stream().anyMatch(o -> o.equals(s))) {
                comboBox.getItems().add(s);
            }else {
             comboBox.getItems().removeIf(o -> !o.equals(s));
            }
        }
    }
}
