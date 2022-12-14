import java.io.FileWriter;
import java.io.IOException;

public class EventLogger {
    private final int processId;
    private FileWriter fileWriter;

    public EventLogger(int processId) {
        this.processId = processId;
        String filename = "log" + String.valueOf(this.processId) + ".txt";
        try {
            this.fileWriter = new FileWriter(filename);
        } catch (IOException err) {
            System.out.println("EventLogger: " + err.toString());
        }
    }

    public void logMessage(Message receivedMsg, int state) {
        try {
            fileWriter.write("Pid" + String.valueOf(this.processId) + ": " + receivedMsg.command + " from chan" + String.valueOf(receivedMsg.id) + ", state: " + String.valueOf(state) + "\n");
            fileWriter.flush();
        } catch (IOException err) {
            System.out.println("EventLogger: " + err.toString());
        }
    }

    public void logString(String note) {
        try {
            fileWriter.write("Pid" + String.valueOf(this.processId) + ": " + note + "\n");
            fileWriter.flush();
        } catch (IOException err) {
            System.out.println("EventLogger: " + err.toString());
        }
    }

    public void close() {
        try {
            fileWriter.close();
        } catch (IOException err) {
            System.out.println("EventLogger: " + err.toString());
        }
    }
}