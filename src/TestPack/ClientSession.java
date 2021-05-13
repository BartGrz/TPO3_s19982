package TestPack;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

class ClientSession {

    SelectionKey selkey;
    SocketChannel chan;
    ByteBuffer buf;

    ClientSession(SelectionKey selkey, SocketChannel chan) throws Throwable {
        this.selkey = selkey;
        this.chan = (SocketChannel) chan.configureBlocking(false); // asynchronous/non-blocking
        buf = ByteBuffer.allocateDirect(256); // 64 byte capacity
    }

    void disconnect() {
        MainServer.clientMap.remove(selkey);
        try {
            if (selkey != null)
                selkey.cancel();

            if (chan == null)
                return;

            System.out.println("bye bye " +  chan.getRemoteAddress());
            chan.close();
        } catch (Throwable t) { /** quietly ignore  */ }
    }

    void read() {
        try {
            int amount_read = -1;

            try { amount_read = chan.read((ByteBuffer) buf.clear());
            } catch (Throwable t) { }

            if (amount_read == -1)
                disconnect();

            if (amount_read < 1)
                return; // if zero

            // turn this bus right around and send it back!
            buf.flip();
            chan.write(buf);
            disconnect();
        } catch (Throwable t) {
            disconnect();
            t.printStackTrace();
        }
    }
    public void write() throws IOException {

        String newData = "New String to write to file..." + System.currentTimeMillis();

        ByteBuffer buf = ByteBuffer.allocate(48);
        buf.clear();
        buf.put(newData.getBytes());

        buf.flip();

        while(buf.hasRemaining()) {
            chan.write(buf);
        }

        disconnect();
    }

}