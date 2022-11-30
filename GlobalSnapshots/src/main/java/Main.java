import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        BlockingQueue<Message> handleNode1 = new LinkedBlockingQueue<>();
        BlockingQueue<Message> handleNode2 = new LinkedBlockingQueue<>();
        BlockingQueue<Message> handleNode3 = new LinkedBlockingQueue<>();
        BlockingQueue<Message> handleNode4 = new LinkedBlockingQueue<>();

        List<BlockingQueue<Message>> outgoingChannels1 = new ArrayList<BlockingQueue<Message>>();
        List<BlockingQueue<Message>> outgoingChannels2 = new ArrayList<BlockingQueue<Message>>();
        List<BlockingQueue<Message>> outgoingChannels3 = new ArrayList<BlockingQueue<Message>>();
        List<BlockingQueue<Message>> outgoingChannels4 = new ArrayList<BlockingQueue<Message>>();

        outgoingChannels1.add(handleNode2);
        outgoingChannels2.add(handleNode3);
        outgoingChannels3.add(handleNode4);
        outgoingChannels4.add(handleNode1);

        int[] incomingChannelIDs1 = {0,4};
        int[] incomingChannelIDs2 = {1};
        int[] incomingChannelIDs3 = {2};
        int[] incomingChannelIDs4 = {3};

        Thread nodeThread1 = new Thread(new Node(1, handleNode1, incomingChannelIDs1, outgoingChannels1));
        Thread nodeThread2 = new Thread(new Node(2, handleNode2, incomingChannelIDs2, outgoingChannels2));
        Thread nodeThread3 = new Thread(new Node(3, handleNode3, incomingChannelIDs3, outgoingChannels3));
        Thread nodeThread4 = new Thread(new Node(4, handleNode4, incomingChannelIDs4, outgoingChannels4));

        nodeThread1.start();
        nodeThread2.start();
        nodeThread3.start();
        nodeThread4.start();

        Scanner inputReader = new Scanner(System.in);
        System.out.println("Enter a Command (0:Exit/1:Snapshot/2:Restore)");

        try {
            handleNode1.put(new Message(0, "ProgMsg"));
            boolean exit = false;
            do {
                try {
                    int cmd = inputReader.nextInt();
                    if (cmd == 0) {
                        handleNode1.put(new Message(0, "Exit"));
                        exit = true;
                    }
                    else if (cmd == 1) {
                        handleNode1.put(new Message(0, "MARKER"));
                    }
                    else if (cmd == 2) {
                        handleNode1.put(new Message(0, "RESTORE"));
                    }
                    else {
                        System.out.println("Invalid input: " + Integer.valueOf(cmd));
                    }
                } catch (InputMismatchException err) {
                    System.out.println(err.toString());
                }
            } while (exit == false);

            nodeThread1.join();
            nodeThread2.join();
            nodeThread3.join();
            nodeThread4.join();

        } catch (InterruptedException err){
            System.out.println(err.toString());
        }

        inputReader.close();
        System.out.println("Exiting");
    }
}