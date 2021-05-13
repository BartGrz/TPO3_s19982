package dupadupa;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private static Selector selector = null;
    private static Selector another_one = null;
    @Getter
    @Setter
    private static String info;
    public static Info askMe = new Info();
    private static Set<SelectionKey> selectedKeys = null;

    public static void main(String[] args) {

        try {
            selector = Selector.open();
            ServerSocketChannel socket = ServerSocketChannel.open();
            ServerSocket serverSocket = socket.socket();
            serverSocket.bind(new InetSocketAddress("localhost", 8089));
            socket.configureBlocking(false);
            int ops = socket.validOps();
            socket.register(selector, ops, null);

            while (true) {
                selector.select();
                selectedKeys = selector.selectedKeys();
                for (Iterator<SelectionKey> it = selectedKeys.iterator(); it.hasNext(); ) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        handleAccept(socket, key);
                    } else if (key.isReadable()) {
                        handleReading(key);
                    } else if (key.isWritable()) {
                        handleWriting(key);
                    }
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleAccept(ServerSocketChannel serverSocketChannel, SelectionKey key) throws IOException {


        SocketChannel client = serverSocketChannel.accept();
        System.out.println("connection accepted from " + client.getRemoteAddress());
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ, client.socket().getPort());

    }

    private static void handleReading(SelectionKey key) throws IOException {

        SocketChannel client = (SocketChannel) key.channel();


        // Create buffer to read data
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        client.read(buffer);
        buffer.flip();
        buffer.clear();

//        Parse data from buffer to String
        String data = new String(buffer.array()).trim();
        if (data.length() > 0) {

            if (data.equals("Client") || data.equals("Admin")) {
                askMe.gatherConnectionInfo(key, data);
            } else if (!askMe.getKeyValueClients(key).equals("Admin")) {
                askMe.addToMap(key, data);
            }
            if (askMe.getKeyValueClients(key).equals("Admin") && !data.equals("Admin")) {
                setInfo(data);
                // System.out.println("sended" + getInfo());
            }
        }
        client.register(selector, SelectionKey.OP_WRITE);


    }


    private static void handleWriting(SelectionKey key) throws IOException {


        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // buffer.clear();
        String mes_cl = getInfo();

        //fixme: problem z wysylaniem wiadomosci do odpowiednich klientow, teraz dodatkowo nie zadziala
        // , jezeli klient zasubskrybuje kanal przed wyslaniem wiadomosci przez admina

        SocketChannel chan = (SocketChannel) key.channel();
        for (SelectionKey keys : selectedKeys) {
            if (askMe.getKeyValueSubs(keys).equals(mes_cl)) {

                System.out.println(" keys = " + keys + " key = " + keys);
                if (keys.channel() instanceof SocketChannel) {
                    SocketChannel channel = (SocketChannel) keys.channel();
                    buffer.put(mes_cl.getBytes());
                    buffer.flip();
                    channel.write(buffer);
                    buffer.clear();
                }else {
                    SocketChannel channel = (SocketChannel) keys.channel();
                    buffer.put("".getBytes());
                    buffer.flip();
                    channel.write(buffer);
                    buffer.clear();
                }
            }
            buffer.clear();
            chan.register(selector, SelectionKey.OP_READ);
        }
    }

}