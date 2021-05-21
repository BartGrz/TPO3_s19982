package dupadupa;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.*;

public class Client3 {
    @Getter
    @Setter
    static private String message;
    @Getter
    public static SocketChannel client;
    @Getter
    @Setter
    private String preferences = null;
    private static Map<String, String> messages = new HashMap<>();
    private LocalDateTime time = LocalDateTime.now();
    private static List<String> categoriesWWithMessages = new ArrayList<>();

    public static void main(String[] args) {

        try {
            client = SocketChannel.open(new InetSocketAddress("localhost", 8089));

            while (true) {

                ByteBuffer buffer = ByteBuffer.allocate(512);
                String mes = null;
                client.read(buffer); //odczyt od serwera
                buffer.rewind();
                buffer.clear();
                mes = new String(buffer.array()).trim();
                if (mes.length() > 0) {
                    setMessage(mes);
                    String[] received = filter();
                    messages.put(received[0], received[1]);
                    categoriesWWithMessages.add(received[0]);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void start(Stage primaryStage) throws Exception {
        Pane pane = new Pane();

        ComboBox comboBox = new ComboBox();
        ObservableList<String> pref = FXCollections.observableArrayList("celebryci", "kino", "Randki", "sport");
        comboBox.getItems().addAll(pref);
        Button button = new Button("REFRESH");
        Button subscribe = new Button("SUBSCRIBE");
        button.setLayoutY(40);
        comboBox.setLayoutY(80);
        subscribe.setLayoutY(120);
        pane.getChildren().addAll(button, comboBox, subscribe);
        pane.setMaxSize(200, 200);
        pane.setMaxHeight(200);
        pane.setMaxWidth(200);
        pane.setCenterShape(true);
        primaryStage.setScene(new Scene(pane, 200, 200));
        primaryStage.setResizable(false);
        primaryStage.show();

        button.setOnAction(event -> {
            if (getMessage() != null) {
                try {
                    popup();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        subscribe.setOnAction(event -> {
            try {

                String[] tab = this.getClient().getLocalAddress().toString().split(":");
                subscribe(comboBox.getValue().toString() + ";" + tab[1]);
                comboBox.setValue(null);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void subscribe(String sub) throws IOException {

        ByteBuffer buffer = null;
        buffer = ByteBuffer.wrap(sub.getBytes());
        buffer.rewind();
        this.client.write(buffer);
        buffer.clear();


    }

    private void popup() throws IOException {
        List<Label> labels = new ArrayList<>();
        String mes[] = getMessage().split(";");
        Stage stage = new Stage();
        Pane pane = new Pane();

        Label label = new Label(time.getHour() + ":" + time.getMinute() + " Message : ");
        Label info = new Label("You getting this message because you subsbscribe to topic ");
        info.setLayoutY(0);
        label.setLayoutY(20);

        int i = 0;
        for (Iterator<String> it = messages.keySet().iterator(); it.hasNext(); ) {

            String key = it.next();
            if (key.equals(categoriesWWithMessages.get(i))) {

                Label messageInfo = new Label(info.getText() + " " + key);
                Label message = new Label(label.getText() + " " + messages.get(key));

                messageInfo.setLayoutY(info.getLayoutY() + 20);
                message.setLayoutY(label.getLayoutY() + 20);

                labels.add(messageInfo);
                labels.add(message);

                info.setLayoutY(40);
                label.setLayoutY(60);
            }
            i += 1;
        }
        for (Label l : labels) {
            pane.getChildren().add(l);
        }
        //  pane.getChildren().addAll(label, info);
        stage.setScene(new Scene(pane, 400, 200));
        stage.setTitle("Message from Admin");
        stage.show();
    }

    private static String[] filter() {


        return getMessage().split(";");
    }

}