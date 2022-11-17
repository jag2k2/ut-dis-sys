import java.io.Serializable;

public class Order implements Serializable {
    public int orderId;
    public String productName;
    public int quantityPurchased;

    public String username;

    public Order(int orderId, String productName, int quantityPurchased, String buyer){
        this.orderId = orderId;
        this.productName = productName;
        this.quantityPurchased = quantityPurchased;
        this.username = buyer;
    }
}
