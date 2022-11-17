import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        BlockingQueue<Integer> handleNode1 = new LinkedBlockingQueue<>();
        BlockingQueue<Integer> handleNode2 = new LinkedBlockingQueue<>();
        BlockingQueue<Integer> handleNode3 = new LinkedBlockingQueue<>();
        BlockingQueue<Integer> handleNode4 = new LinkedBlockingQueue<>();

        Thread nodeThread1 = new Thread(new Node(0, handleNode1));
        Thread nodeThread2 = new Thread(new Node(1, handleNode2));
        Thread nodeThread3 = new Thread(new Node(2, handleNode3));
        Thread nodeThread4 = new Thread(new Node(3, handleNode4));

        nodeThread1.start();
        nodeThread2.start();
        nodeThread3.start();
        nodeThread4.start();

        try {
            handleNode1.put(0);
            handleNode2.put(5);
            handleNode3.put(10);
            handleNode4.put(15);
        } catch (InterruptedException err){
            System.out.println(err.toString());
        }

        try {
            nodeThread1.join();
            nodeThread2.join();
            nodeThread3.join();
            nodeThread4.join();
        } catch(InterruptedException err){
            System.out.println(err.toString());
        }

        System.out.println("Exiting");
    }
}