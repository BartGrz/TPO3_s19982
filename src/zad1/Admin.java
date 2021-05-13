package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;

public class Admin {

    public static void main(String[] args) throws InterruptedException, IOException {
        Scanner in = new Scanner(System.in);
        int port = Integer.parseInt(args[0]);
        InetSocketAddress adminAddr = new InetSocketAddress("localhost", port);
        SocketChannel admin = SocketChannel.open(adminAddr);

        log("Connecting to Server on port " +port + "...");

        while (true) {
            in = new Scanner(System.in);
            String mes = in.nextLine();


            byte[] message = mes.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            admin.write(buffer);

            if(mes.equals("quit")) {
                admin.close();
            }

            log("ADMIN sending: " + mes);
            buffer.clear();

            // wait for 2 seconds before sending next message


        }

    }

    private static void log(String str) {
        System.out.println(str);
    }

}
