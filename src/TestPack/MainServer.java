package TestPack;

import zad1.AvailablePort;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class MainServer {

    public static void main(String[] args) throws Throwable {
        new MainServer(new InetSocketAddress("localhost", Integer.parseInt(args[0])));
    }

    ServerSocketChannel serverChannel;
    Selector selector;
    SelectionKey serverKey;
    SelectionKey adminKey;

    MainServer(InetSocketAddress listenAddress) throws Throwable {
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverKey = serverChannel.register(selector = Selector.open(), SelectionKey.OP_ACCEPT);
        serverChannel.bind(listenAddress);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                loop();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    static HashMap<SelectionKey, ClientSession> clientMap = new HashMap<SelectionKey, ClientSession>();
    static HashMap<SelectionKey, ClientSession> clientMap_write = new HashMap<SelectionKey, ClientSession>();

    void loop() throws Throwable {
        selector.selectNow();

        for (SelectionKey key : selector.selectedKeys()) {
            try {
                if (!key.isValid())
                    continue;

                if (key == serverKey) {
                    SocketChannel acceptedChannel = serverChannel.accept();

                    if (acceptedChannel == null)
                        continue;

                    acceptedChannel.configureBlocking(false);
                    SelectionKey readKey = acceptedChannel.register(selector, SelectionKey.OP_READ);
                    clientMap.put(readKey, new ClientSession(readKey, acceptedChannel));

                    System.out.println("New client ip=" + acceptedChannel.getRemoteAddress() + ", total clients=" + MainServer.clientMap.size());
                }

                if (key.isReadable()) {
                    ClientSession sesh = clientMap.get(key);

                    if (sesh == null)
                        continue;

                    sesh.read();
                    selector.selectedKeys().clear();
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }


            for (SelectionKey write_key : selector.selectedKeys()) {

                if (!write_key.isValid())
                    continue;

                if (write_key == serverKey) {
                    SocketChannel acceptedChannel = serverChannel.accept();

                    if (acceptedChannel == null)
                        continue;

                    acceptedChannel.configureBlocking(false);
                    SelectionKey writeKey = acceptedChannel.register(selector, SelectionKey.OP_WRITE);
                    clientMap_write.put(writeKey, new ClientSession(writeKey, acceptedChannel));

                    System.out.println("New client ip=" + acceptedChannel.getRemoteAddress() + ", total clients=" + MainServer.clientMap.size());


                    if (write_key.isWritable()) {
                        ClientSession sesh = clientMap.get(write_key);

                        if (sesh != null)
                            continue;

                        sesh.write();


                    }


                }
                selector.selectedKeys().clear();
            }

        }
    }
}