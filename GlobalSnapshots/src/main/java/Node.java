import java.util.concurrent.BlockingQueue;

public class Node implements Runnable {
    private final int id;
    private final BlockingQueue<Integer> handle;
    
    public Node(int id, BlockingQueue<Integer> handle){
        this.id = id;
        this.handle = handle;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1; i++){
            try {
                int receivedValue = handle.take();
                receivedValue++;
                System.out.println("Proces " + String.valueOf(id) + ": " + String.valueOf(receivedValue));
            } catch (InterruptedException err) {
                System.out.println(err.toString());
            }
        }
    }
}