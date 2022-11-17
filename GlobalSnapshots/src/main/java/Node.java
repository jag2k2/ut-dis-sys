import java.util.concurrent.BlockingQueue;

public class Node implements Runnable {
    private final int id;
    private final BlockingQueue<Integer> handle;
    private final BlockingQueue<Integer> outgoingChannel;
    
    public Node(int id, BlockingQueue<Integer> handle, BlockingQueue<Integer> outgoingChannel){
        this.id = id;
        this.handle = handle;
        this.outgoingChannel = outgoingChannel;
    }

    @Override
    public void run() {
        for (int i = 0; i < 4; i++){
            try {
                int receivedValue = handle.take();
                int modifiedValue = receivedValue + 1;
                System.out.println("Proces " + String.valueOf(id) + ": " + String.valueOf(modifiedValue));
                outgoingChannel.put(modifiedValue);
            } catch (InterruptedException err) {
                System.out.println(err.toString());
            }
        }
    }
}