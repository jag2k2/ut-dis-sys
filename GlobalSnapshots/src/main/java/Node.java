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
        this.restoreColor = Color.WHITE;
        this.state = id;
        this.savedState = this.state;

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
            Message forwardMessage = new Message(this.id, command);
            if (command == "ProgMsg") {
                if(restoreColor == Color.WHITE){                 // If the node is restoring, it should stop responding to prog messages
                    insertProcessingTime(250);                   // Simulate 
                    this.state += this.id;                       // Update node state by node it
                    sendMsgToNeighbors(forwardMessage);          // Forward ProgMsg to all outgoing neighbors
                    if (snapColor == Color.RED && closed.get(chanId) == false) {
                        chan.get(chanId).add(receivedMsg);       // Save any "white" program messages when snapshot is in progress
                    }
                }
            } 
            else if (command == "MARKER") {
                if (snapColor == Color.WHITE && restoreColor == Color.WHITE) { // No snapshot or restore is in progress. Begin snapshot.
                    savedState = state;                          // Save state
                    snapColor = Color.RED;                       // Red means a snapshot is in progress
                    initializeChan();                            // Initialize chan collection
                    initializeClosed();                          // Initialize closed collection
                    sendMsgToNeighbors(forwardMessage);          // Forward Marker to all outgoing neighbors
                }
                closed.put(chanId, true);                        // Upon receive "Marker" from a channel, close that channel
                boolean allClosed = !closed.containsValue(false);
                if (allClosed == true){                          // When received "Marker" from all incoming channels,
                    snapColor = Color.WHITE;                     // Snapshot is done.  Can reset the snapColor to WHITE
                }
            }
            else if (command == "RESTORE") {
                if (restoreColor == Color.WHITE && snapColor == Color.WHITE) { // No restore or snapshot is in progress. Begin a restore.
                    restoreColor = Color.RED;                     // Red means restore is in progress
                    initializeClosed();                           // Initialize restoreClosed collection
                    sendMsgToNeighbors(forwardMessage);           // Forward Restore to all outgoing neighbors
                }
                closed.put(chanId, true);                         // Upon receive "Restore" from a channel, close that channel      
                boolean allClosed = !closed.containsValue(false);    
                if (allClosed == true) {                          // When received "Restore" from all incoming channels, it is safe to restore
                    state = savedState;                           // Restore state
                    restoreTransitMessages();                     // Restore state messages
                    restoreColor = Color.WHITE;                   // Restore is done.  Can reset the restoreColor to WHITE
                }
            } 
            else if (command == "Exit") {
                sendMsgToNeighbors(forwardMessage);               // Forward "Exit" to all outgoing neighbors
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

    public void insertProcessingTime(int milliseconds) {
                try {
                    Thread.sleep(milliseconds);
                } catch (InterruptedException err) {
                    System.out.println("Node" + String.valueOf(this.id) + " sleep");
                }
    }
}