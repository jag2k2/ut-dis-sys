import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpComms implements CommsAbstract{

    public DatagramSocket udpWriter;
    public DatagramPacket udpResponse;
    public String[] commandtokens;
    public  DatagramPacket receivedPacket;

    public UdpComms(DatagramSocket writer, String[] tokens){
        udpWriter = writer;
        commandtokens = tokens;
        udpResponse = null;
        receivedPacket = null;
    }

    @Override
    public void write() throws IOException {
        this.udpWriter.send(udpResponse);
    }

    public void write(DatagramPacket obj) throws IOException {
        this.udpResponse = obj;
        this.write();
    }
}
