import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;

public class UdpListener implements Runnable{
    private final int udpPort;
    int buffer_length = 2048;
    public DatagramSocket dataSocket;
    public DatagramPacket receivedPacket;

    public ArrayBlockingQueue<QueuePacket> sharedQueue;

    public UdpListener(ArrayBlockingQueue<QueuePacket> threadSafeQueue, int udpPort){
            this.udpPort = udpPort;
            sharedQueue = threadSafeQueue;
        }

    @Override
    public void run() {
        try {
            dataSocket = new DatagramSocket(udpPort);
            while (true) {
                try {
                    byte[] data_buffer = new byte[buffer_length];
                    receivedPacket = new DatagramPacket(data_buffer, buffer_length);
                    dataSocket.receive(receivedPacket);     // blocking
                    try {
                        String clientCommand = new String(receivedPacket.getData(), StandardCharsets.UTF_8);
                        clientCommand = clientCommand.replace("\0", "");

                        String[] tokens = clientCommand.split(" ");

                        this.sendToQueue(this.dataSocket, tokens);


                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                } catch (IOException err) {
                    System.err.println(err.toString());
                }
            }
        } catch (SocketException err) {
            System.err.println(err.toString());
        }
    }

    public void sendToQueue(DatagramSocket comm, String[] tokens) throws InterruptedException {
        UdpComms udpComm= new UdpComms(comm, tokens);
        udpComm.commandtokens = tokens;
        udpComm.receivedPacket = receivedPacket;

        QueuePacket dataToQueue = new QueuePacket(false, udpComm);

        this.sharedQueue.put(dataToQueue);
    }
}
