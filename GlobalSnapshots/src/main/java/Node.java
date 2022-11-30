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
    private Color color;
    private final Map<Integer, List<Message>> chan = new HashMap<>();
    private final Map<Integer, Boolean> closed = new HashMap<>();

    private int state;
    private int savedState;

    public Node(int id, BlockingQueue<Message> handle, int[] incomingChannelIDs, List<BlockingQueue<Message>> outgoingChannels){
        this.id = id;
        this.handle = handle;
        this.incomingChannelIDs = incomingChannelIDs;
        this.outgoingChannels = outgoingChannels;
        this.color = Color.WHITE;

        this.state = 0;
        for (int chanId : incomingChannelIDs) {
            closed.put(chanId, false);
            chan.put(chanId, new ArrayList<>());
        }
    }

    @Override
    public void run() {
        try {
            EventLogger eventLogger = new EventLogger(id);
            boolean exit = false;
            do {
                Message receivedMsg = handle.take();
                String command = receivedMsg.command;
                if (command == "ProgMsg") {
                    Thread.sleep(100);
                    this.state += this.id;                     
                    sendMsgToNeighbors(new Message(id, command));
                } 
                else if (command == "Marker") {
                    if (color == Color.WHITE) {
                        turnRed();
                    }
                }
                else if (command == "Restore") {
                    if (color == Color.RED) {
                        turnWhite();
                    }
                } 
                else if (command == "Exit") {
                    sendMsgToNeighbors(new Message(id, command));
                    exit = true;
                }
                eventLogger.logMessage(receivedMsg, state);
            } while (exit == false);
            eventLogger.close();
        } catch (Exception err) {
            System.out.println(err.toString());
        }
    }

    public void turnRed() {
        //savedState = state;
        this.color = Color.RED;     
        sendMsgToNeighbors(new Message(id, "Marker"));           // forward Marker but with new id and state
    }

    public void turnWhite() {
        //state = savedState;
        this.color = Color.WHITE;    
        sendMsgToNeighbors(new Message(id, "Restore"));           // forward Restore but with new id and state
    }

    public void sendMsgToNeighbors(Message message) {
        for (BlockingQueue<Message> handle : this.outgoingChannels){         // forward message to all outgoing channels
            try {
                handle.put(message);
            } catch (InterruptedException err) {
                System.out.println(err.toString());
            }
        }
    }
}