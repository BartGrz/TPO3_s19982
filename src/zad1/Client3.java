package zad1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Client3 {
    @Getter
    @Setter
    static private String message;
    @Getter
    public static SocketChannel client;
    private static Map<String, String> messages = new HashMap<>();
    private List<String> subscibedTopics = new ArrayList<>();


    public static void main(String[] args) {

        try {
            client = SocketChannel.open(new InetSocketAddress("localhost", 8089));

            while (true) {

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                String mes = null;
                client.read(buffer); //odczyt od serwera
                buffer.rewind();
                buffer.clear();
                mes = new String(buffer.array()).trim();
                if (mes.length() > 0) {
                    setMessage(mes);
                    String[] received = filter();
                    messages.put(received[0], received[1]);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void start(Stage primaryStage) throws Exception {
        Pane pane = new Pane();
        ComboBox comboBox = new ComboBox();
        ObservableList<String> pref = FXCollections.observableArrayList(Server.getListInfo().get(0).getActualCategories());
        comboBox.getItems().addAll(pref);
        Button refresh = new Button("REFRESH");
        Button subscribe = new Button("SUBSCRIBE");
        Button unsubscribe = new Button("UNSUBSCRIBE");
        Button subscibedTopics = new Button("TOPICS");
        refresh.setLayoutY(40);
        comboBox.setLayoutY(80);
        subscribe.setLayoutY(120);
        subscibedTopics.setLayoutY(160);
        unsubscribe.setLayoutY(120);
        unsubscribe.setLayoutX(80);
        unsubscribe.setVisible(false);
        pane.getChildren().addAll(refresh, comboBox, subscribe, subscibedTopics, unsubscribe);
        pane.setMaxSize(200, 200);
        pane.setMaxHeight(200);
        pane.setMaxWidth(200);
        pane.setCenterShape(true);
        primaryStage.setScene(new Scene(pane, 200, 200));
        primaryStage.setResizable(false);
        primaryStage.show();

        refresh.setOnAction(event -> {

            try {
                popupWIthMessage();
                validateCateogories(comboBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        subscribe.setOnAction(event -> {

            try {
                String[] tab = client.getLocalAddress().toString().split(":");
                if (comboBox.getValue() == null) {
                    Pane paneError = new Pane();
                    Label label = new Label("you need to choose topic from list");
                    paneError.getChildren().add(label);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(paneError, 200, 100));
                    stage.show();

                } else {
                    subscribe(comboBox.getValue().toString() + ";" + tab[1]);
                    comboBox.setValue(null);
                    unsubscribe.setVisible(true);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        subscibedTopics.setOnAction(event -> {
            try {
                subscribedElements();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        unsubscribe.setOnAction(event -> {
            try {
                int port = Integer.parseInt(client.getLocalAddress().toString().split(":")[1]);

                if (comboBox.getValue() == null) {
                    Pane paneError = new Pane();
                    Label label = new Label("you need to choose topic from list");
                    paneError.getChildren().add(label);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(paneError, 200, 100));
                    stage.show();
                } else if (!Server.getListInfo().get(0).getSet()
                        .get(port)
                        .stream()
                        .filter(s -> s.equals(comboBox.getValue().toString()))
                        .findAny()
                        .isPresent()) {
                    Pane paneError = new Pane();
                    Label label = new Label("you are not subsribed to this topic, operation foribidden");
                    paneError.getChildren().add(label);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(paneError, 200, 100));
                    stage.show();
                } else {
                    deleteTopicFromList(comboBox.getValue().toString());
                }
                Thread.sleep(20);
                unsubscribe.setVisible(!validate());
                comboBox.setValue(null);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * enables sending message to server with category client want to subsribe to
     * @param sub
     * @throws IOException
     */
    private void subscribe(String sub) throws IOException {
        if (sub == null) {

        }
        String splitted[] = sub.split(";");

        subscibedTopics.add(splitted[0]);
        ByteBuffer buffer = null;
        buffer = ByteBuffer.wrap(sub.getBytes());
        buffer.rewind();
        client.write(buffer);
        buffer.clear();
    }

    /**
     * showing whole history of messages sended by admin managed by server
     * @using map messages as a container
     * @throws IOException
     */
    private void popupWIthMessage() throws IOException {
        LocalDateTime time = LocalDateTime.now();
        time.format(DateTimeFormatter.BASIC_ISO_DATE);
        int port = Integer.parseInt(client.getLocalAddress().toString().split(":")[1]);
        List<Label> labels = new ArrayList<>();

        Stage stage = new Stage();
        Pane pane = new Pane();

        Label label = new Label(time.getHour() + ":" + time.getMinute() + ":" + time.getSecond() + " Message : ");
        Label info = new Label("You getting this message because you subsbscribe to topic ");
        info.setLayoutY(0);
        label.setLayoutY(20);

        int i = 0;
        for (Iterator<String> it = messages.keySet().iterator(); it.hasNext(); ) {

            String key = it.next();
            if (Server.getListInfo().get(0).getSet().get(port).stream().anyMatch(s -> s.equals(key))) {


                Label messageInfo = new Label(info.getText() + " " + key);
                Label message = new Label(label.getText() + " " + messages.get(key));

                messageInfo.setLayoutY(info.getLayoutY() + 20);
                message.setLayoutY(label.getLayoutY() + 20);

                labels.add(messageInfo);
                labels.add(message);

                info.setLayoutY(messageInfo.getLayoutY() + 20);
                label.setLayoutY(message.getLayoutY() + 20);
                i += 1;
            }
        }
        if (labels.isEmpty()) {
            label.setText("there are no messages available at the moment for topics you subsribed to");
            labels.add(label);
        }
        for (Label l : labels) {
            pane.getChildren().add(l);
        }

        stage.setScene(new Scene(pane, 400, 200));
        stage.setTitle("Message from Admin");
        stage.show();

    }

    /**
     * @using method showTopicsClientIsSubscribedTo(int port) from info class
     * showing all topics client is subsribed to
     * @throws IOException
     */
    private void subscribedElements() throws IOException {

        int port = Integer.parseInt(client.getLocalAddress().toString().split(":")[1]);
        Stage stage = new Stage();
        Pane pane = new Pane();
        Label info = new Label();
        pane.getChildren().add(info);

        if (Server.getListInfo().get(0).getSet().values().stream().findAny().isPresent()) {
            info.setText("subsribed topics : " + Server.getListInfo()
                    .get(0)
                    .showTopicsClientIsSubscribedTo(port)
                    .stream()
                    .collect(Collectors.joining(", ")));

        } else {
            info.setText("you did not subsribed to any topics available");
        }
        stage.setScene(new Scene(pane, 200, 100));
        stage.show();

    }

    /**
     * sending request to unsubsribed from choosen topic
     * @param category
     * @throws IOException
     */
    private void deleteTopicFromList(String category) throws IOException {

        ByteBuffer buffer = null;
        String message = category + ";" + "delete";
        buffer = ByteBuffer.wrap(message.getBytes());
        buffer.rewind();
        client.write(buffer);
        buffer.clear();

    }

    /**
     * method linked directly with unsubsribe button,
     * button will be visible only if client is subsribed to any topic
     * @return
     * @throws IOException
     */
    private boolean validate() throws IOException {
        int port = Integer.parseInt(client.getLocalAddress().toString().split(":")[1]);
        return Server.getListInfo().get(0).getSet()
                .get(port)
                .isEmpty();
    }

    private static String[] filter() {
        return getMessage().split(";");
    }

    /**
     * method linked with comboBox field,
     * checking if client list of topics is equal to possible topics in class info managed by server
     * @param comboBox
     */
    private void validateCateogories(ComboBox comboBox) {
        for (String s : Server.getListInfo().get(0).getActualCategories()) {
            if (!comboBox.getItems().stream().anyMatch(o -> o.equals(s))) {
                comboBox.getItems().add(s);
            } else {
                comboBox.getItems().removeIf(o -> !o.equals(s));
            }
        }
    }
}