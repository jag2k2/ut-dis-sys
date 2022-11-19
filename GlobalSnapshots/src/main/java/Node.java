import java.util.concurrent.BlockingQueue;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Node implements Runnable {
    private final int id;
    private final BlockingQueue<Message> handle;
    private final int[] incomingChannelIDs;
    private final List<BlockingQueue<Message>> outgoingChannels;
    private boolean color = false;
    private final Map<Integer, List<Message>> chan = new HashMap<>();
    private final Map<Integer, Boolean> closed = new HashMap<>();

    private int state;
    
    public Node(int id, BlockingQueue<Message> handle, int[] incomingChannelIDs, List<BlockingQueue<Message>> outgoingChannels){
        this.id = id;
        this.handle = handle;
        this.incomingChannelIDs = incomingChannelIDs;
        this.outgoingChannels = outgoingChannels;
        this.state = 0;
        for (int chanId : incomingChannelIDs) {
            closed.put(chanId, false);
            chan.put(chanId, new ArrayList<>());
        }
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