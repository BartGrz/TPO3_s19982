package zad1;

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

public class CrunchifyNIOServer {

    public static final String client = "CLIENT: " ;
    public static final String admin = "ADMIN: " ;
    public static final String server = "SERVER: " ;

    @SuppressWarnings("unused")
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        int port_admin = Integer.parseInt(args[1]);
        // Selector: multiplexor of SelectableChannel objects
        Selector selector_client = Selector.open(); // selector is open here
    //    Selector selector_admin = Selector.open();

        // ServerSocketChannel: selectable channel for stream-oriented listening sockets
        ServerSocketChannel crunchifySocket = ServerSocketChannel.open();
        InetSocketAddress crunchifyAddr = new InetSocketAddress("localhost", port);

        ServerSocketChannel crunchifySocket_admin = ServerSocketChannel.open();
        InetSocketAddress crunchifyAddr_admin = new InetSocketAddress("localhost", port_admin);

        // Binds the channel's socket to a local address and configures the socket to listen for connections
        crunchifySocket.bind(crunchifyAddr);
        crunchifySocket_admin.bind(crunchifyAddr_admin);

        // Adjusts this channel's blocking mode.
        crunchifySocket.configureBlocking(false);

        crunchifySocket_admin.configureBlocking(false);

        int ops = crunchifySocket.validOps();
        SelectionKey selectKy = crunchifySocket.register(selector_client, ops, null);

      //  int ops_admin = crunchifySocket_admin.validOps();
      //  SelectionKey selectKy_admin = crunchifySocket_admin.register(selector_admin, ops_admin, null);

        // Infinite loop..
        // Keep server running
        // SocketChannel client_acc = crunchifySocket.accept();
        //SocketChannel admin_acc = crunchifySocket_admin.accept();
/*
                Thread thread = new Thread(()-> {
                    try {
                        startListening(selector_client, crunchifySocket, client);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        Thread thread_adm = new Thread(()-> {
            try {
                startListening(selector_admin,crunchifySocket_admin,admin);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
               if(thread.isAlive()) {
                    thread_adm.start();
               }


           }



 */

        startListening(selector_client,crunchifySocket,client);

    }

    private static void log(String str) {
        System.out.println(str);
    }

    private static void startListening(Selector selector, ServerSocketChannel socket, String name) throws IOException {
        while (true) {

            log(server + "i'm a server and i'm waiting for new connection and buffer select...");
            // Selects a set of keys whose corresponding channels are ready for I/O operations
            selector.select();

            // token representing the registration of a SelectableChannel with a Selector
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();

            while (it.hasNext()) {
                SelectionKey myKey = it.next();

                // Tests whether this key's channel is ready to accept a new socket connection
                if (myKey.isAcceptable()) {
                   SocketChannel socketChannel = socket.accept();

                    // Adjusts this channel's blocking mode to false
                    socketChannel.configureBlocking(false);

                    // Operation-set bit for read operations
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    log("Connection Accepted: " + socketChannel.getLocalAddress() + "\n");

                    // Tests whether this key's channel is ready for reading
                } else if (myKey.isReadable()) {

                    SocketChannel crunchifyClient = (SocketChannel) myKey.channel();
                    ByteBuffer crunchifyBuffer = ByteBuffer.allocate(256);
                    crunchifyClient.read(crunchifyBuffer);
                    String result = new String(crunchifyBuffer.array()).trim();


                    log("Message received from " + name + " " + result);

                    if (result.equals("quit")) {
                        crunchifyClient.close();
                        log("\nIt's time to close connection as we got quit request");
                        log("\nServer will keep running. Try running client again to establish new connection");
                    }
                }
                it.remove();
            }

        }

    }


}