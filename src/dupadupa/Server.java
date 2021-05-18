package dupadupa;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;


public class Server {
    private static Selector selector = null;
    private static Selector another_one = null;
    private static Info info = new Info();

    public static void main(String[] args) {

        try {
            selector = Selector.open();
            ServerSocketChannel socket = ServerSocketChannel.open();
            socket.bind(new InetSocketAddress("localhost", 8089));
            socket.configureBlocking(false);
            socket.register(selector, socket.validOps(), null);

            while (true) {

                selector.selectNow();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();


                for (Iterator<SelectionKey> it = selectedKeys.iterator(); it.hasNext(); ) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (key.isAcceptable()) {

                        handleAccept(key);
                    } else if (key.isReadable()) {
                        handleReading(key);

                    } else if (key.isWritable()) {
                        handleWriting(key);

                    }

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void handleAccept(SelectionKey key) throws IOException {

        SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
        System.out.println("connection accepted from " + client.socket().getPort());
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("key " + key + " is readable for port " + client.socket().getPort());
        selector.wakeup();
    }

    private static void handleReading(SelectionKey key) throws IOException {
        String portAndMessage[] = null;
        SocketChannel socketChannel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(256);
        buffer.rewind();
        socketChannel.read(buffer);
        String data = new String(buffer.array()).trim();
        String[] mes = data.split(" ");
        if (mes[0].equals("admin")) {
            info.setAdminMessage(mes[1]);
            SocketChannel admin = (SocketChannel) key.channel();
            admin.register(selector, SelectionKey.OP_WRITE);
        } else {
            if (!info.getSet().keySet().stream().anyMatch(integer -> integer == socketChannel.socket().getPort())) {
                info.linkPortWithKey(socketChannel.socket().getPort(), null);
            }
            System.out.println(socketChannel.socket().getPort() + " == " + Integer.parseInt(mes[1]));
            info.linkPortWithKey((int) socketChannel.socket().getPort(), mes[0]);
            System.out.println(info.getSet());
            socketChannel.register(selector, SelectionKey.OP_WRITE);

        }
    }


    private static void handleWriting(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        if (info.getAdminMessage() != null) {

            ByteBuffer buffer = ByteBuffer.allocate(256);
            System.out.println(info.getAdminMessage());
            for (SelectionKey selectionKey : selector.keys()) {
                if (selectionKey.channel() instanceof SocketChannel) {
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    System.out.println(info.getSet().get(channel.socket().getPort()) + " wiadomosc admina = " + info.getAdminMessage());
                    if (info.getSet().get(channel.socket().getPort()) != null && info.getSet().get(channel.socket().getPort()).stream().anyMatch(s -> s.equals(info.getAdminMessage()))) {
                        buffer.put(info.getAdminMessage().getBytes());
                        buffer.rewind();
                        channel.write(buffer);

                    }

                    channel.register(selector, SelectionKey.OP_READ);
                }
                buffer.clear();
            }
        } else {
            socketChannel.register(selector, SelectionKey.OP_READ);
        }
    }

}