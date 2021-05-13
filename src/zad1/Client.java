package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {


    public static void main(String[] args) throws InterruptedException, IOException {
        Scanner in = new Scanner(System.in);
        int port = Integer.parseInt(args[0]);
        InetSocketAddress crunchifyAddr = new InetSocketAddress("localhost", port);
        SocketChannel crunchifyClient = SocketChannel.open(crunchifyAddr);

        log("Connecting to Server on port " +port + "...");

        ArrayList<String> companyDetails = new ArrayList<String>();


        while (true) {
            in = new Scanner(System.in);
            String mes = in.nextLine();


            byte[] message = mes.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            crunchifyClient.write(buffer);

            if (mes.equals("quit")) {
                crunchifyClient.close();
            }

            log("CLIENT sending: " + mes);
            buffer.clear();

        }

    }

    private static void log(String str) {
        System.out.println(str);
    }

}
