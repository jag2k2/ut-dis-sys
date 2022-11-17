import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        BlockingQueue<String> handleNode1 = new ArrayBlockingQueue<>(100);
        BlockingQueue<String> handleNode2 = new ArrayBlockingQueue<>(100);
        BlockingQueue<String> handleNode3 = new ArrayBlockingQueue<>(100);
        BlockingQueue<String> handleNode4 = new ArrayBlockingQueue<>(100);

        Thread nodeThread1 = new Thread(new Node(handleNode1));
        Thread nodeThread2 = new Thread(new Node(handleNode2));
        Thread nodeThread3 = new Thread(new Node(handleNode3));
        Thread nodeThread4 = new Thread(new Node(handleNode4));

        nodeThread1.start();
        nodeThread2.start();
        nodeThread3.start();
        nodeThread4.start();

        try {
            handleNode1.put("Jeff");
            handleNode2.put("Mindi");
            handleNode3.put("Aubrey");
            handleNode4.put("Brooklyn");
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