public class Message {
    public int id;
    public String command;
    public int payload;
    
    public Message(int id, String command, int payload){
        this.id = id;
        this.command = command;
        this.payload = payload;
    }
}