import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpConnection implements CanCommunicate {
    private DatagramSocket dataSocket;
    private InetAddress address;
    private final String hostName;
    private final int udpPort;
    private final int receiveBufferLength = 10000;

    public UdpConnection(String hostName, int port){
        this.hostName = hostName;
        this.udpPort = port;
    }

    @Override
    public void connect() throws IOException {
        this.dataSocket = new DatagramSocket();
        this.address = InetAddress.getByName(hostName);
    }

    @Override
    public void send(String command) throws IOException{
        byte[] sendBuffer = command.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, udpPort);
        dataSocket.send(sendPacket);
    }

    @Override
    public Object receive() throws IOException{
        Object receivedObject = null;
        byte[] receiveBuffer = new byte[receiveBufferLength];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        dataSocket.receive(receivePacket);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receiveBuffer);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        try {
            receivedObject = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException err){
            System.out.println(err.toString());
        }
        return receivedObject;
    }

    @Override
    public void close() throws IOException {
        dataSocket.close();
    }
}









