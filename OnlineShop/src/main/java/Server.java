import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

public class Server {

    static CriticalSection criticalSection;
    public static void main (String[] args) throws IOException {
        int tcpPort;
        int udpPort;
        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(2) <udpPort>: the port number for UDP connection");
            System.out.println("\t(3) <file>: the file of inventory");

            System.exit(-1);
        }
        tcpPort = Integer.parseInt(args[0]);
        udpPort = Integer.parseInt(args[1]);

        String inventoryFile = args[2];

        criticalSection = new CriticalSection(inventoryFile);
        CanShop shopper = new DbShopper(criticalSection);
        //CanShop shopper = new FakeShopper();

        ArrayBlockingQueue<QueuePacket> commandQueue = new ArrayBlockingQueue<>(100);

        Thread commandHandlerThread = new Thread(new CommandHandler(commandQueue, criticalSection, shopper));
        commandHandlerThread.start();

        System.out.println("Starting TCP server on port: " + tcpPort);
        Thread tcpListenerThread = new Thread(new TcpListener(commandQueue, tcpPort));
        tcpListenerThread.start();

        System.out.println("Starting UDP server on port: " + udpPort);
        Thread udpListenerThread = new Thread(new UdpListener(commandQueue, udpPort));
        udpListenerThread.start();



        try {
            commandHandlerThread.join();//set up the critical section once and only once
            udpListenerThread.join(); //set up the udpListener once and only once
            tcpListenerThread.join(); //set up the tcpListener once and only once
        } catch(InterruptedException err){
            System.out.println(err.toString());
            System.out.println("deleting all threads");
            criticalSection.close();
            criticalSection = null;

        }
    }
}
