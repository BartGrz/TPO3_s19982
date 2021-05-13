package myTest;

public class Main {

    public static void main(String[] args) {

        Thread thread_mainServer = new Thread(() -> new MainServer().main(new String[]{""}));
        Thread thread_client_1 = new Thread(() -> new Client().main(new String[]{""}));
        Thread thread_client2 = new Thread(() -> new Client().main(new String[]{""}));

        thread_mainServer.start();

        if(thread_mainServer.isAlive()) {
            thread_client2.start();
            thread_client_1.start();
        }


    }


}
