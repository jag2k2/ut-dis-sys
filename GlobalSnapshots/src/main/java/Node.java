import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class Node implements Runnable, SnapShotAPI {
    private final int id;
    private final BlockingQueue<Message> handle;
    private final int[] incomingChannelIDs;
    private final List<BlockingQueue<Message>> outgoingChannels;
    private boolean color = false;
    private Map<Integer, List<Message>> chan = new HashMap<>();
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
                        System.out.println("About to Restore");
                        restoreState();

                    } 
                    else if (command == "Exit") {
                        outgoingChannels.get(0).put(new Message(id, "Exit", 0, null));
                        this.fileWriter.close();
                        break;
                    }
            } 
            //fileWriter.close();
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
           boolean allClosed = !chan.containsValue(false);
           if(allClosed){
               FileOutputStream fout = new FileOutputStream("Node_" + this.id + "_messageList.txt");
               ObjectOutputStream objStream = new ObjectOutputStream(fout);
               objStream.writeObject(this.chan);
               fout.close();
               System.out.println("SnapShot of Node: " + this.id + ", State: " + this.state);

           }
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



        //first save the state of the node
        this.fileWriter = new FileWriter(filename);
        this.fileWriter.write(String.valueOf(this.state));
        this.fileWriter.close();
        //During this IO operation there could have been messages queued so save the chan object
        //which are the incoming messages during that time.

//        FileOutputStream fout = new FileOutputStream("Node_" + this.id + "_messageList.txt");
//        ObjectOutputStream objStream = new ObjectOutputStream(fout);
//        objStream.writeObject(this.chan);
//        fout.close();
//        System.out.println("SnapShot of Node: " + this.id + ", State: " + this.state);
    }

    public int getState(){
        return this.state;
    }

    public void localSnapShot() throws IOException, InterruptedException {
        turnRed();
    }

    public void restoreState() throws InterruptedException, IOException {
        Scanner fileToRead = new Scanner(new File(filename));
        if(myMarkerColor == MarkerCustom.RED) {
            state = fileToRead.nextInt();
            // READ during time of snapshot the restoration of the channel
            ObjectInputStream objectinputstream = null;
            try {
                FileInputStream streamIn = new FileInputStream("Node_" + this.id + "_messageList.txt");
                objectinputstream = new ObjectInputStream(streamIn);
                Map<Integer, List<Message>> chanObjectStreamedIn = (Map<Integer, List<Message>>) objectinputstream.readObject();
                System.out.println(chanObjectStreamedIn);
                this.chan = chanObjectStreamedIn;
                System.out.println("updated the channel successfully of previous messages");

                this.handle.clear();

                for( Map.Entry<Integer, List<Message>> oldMesgList : chan.entrySet()){
                    for(Message msg: oldMesgList.getValue() ){
                        handle.put(msg); // Loses Fifo change to List of Messages
                        // loses any Happens-Before relationship that might exist between these messages
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(objectinputstream != null){
                    objectinputstream.close();
                }
            }


            myMarkerColor = MarkerCustom.WHITE;
            Message markerMessage = new Message(this.id, "Restore",0, null);


            outgoingChannels.get(0).put(markerMessage);
            System.out.println("Restored to State, Node: " + this.id + " State: " + this.state);

            //now restore the incoming messages that occured previously
        }
        else{
            if(fileToRead.hasNextInt()){
                state = fileToRead.nextInt();
                myMarkerColor = MarkerCustom.WHITE; // just make sure the marker color is set to White.
            }
        }
    }

}