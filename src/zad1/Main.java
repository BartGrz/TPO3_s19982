package zad1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    static int port;


    private static String[] tab = new String[]{""};


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

                Thread adminStart = new Thread(() -> Admin.main(tab));
                Admin admin = new Admin();
                admin.start(new Stage());
                adminStart.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        button_client.setOnAction(event -> {

            Client client = new Client();
            Client2 client2 = new Client2();
            Client3 client3 = new Client3();

            try {
                Thread clientStrart = new Thread(() -> client.main(tab));
                Thread client2Start = new Thread(() -> client2.main(tab));
                Thread client3tart = new Thread(() -> client3.main(tab));
                clientStrart.start();
                client2Start.start();
                client3tart.start();
                client.start(new Stage());
                client2.start(new Stage());
                client3.start(new Stage());



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
