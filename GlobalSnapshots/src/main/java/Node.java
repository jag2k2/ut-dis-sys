import java.util.concurrent.BlockingQueue;
import java.io.FileWriter;

public class Node implements Runnable {
    private final int id;
    private final BlockingQueue<Message> handle;
    private final BlockingQueue<Message> outgoingChannel;
    
    public Node(int id, BlockingQueue<Message> handle, BlockingQueue<Message> outgoingChannel){
        this.id = id;
        this.handle = handle;
        this.outgoingChannel = outgoingChannel;
    }

    @Override
    public void run() {
        try {
            String filename = "debug_out" + String.valueOf(id) + ".txt";
            FileWriter fileWriter = new FileWriter(filename);
            while (true) {
                    Message receivedMsg = handle.take();
                    String command = receivedMsg.command;
                    if (command == "Exit") {
                        outgoingChannel.put(new Message("Exit", 0));
                        break;
                    }
                    else if (command == "AppMsg") {
                        int modifiedValue = receivedMsg.payload + 1;
                        Thread.sleep(100);
                        fileWriter.write("Process " + String.valueOf(id) + ": " + String.valueOf(modifiedValue) + "\n");
                        fileWriter.flush();
                        Message responseMessage = new Message("AppMsg", modifiedValue);
                        outgoingChannel.put(responseMessage);
                    }
            } 
            fileWriter.close();
        } catch (Exception err) {
            System.out.println(err.toString());
        }
    }
}