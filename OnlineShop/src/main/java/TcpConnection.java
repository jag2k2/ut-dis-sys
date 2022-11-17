import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class TcpConnection implements CanCommunicate {

    private Socket serverConnection;
    private ObjectInputStream responseStream;
    private PrintStream commandStream;
    private final String hostName;
    private final int tcpPort;

    public TcpConnection(String hostName, int port){
        this.hostName = hostName;
        this.tcpPort = port;
    }

    public void connect() throws IOException {
        serverConnection = new Socket(hostName, tcpPort);
        commandStream = new PrintStream(serverConnection.getOutputStream());
        responseStream = new ObjectInputStream(serverConnection.getInputStream());
    }

    public void send(String command){
        commandStream.println(command);
        commandStream.flush();
    }

    public Object receive(){
        Object receiveObject = null;
        try {
            receiveObject = responseStream.readObject();
        } catch (IOException | ClassNotFoundException err) {
            System.out.println("TCP receive error");
            System.out.println(err.toString());
        }
        return receiveObject;
    }

    public void close() throws IOException {
        serverConnection.close();
    }
}
