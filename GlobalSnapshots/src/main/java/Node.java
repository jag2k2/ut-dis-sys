import java.util.concurrent.BlockingQueue;

public class Node implements Runnable {
    private final BlockingQueue<String> handle;
    
    public Node(BlockingQueue<String> handle){
        this.handle = handle;
    }

    @Override
    public void run() {
        try {
            String name = handle.take();
            System.out.println("Hello " + name);
        } catch (InterruptedException err) {
            System.out.println(err.toString());
        }
    }
}