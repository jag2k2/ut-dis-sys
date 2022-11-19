import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        BlockingQueue<Message> handleNode1 = new LinkedBlockingQueue<>();
        BlockingQueue<Message> handleNode2 = new LinkedBlockingQueue<>();
        BlockingQueue<Message> handleNode3 = new LinkedBlockingQueue<>();
        BlockingQueue<Message> handleNode4 = new LinkedBlockingQueue<>();

        Thread nodeThread1 = new Thread(new Node(1, handleNode1, handleNode2));
        Thread nodeThread2 = new Thread(new Node(2, handleNode2, handleNode3));
        Thread nodeThread3 = new Thread(new Node(3, handleNode3, handleNode4));
        Thread nodeThread4 = new Thread(new Node(4, handleNode4, handleNode1));

        nodeThread1.start();
        nodeThread2.start();
        nodeThread3.start();
        nodeThread4.start();

        Scanner inputReader = new Scanner(System.in);
        System.out.println("Enter a Command (0:Exit/1:Snapshot/2:Restore)");

        try {
            handleNode1.put(new Message(0, "AppMsg", 0));
            while (true) {
                try {
                    int cmd = inputReader.nextInt();
                    if (cmd == 0) {
                        handleNode1.put(new Message(0, "Exit", 0));
                        break;
                    }
                    else {
                        System.out.println("User Input: " + Integer.valueOf(cmd));
                    }
                } catch (InputMismatchException err) {
                    System.out.println(err.toString());
                }
            }

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