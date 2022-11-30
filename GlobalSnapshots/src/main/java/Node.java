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
    private final Map<Integer, Boolean> snapClosed = new HashMap<>();
    private final Map<Integer, Boolean> restoreClosed = new HashMap<>();
    
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
        initializeSnapClosed();
        initializeRestoreClosed();
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
                if(restoreColor == Color.WHITE){
                    insertProcessingTime(250);
                    this.state += this.id;                     
                    sendMsgToNeighbors(forwardMessage);
                    if (snapColor == Color.RED && snapClosed.get(chanId) == false) {
                        chan.get(chanId).add(receivedMsg);
                    }
                }
            } 
            else if (command == "MARKER") {
                if (snapColor == Color.WHITE) {                  // No snapshot is in progress.  Start a new one
                    savedState = state;                          // Save state
                    snapColor = Color.RED;                       // Red means a snapshot is in progress
                    initializeChan();
                    initializeSnapClosed();
                    sendMsgToNeighbors(forwardMessage);          // Forward Marker to neighbors
                }
                snapClosed.put(chanId, true);
                boolean allClosed = !snapClosed.containsValue(false);
                if (allClosed == true){
                    snapColor = Color.WHITE;
                }
            }
            else if (command == "RESTORE") {
                if (restoreColor == Color.WHITE && snapColor == Color.WHITE) {  // No restore or snapshot is in progress.
                    restoreColor = Color.RED;                     // Red means restore is in progress
                    setAllRestoreClosed();
                    sendMsgToNeighbors(forwardMessage);           // Forward Restore to neighbors
                }
                restoreClosed.put(chanId, false);                        
                boolean allOpen = !restoreClosed.containsValue(true);    // Only restore transit messages when all channels are open again.
                if (allOpen == true) {
                    state = savedState;                           // Restore state.
                    restoreTransitMessages();
                    restoreColor = Color.WHITE;
                }
            } 
            else if (command == "Exit") {
                sendMsgToNeighbors(forwardMessage);
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

    public void initializeSnapClosed() {
        for (int chanId : incomingChannelIDs) {
            snapClosed.put(chanId, false);
        }
    }

    public void initializeRestoreClosed() {
        for (int chanId : incomingChannelIDs) {
            restoreClosed.put(chanId, false);
        }   
    }

    public void setAllRestoreClosed() {
        for (int chanId : incomingChannelIDs) {
            restoreClosed.put(chanId, true);
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
                    System.out.println("Node" + String.valueOf(this.id) + ": Restoring message!");
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