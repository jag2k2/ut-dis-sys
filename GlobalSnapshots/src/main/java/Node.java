import java.util.concurrent.BlockingQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Node implements Runnable {
    private final int id;
    private final BlockingQueue<Message> handle;
    private final int[] incomingChannelIDs;
    private final List<BlockingQueue<Message>> outgoingChannels;
    private Color snapColor;
    private Color restoreColor;
    private final Map<Integer, List<Message>> chan = new HashMap<>();
    private final Map<Integer, Boolean> closed = new HashMap<>();

    private int state;
    private int savedState;

    public Node(int id, BlockingQueue<Message> handle, int[] incomingChannelIDs, List<BlockingQueue<Message>> outgoingChannels){
        this.id = id;
        this.handle = handle;
        this.incomingChannelIDs = incomingChannelIDs;
        this.outgoingChannels = outgoingChannels;
        this.snapColor = Color.WHITE;
        this.restoreColor = Color.BLUE;
        this.state = 0;

        initializeChan();
        initializeClosed();
    }

    @Override
    public void run() {
        EventLogger eventLogger = new EventLogger(this.id);
        boolean exit = false;
        do {
            Message receivedMsg = new Message(this.id, "default");
            try {
                receivedMsg = handle.take();
            } catch (Exception err) {
                System.out.println("Node" + String.valueOf(this.id) + " queue take: " + err.toString());
            }
            String command = receivedMsg.command;
            int chanId = receivedMsg.id;
            if (command == "ProgMsg") {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException err) {
                    System.out.println("Node" + String.valueOf(this.id) + " sleep");
                }
                this.state += this.id;                     
                sendMsgToNeighbors(new Message(id, command));
                if (snapColor == Color.RED && closed.get(chanId) == false) {
                    chan.get(chanId).add(receivedMsg);
                }
            } 
            else if (command == "Marker") {
                if (snapColor == Color.WHITE) {                                 // turn red
                    savedState = state;
                    snapColor = Color.RED;     
                    sendMsgToNeighbors(new Message(id, "Marker"));          // forward Marker but with new id and state
                }
                closed.put(chanId, true);
            }
            else if (command == "Restore") {
                if (restoreColor == Color.BLUE) {                                   // turn white
                    state = savedState;
                    restoreColor = Color.GREEN;    
                    sendMsgToNeighbors(new Message(id, "Restore"));         // forward Restore but with new id and state
                }
                closed.put(chanId, false);
                boolean allOpen = !chan.containsValue(true);
                if (allOpen == true) {
                    restoreTransitMessages();
                    initializeChan();
                }
            } 
            else if (command == "Exit") {
                sendMsgToNeighbors(new Message(id, command));
                exit = true;
            }
            eventLogger.logMessage(receivedMsg, state);
        } while (exit == false);
        eventLogger.close();
    }

    public void initializeChan() {
        for (int chanId : incomingChannelIDs) {
            chan.put(chanId, new ArrayList<>());
        }
    }

    public void initializeClosed() {
        for (int chanId : incomingChannelIDs) {
            closed.put(chanId, false);
        }
    }

    public void sendMsgToNeighbors(Message message) {
        for (BlockingQueue<Message> handle : this.outgoingChannels){         // forward message to all outgoing channels
            try {
                handle.put(message);
            } catch (InterruptedException err) {
                System.out.println("Node" + String.valueOf(this.id) + ": sendMsgToNeighbors: " + err.toString());
            }
        }
    }

    public void restoreTransitMessages(){
        for (Map.Entry<Integer, List<Message>> entry : chan.entrySet()) {
            for (Message message : entry.getValue()) {
                try {
                    handle.put(message);
                } catch (InterruptedException err) {
                    System.out.println("Node" + String.valueOf(this.id) + ": restoreTransitMessages: " + err.toString());
                }
            }
        }
    }
}