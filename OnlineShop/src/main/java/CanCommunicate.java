import java.io.IOException;

public interface CanCommunicate {
    void connect() throws IOException;
    void send(String command) throws IOException;
    Object receive() throws IOException;
    void close() throws IOException;
}
