package myTest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MainServer {

    private static Selector selector = null;
    private static Selector selector_write = null;

    public static void main(String[] args) {

        try {
            selector = Selector.open();
            ServerSocketChannel socket = ServerSocketChannel.open();
            ServerSocket serverSocket = socket.socket();
            serverSocket.bind(new InetSocketAddress("localhost", 8089));
            System.out.println("Server is listening on port " + serverSocket.getLocalSocketAddress());
            socket.configureBlocking(false);
            int ops = socket.validOps();
            socket.register(selector, ops, null);

            while (true) {
                selector.select();
                //selector_write.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                for (Iterator<SelectionKey> it = selectedKeys.iterator(); it.hasNext(); ) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        handleAccept(socket, key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleAccept(ServerSocketChannel mySocket, SelectionKey key) throws IOException {

        System.out.println("connection accepted");
        SocketChannel client = mySocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);


    }

    private static void handleRead(SelectionKey key)
            throws IOException {
        //System.out.println("Reading...");
        // create a ServerSocketChannel to read the request
        SocketChannel client = (SocketChannel) key.channel();

        // Create buffer to read data
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        client.read(buffer);
//        Parse data from buffer to String
        String data = new String(buffer.array()).trim();
        if (data.length() > 0) {
            // System.out.println("Received message: " + data);
            if (data.equalsIgnoreCase("exit")) {
                // client.close();
                System.out.println("Connection closed...");
            }
            buffer.flip();
            client.write(buffer);

           // client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_WRITE);
        }

    }


}
