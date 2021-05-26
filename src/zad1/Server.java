package zad1;

import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;


public class Server {
    private static Selector selector = null;
    private static Info info = new Info();
    private static final String serverInfo = "SERVER : ";
    @Getter
    private static List<Info> listInfo = new ArrayList<>();


    public static void main(String[] args) {

        try {

            /**
             * main logic,getting key if client trying to connect checking if key is prepared for possible operations
             * @param args
             */
            selector = Selector.open();
            ServerSocketChannel socket = ServerSocketChannel.open();
            socket.bind(new InetSocketAddress("localhost", 8089));
            socket.configureBlocking(false);
            socket.register(selector, socket.validOps(), null);
            listInfo.add(info);
            while (true) {

                selector.selectNow(); //selector is now gathering possible connection and preparing SelectionKeys
                Set<SelectionKey> selectedKeys = selector.selectedKeys(); //stored SelectionKeys

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

    /**
     * accepting the key from fresh connection
     * accepting serverSocketChannel and creating SocketChannel for key method get by selector
     * @param key
     * @throws IOException
     */
    private static void handleAccept(SelectionKey key) throws IOException {

        SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
        System.out.println(serverInfo + " connection accepted from " + client.socket().getPort());
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    /**
     * handle reading, if key is readable, read messages from clients and admin
     * managing requests using Info class and its methods
     * @param key
     * @throws IOException
     */
    private static void handleReading(SelectionKey key) throws IOException {

        SocketChannel socketChannel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.rewind();
        socketChannel.read(buffer);
        String data = new String(buffer.array()).trim();
        String[] mes = data.split(";");

        if (mes[0].equals("admin")) {
            SocketChannel admin = (SocketChannel) key.channel();
            switch (mes[2]) {
                case "SEND" :
                    info.setAdminChosenCategory(mes[1]);
                    info.setAdminMessage(mes[3]);
                    admin.register(selector, SelectionKey.OP_WRITE);
                    break;
                case "DELETE":
                    info.setAdminMessage(" topic has been removed by admin");
                    info.setAdminChosenCategory(mes[1]);
                    info.deleteTopic(mes[1]);
                    admin.register(selector, SelectionKey.OP_WRITE);
                    break;
                case "ADD" :
                    info.addTopic(mes[3]);
                    break;
            }

        } else {
            if (!info.getSet().keySet().stream().anyMatch(integer -> integer == socketChannel.socket().getPort())) {
                info.linkPortWithCategory(socketChannel.socket().getPort(), null);
            }
            if(mes[1].equals("delete")) {
                info.deleteCategoryForPort(socketChannel.socket().getPort(),mes[0]);
                System.out.println(info.getSet().get(socketChannel.socket().getPort()));
            }else {
                info.linkPortWithCategory(socketChannel.socket().getPort(), mes[0]);
                socketChannel.register(selector, SelectionKey.OP_READ);
            }
        }
    }

    /**
     * handle writing operation if ADMIN key is writable, broadcasting messages to clients subsribed to choosen topic
     * @param key
     * @throws IOException
     */
    private static void handleWriting(SelectionKey key) throws IOException {

        SocketChannel socketChannel = (SocketChannel) key.channel();
        if (info.getAdminMessage() != null) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            for (SelectionKey selectionKey : selector.keys()) {
                if (selectionKey.channel() instanceof SocketChannel) {
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    int port = channel.socket().getPort();
                    if (info.getSet().get(port) != null
                            && info.getSet().get(port)
                            .stream()
                            .anyMatch(s -> s.equals(info.getAdminChosenCategory()))
                    ) {

                        String messageReturn = info.getAdminChosenCategory() + ";" + info.getAdminMessage();
                        buffer.put(messageReturn.getBytes());
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