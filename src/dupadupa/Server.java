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
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
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
        // client.getRemoteAddress();

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
            //  System.out.println("Received message  from " + client.socket().getPort() +  " with value : " + data);
            if(data.equals("celebryci")){
                setInfo(data);
                client.register(selector, SelectionKey.OP_WRITE);
            }


            if (data.equalsIgnoreCase("exit")) {
                // client.close();
                System.out.println("Connection closed...");
            }



        }

    }

    private static void handleWriting(SelectionKey key) throws IOException {


        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String mes_cl = getInfo() ;

        //FIXME : bufor zbiera ostatnie elementy z buforu i dodaje do mes

        buffer.put(mes_cl.getBytes());

        for (SelectionKey keys : selector.keys()) {

            if (key.isValid() && keys.channel() instanceof SocketChannel) {
                SocketChannel chan = (SocketChannel) keys.channel();
                buffer.flip();
                chan.write(buffer);


                //   System.out.println("mesage send to " + chan.socket().getPort());
                chan.register(selector, SelectionKey.OP_READ);

            }


        }

        buffer.clear();

    }

}
