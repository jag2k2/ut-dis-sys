import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Orders implements Serializable {
    private final List<Order> allOrders;

    public Orders() {
        this.allOrders = new ArrayList<>();
    }

    public void add(Order order) {
        allOrders.add(order);
    }

    public int size(){
        return allOrders.size();
    }

    @Override
    public String toString() {
        String output = "";
        for (Order order : allOrders) {
            output = output.concat(order.orderId + " " + order.productName + " " + order.quantityPurchased + "\n");
        }
        return output;
    }

    public String saveOrdersToString(){
        String output = "";
        for (Order order : allOrders) {
            output = output.concat(order.orderId + " " + order.productName + " " + order.quantityPurchased + " " + order.username + "\n");
        }
        return output;
    }

    public List<Order> getList(){
        return this.allOrders;
    }
}
