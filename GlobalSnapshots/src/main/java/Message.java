import java.io.Serializable;

public class Message implements Serializable {
    public int id;
    public String command;
    public int payload;
    public MarkerCustom marker;
    
    public Message(int id, String command, int payload, MarkerCustom markerMesg){
        this.id = id;
        this.command = command;
        this.payload = payload;
        this.marker = markerMesg;
    }
}