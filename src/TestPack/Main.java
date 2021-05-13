package TestPack;

import anothertest.NiochatServer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import zad1.AvailablePort;
import zad1.Client;
import zad1.CrunchifyNIOServer;

import java.io.IOException;

public class Main extends Application {
   static int port;

    static {
        try {
            port = AvailablePort.getAvailablePort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  static   String passPort = String.valueOf(port);

    public static void main(String[] args) throws IOException {




        int port_admin = AvailablePort.getAvailablePort();

        String passPort_admin = String.valueOf(port_admin);
        launch(args);
        /*
        Thread thread = new Thread(() -> {
            try {
                 MainServer.main(new String[]{passPort});
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        thread.start();

        Thread thread_client= new Thread(() -> {
            try {
                new TestClient(port).main(new String[]{passPort});
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        Thread thread_client_2= new Thread(() -> {
            try {
                new TestClient(port).main(new String[]{passPort});
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        if(thread.isAlive()) {

        //    thread_client.start();
         //   thread_client_2.start();

        }

         */
    }


    public void start(Stage primaryStage) throws Exception {
       // int port = AvailablePort.getAvailablePort();

        Pane pane = new Pane();
        Button button = new Button("client");
        Button button_admin  = new Button("admin");
        button_admin.setLayoutY(40);

        pane.getChildren().addAll(button,button_admin);
        primaryStage.setScene(new Scene(pane,100,100));
        primaryStage.setTitle("MAIN");
        primaryStage.show();

        try {
            NiochatServer.main(new String[]{passPort});
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        button.setOnAction(event -> {
            try {
                new TestClient(port).start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        button_admin.setOnAction(event -> {
            try {
                new Admin(port).start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
