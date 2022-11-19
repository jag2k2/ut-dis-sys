import java.util.concurrent.BlockingQueue;
import java.io.FileWriter;
import java.util.List;

public class Node implements Runnable {
    private final int id;
    private final BlockingQueue<Message> handle;
    private final int[] incomingChannels;
    private final List<BlockingQueue<Message>> outgoingChannels;
    
    private int state;
    
    public Node(int id, BlockingQueue<Message> handle, int[] incomingChannels, List<BlockingQueue<Message>> outgoingChannels){
        this.id = id;
        this.handle = handle;
        this.incomingChannels = incomingChannels;
        this.outgoingChannels = outgoingChannels;
        this.state = 0;
    }

    @Override
    public void run() {
        try {
            String filename = "debug_out" + String.valueOf(id) + ".txt";
            FileWriter fileWriter = new FileWriter(filename);
            while (true) {
                    Message receivedMsg = handle.take();
                    String command = receivedMsg.command;
                    if (command == "AppMsg") {
                        state = receivedMsg.payload + 1;
                        Thread.sleep(100);
                        fileWriter.write("Process" + String.valueOf(id) + ": AppMsg from chan" + String.valueOf(receivedMsg.id) + ", state: " + String.valueOf(state) + "\n");
                        fileWriter.flush();
                        Message responseMessage = new Message(id, "AppMsg", state);
                        outgoingChannels.get(0).put(responseMessage);
                    } 
                    else if (command == "Snapshot") {

                    }
                    else if (command == "Restore") {

                    } 
                    else if (command == "Exit") {
                        outgoingChannels.get(0).put(new Message(id, "Exit", 0));
                        break;
                    }
            } 
            fileWriter.close();
        } catch (Exception err) {
            System.out.println(err.toString());
        }
    }
}