import java.util.concurrent.BlockingQueue;
import java.io.FileWriter;

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
        try {
            String filename = "debug_out" + String.valueOf(id) + ".txt";
            FileWriter fileWriter = new FileWriter(filename);
            for (int i = 0; i < 4; i++){
                    int receivedValue = handle.take();
                    int modifiedValue = receivedValue + 1;
                    fileWriter.write("Process " + String.valueOf(id) + ": " + String.valueOf(modifiedValue) + "\n");
                    outgoingChannel.put(modifiedValue);
                    Thread.sleep(10);
            }
            fileWriter.close();
        } catch (Exception err) {
            System.out.println(err.toString());
        }
    }
}