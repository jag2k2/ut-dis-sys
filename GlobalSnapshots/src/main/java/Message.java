public class Message {
    public String command;
    public int payload;
    
    public Message(String command, int payload){
        this.command = command;
        this.payload = payload;
    }
}