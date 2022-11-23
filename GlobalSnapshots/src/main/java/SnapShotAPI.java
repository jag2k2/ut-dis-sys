import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SnapShotAPI {

    public void receive(MarkerCustom markerColor, int jth) throws IOException, InterruptedException;

    public void receive(Message program_message);
    public void turnRed() throws IOException, InterruptedException;

    public boolean getColor();
    public void saveState() throws IOException;
}
