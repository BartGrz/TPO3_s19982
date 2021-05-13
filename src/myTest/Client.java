package myTest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Client {
    private static Selector selector = null;

    public static void main(String[] args) {


        try {
            selector = Selector.open();
            SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 8089));
            Scanner scanner = new Scanner(System.in);

            while (true) {
              //  client.configureBlocking(false);
              //  client.register(selector,SelectionKey.OP_WRITE);

                String message = scanner.next();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                buffer.put(message.getBytes());
                buffer.flip();
                int BytesWritten = client.write(buffer);
                System.out.println("CLient sending message");

                //  client.close();
                // System.out.println("connection from client closed");

                client.configureBlocking(false);
                client.register(selector, SelectionKey.OP_READ);
                Set<SelectionKey> keys = selector.selectedKeys();

                for(Iterator<SelectionKey> it = keys.iterator(); it.hasNext();) {

                    SelectionKey key = it.next();
                    if (key.isReadable()) {
                        ByteBuffer bufferRead = ByteBuffer.allocate(1024);
                        client.read(bufferRead);
//        Parse data from buffer to String
                        String data = new String(bufferRead.array()).trim();
                        if (data.length() > 0) {

                            System.out.println("Received message from server : " + data);
                            if (data.equalsIgnoreCase("exit")) {
                                client.close();
                                bufferRead.flip();
                                System.out.println("Connection closed...");
                            }
                        }
                    }

                }


            }
        } catch (ClosedChannelException closedChannelException) {
            closedChannelException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}



