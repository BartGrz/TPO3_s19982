package dupadupa;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import zad1.AvailablePort;

import java.io.IOException;

public class Main extends Application {
    static int port;


    private static String[] tab;

    static {
        try {
            tab = new String[]{String.valueOf(AvailablePort.getAvailablePort())};
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Thread server = new Thread(() -> Server.main(tab));

        server.start();
        if (server.isAlive()) {
            launch(args);
        } else {
            server.start();
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pane = new Pane();

        Button button_client = new Button("client");
        Button button_admin = new Button("admin");
        button_client.setLayoutY(40);
        pane.getChildren().addAll(button_admin, button_client);
        button_admin.setOnAction(event -> {
            try {
                Thread admin = new Thread(() -> Admin.main(tab));
                Admin.start(new Stage());
                admin.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        button_client.setOnAction(event -> {
            Client client = new Client();
            Client2 client2 = new Client2();
            try {
                Thread clientStrart = new Thread(() -> client.main(tab));
                Thread client2Start = new Thread(() -> client2.main(tab));
                client.start(new Stage());
                client2.start(new Stage());
                client2Start.start();
                clientStrart.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        primaryStage.setScene(new Scene(pane, 100, 200));
        primaryStage.setResizable(false);
        primaryStage.show();
        if (!primaryStage.isShowing()) {

            Platform.exit();
        }
    }
}
