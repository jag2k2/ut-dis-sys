public class QueuePacket {
    public boolean write; //write is true read is false
    public CommsAbstract concreteWriter;


    public QueuePacket(boolean w, CommsAbstract writer ){
        this.write = w;
        this.concreteWriter = writer;
    }
}