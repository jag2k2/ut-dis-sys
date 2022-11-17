import java.io.Serializable;

public class Inventory implements Serializable {
    public String productName;
    public int productQuantity;

    public Inventory(String productName, int productQuantity){
        this.productName = productName;
        this.productQuantity = productQuantity;
    }
}
