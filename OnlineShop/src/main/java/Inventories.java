import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventories implements Serializable {
    private List<Inventory> allInventories;

    public Inventories() {
        this.allInventories = new ArrayList<>();
    }

    public void add(Inventory inventory) {
        allInventories.add(inventory);
    }

    @Override
    public String toString() {
        String output = "";
        Collections.sort(allInventories, new SortByName());
        for (Inventory inventory : allInventories) {
            output = output.concat(inventory.productName + " " + inventory.productQuantity + "\n");
        }
        return output;
    }

    public List<Inventory> getList(){
        return this.allInventories;
    }
}
