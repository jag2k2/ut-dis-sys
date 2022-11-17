import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

public class TcpListener implements Runnable {
    private final int tcpPort;
    public ArrayBlockingQueue<QueuePacket> sharedQueue;

    public TcpListener(ArrayBlockingQueue<QueuePacket> threadSafeQueue, int tcpPort){
        this.tcpPort = tcpPort;
        sharedQueue = threadSafeQueue;

    }

    @Override
    public void run() {
        try {
            ServerSocket listener = new ServerSocket(tcpPort);
            Socket clientConnection;
            while ((clientConnection = listener.accept()) != null) {    // Blocking
                try {
                    Scanner commandScanner = new Scanner(clientConnection.getInputStream());
                    ObjectOutputStream responseWriter = new ObjectOutputStream(clientConnection.getOutputStream());
                    String clientCommand = commandScanner.nextLine();

                    String[] tokens = clientCommand.split(" ");

                    this.sendToQueue(responseWriter, tokens);

                } catch (IOException e){
                    System.err.print(e.toString());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e){
            System.err.println("Server aborted: " + e.toString());
        }
    }

    public void sendToQueue(ObjectOutputStream responseWriter, String[] tokens) throws InterruptedException {
        TcpComms commInfo = new TcpComms(responseWriter, tokens );

        QueuePacket tcpPacketToBeSent = new QueuePacket(false, commInfo );
        sharedQueue.put(tcpPacketToBeSent);
    }
}
