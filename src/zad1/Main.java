package zad1;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        int port = AvailablePort.getAvailablePort();
        int port_admin = AvailablePort.getAvailablePort();
        String passPort = String.valueOf(port);
        String passPort_admin = String.valueOf(port_admin);

        Thread thread = new Thread(() -> {
            try {
                new CrunchifyNIOServer().main(new String[]{passPort,passPort_admin});
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread thread_client= new Thread(() -> {
            try {
                new Client().main(new String[]{passPort});
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread thread_client_2= new Thread(() -> {
            try {
                new Client().main(new String[]{passPort});
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread thread_admin= new Thread(() -> {
            try {
                new Admin().main(new String[]{passPort_admin});
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();
        if(thread.isAlive()) {
            thread_client.start();
            thread_admin.start();
         //   thread_client_2.start();
        }
    }

}
