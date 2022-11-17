import java.io.IOException;
import java.io.ObjectOutputStream;

public class TcpComms implements CommsAbstract{
    //unique identifier for client comms
    public ObjectOutputStream tcpWriter;

    public Object tcpResponse;
    public String[] commandTokens;

    public TcpComms(ObjectOutputStream writer, String[] tokens){
        this.tcpWriter = writer;
        this.commandTokens = tokens;
        this.tcpResponse = null;
    }

    @Override
    public void write() throws IOException {
        this.tcpWriter.writeObject(tcpResponse);
        //then clean up once done
        this.tcpWriter.close();
    }
    public void write(Object newObj) throws IOException {
        this.tcpResponse = newObj;
        this.write();
    }
}
