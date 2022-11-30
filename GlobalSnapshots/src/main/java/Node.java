import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

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
            String DebugFilename = "debug_out" + String.valueOf(id) + ".txt";
            FileWriter fileWriter = new FileWriter(DebugFilename);
            while (true) {
                    Message receivedMsg = handle.take();
                    String command = receivedMsg.command;
                    if(receivedMsg.marker != null){
                        receive(receivedMsg.marker,  receivedMsg.id);
                    }
                    receive(receivedMsg);
                    if (command == "AppMsg") {
                        fileWriter.write("\n" + state + "\n");
                        state = receivedMsg.payload + 1;
                        Thread.sleep(100);
                        fileWriter.write("Process" + String.valueOf(id) + ": AppMsg from chan" + String.valueOf(receivedMsg.id) + ", state: " + String.valueOf(state) + "\n");
                        Message responseMessage = new Message(id, "AppMsg", state, null);
                        outgoingChannels.get(0).put(responseMessage);
                    } 
                    else if (command == "Snapshot") {
                        localSnapShot();
                    }
                    else if (command == "Restore") {
                        System.out.println("About to Restore");
                        //put the restore message in the outgoing queue before restoring the state else it will loop
                        //it will loop in the Restore "state" message


                        //freeze the entire system first;

                        //then restore state
                        File snapshot = new File(filename);
                        if(snapshot.exists()) {

                            restoreState();
                            Message travelingRestore = new Message(id, "Restore", 0, null);
                            outgoingChannels.get(0).put(travelingRestore);
                            String cmd = "";
                            int tries = 0;
                            while( !cmd.equals("Restore") ){
                                Message recv = handle.poll();
                                if(recv == null){continue;}
                                cmd = recv.command;
                                //drop message that weren't previously saved.
                                //but once we receive back our Restore message from node4->node1 then
                                //not only have we cleared our handle we can gurantee that restore can now occur.
                            }
                            if(snapshot.delete()) {
                                restoreMessages();
                                fileWriter.write("ITS WORKING!!!");
                                fileWriter.write("Restored to State, Node: " + this.id + " State: " + this.state);
                                Message delete_msg = new Message(id, "Restore", 0, null);
                                outgoingChannels.get(0).put(delete_msg);
                                //Get The Program Started Again.
                                Message responseMessage = new Message(id, "AppMsg", state, null);
                                outgoingChannels.get(0).put(responseMessage);

                            }
                        }

                    } 
                    else if (command == "Exit") {
                        outgoingChannels.get(0).put(new Message(id, "Exit", 0, null));
                        fileWriter.close();
                        break;
                    }
                    fileWriter.flush();
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

    }

    public int getState(){
        return this.state;
    }

    public void localSnapShot() throws IOException, InterruptedException {
        turnRed();
    }

    public void restoreState() throws IOException {
        Scanner fileToRead = new Scanner(new File(filename));
        if(myMarkerColor != null) {
            if(myMarkerColor == MarkerCustom.RED) {
                state = fileToRead.nextInt();

            }

            else if(myMarkerColor == MarkerCustom.RED) {
                myMarkerColor = MarkerCustom.WHITE;
            }
            System.out.println(myMarkerColor);
        }
   }

   public void restoreMessages() throws IOException {
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

           for (Map.Entry<Integer, List<Message>> oldMesgList : chan.entrySet()) {
               for (Message msg : oldMesgList.getValue()) {
                   System.out.println("message is: " + msg.command + " " + msg.id + " " + msg.payload);

                   this.handle.put(msg); // Loses Fifo change to List of Messages
               }
           }


       } catch (Exception e) {
           e.printStackTrace();
       } finally {
           if (objectinputstream != null) {
               objectinputstream.close();
           }
       }
   }

}