import java.util.concurrent.BlockingQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class IncomingChannels{
    private final int[] incomingChannelIDs;
    private final Map<Integer, List<Message>> chan = new HashMap<>();
    private final Map<Integer, Boolean> closed = new HashMap<>();

    public IncomingChannels(int[] incomingChannelIDs) {
        this.incomingChannelIDs = incomingChannelIDs;
        initializeChan();
        initializeClosed();
    }

    public void initializeChan() {
        for (int chanId : this.incomingChannelIDs) {
            chan.put(chanId, new ArrayList<>());
        }
    }

    public void initializeClosed() {
        for (int chanId : this.incomingChannelIDs) {
            closed.put(chanId, false);
        }
    }

    public void close(int chanId) {
        closed.put(chanId, true);
    }

    public boolean isClosed(int chanId) {
        return closed.get(chanId);
    }

    public boolean allClosed() {
        return !closed.containsValue(false);
    }

    public void storeTransitMessage(Message message) {
        chan.get(message.id).add(message);
    }

    public void restoreTransitMessages(BlockingQueue<Message> handle){
        for (Map.Entry<Integer, List<Message>> entry : this.chan.entrySet()) {
            for (Message message : entry.getValue()) {
                try {
                    handle.put(message);
                } catch (InterruptedException err) {
                    System.out.println("restoreTransitMessages: " + err.toString());
                }
            }
        }
    }
}