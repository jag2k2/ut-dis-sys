import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Node implements Runnable, SnapShotAPI {
    private final int id;
    private final BlockingQueue<Message> handle;
    private final int[] incomingChannelIDs;
    private final List<BlockingQueue<Message>> outgoingChannels;
    private boolean color = false;
    private final Map<Integer, List<Message>> chan = new HashMap<>();
    private final Map<Integer, Boolean> closed = new HashMap<>();
    MarkerCustom myMarkerColor;

    //local_snapshot_info
    String filename;
    FileWriter fileWriter;

    private int state;
    
    public Node(int id,
                BlockingQueue<Message> handle,
                int[] incomingChannelIDs,
                List<BlockingQueue<Message>> outgoingChannels) throws IOException {
        this.id = id;
        this.handle = handle;
        this.incomingChannelIDs = incomingChannelIDs;
        this.outgoingChannels = outgoingChannels;
        this.myMarkerColor = MarkerCustom.WHITE;
        this.state = this.myMarkerColor.ordinal();
        for (int chanId : incomingChannelIDs) {
            closed.put(chanId, false);
            chan.put(chanId, new ArrayList<>());
        }
        filename = "snapshot_Node_" + String.valueOf(id) + ".txt";
        this.fileWriter = new FileWriter(filename);
    }

    @Override
    public void run() {
        try {
            String filename = "debug_out" + String.valueOf(id) + ".txt";
            FileWriter fileWriter = new FileWriter(filename);
            while (true) {
                    Message receivedMsg = handle.take();
                    String command = receivedMsg.command;
                    if(receivedMsg.marker != null){
                        receive(receivedMsg.marker,  receivedMsg.id);
                    }
                    receive(receivedMsg);
                    if (command == "AppMsg") {
                        state = receivedMsg.payload + 1;
                        Thread.sleep(100);
                        fileWriter.write("Process" + String.valueOf(id) + ": AppMsg from chan" + String.valueOf(receivedMsg.id) + ", state: " + String.valueOf(state) + "\n");
                        fileWriter.flush();
                        Message responseMessage = new Message(id, "AppMsg", state, null);
                        outgoingChannels.get(0).put(responseMessage);
                    } 
                    else if (command == "Snapshot") {
                        localSnapShot();
                    }
                    else if (command == "Restore") {

                    } 
                    else if (command == "Exit") {
                        outgoingChannels.get(0).put(new Message(id, "Exit", 0, null));
                        this.fileWriter.close();
                        break;
                    }
            } 
            fileWriter.close();
        } catch (Exception err) {
            System.out.println(err.toString());
        }
    }

    @Override
    public void receive(MarkerCustom markerColor, int jth) throws IOException, InterruptedException {
       if(markerColor != null){
           if(getColor()){
               turnRed();
           }
           closed.put(jth, true);
       }
    }

    @Override
    public void receive(Message program_message) {
            if ((myMarkerColor == MarkerCustom.RED) && (!closed.get(program_message.id))) {
                chan.get(program_message.id).add(program_message);
            }
    }

    @Override
    public void turnRed() throws IOException, InterruptedException {
        if(getColor()){
            saveState();
            myMarkerColor = MarkerCustom.RED;
            System.out.println("Node: " + this.id + ", changed color to: " + myMarkerColor);
            //create null command message for sending a marker
            Message markerMessage = new Message(this.id, null,0, myMarkerColor);
            outgoingChannels.get(0).put(markerMessage);
        }
    }

    @Override
    public boolean getColor() {
        boolean result = false;
        if(myMarkerColor.ordinal() == 1){
            result = true;
        }
        this.color = result;
        return result;
    }

    @Override
    public void saveState() throws IOException {
        this.fileWriter.write(String.valueOf(this.state));
    }

    public int getState(){
        return this.state;
    }

    public void localSnapShot() throws IOException, InterruptedException {
        turnRed();
    }

}