import java.io.IOException;

public class Shopper implements CanShop {
    private final CanCommunicate connection;

    public Shopper(CanCommunicate connection){
        this.connection = connection;
    }

    @Override
    public int purchaseProduct(String userName, String productName, int quantity) {
        int purchaseOrderId = -1;
        try {
            connection.connect();
            connection.send("purchase " + userName + " " + productName + " " + quantity);
            purchaseOrderId = (int) connection.receive();
            connection.close();
        } catch (IOException err) {
            System.out.println(err.toString());
        }
        return purchaseOrderId;
    }

    @Override
    public int cancelOrder(int orderId) {
        int canceledOrder = -1;
        try {
            connection.connect();
            connection.send("cancel " + orderId);
            canceledOrder = (int) connection.receive();
            connection.close();
        } catch (IOException err) {
            System.out.println(err.toString());
        }
        return canceledOrder;
    }

    @Override
    public Orders search(String userName) {
        Orders allOrders = new Orders();
        try {
            connection.connect();
            connection.send("search " + userName);
            allOrders = (Orders) connection.receive();
            connection.close();
        } catch (IOException err) {
            System.out.println("shopper search error");
            System.out.println(err.toString());
        }
        return allOrders;
    }

    @Override
    public Inventories list() {
        Inventories allInventories = new Inventories();
        try {
            connection.connect();
            connection.send("list");
            allInventories = (Inventories) connection.receive();
            connection.close();
        } catch (IOException err) {
            System.out.println(err.toString());
        }

        return allInventories;
    }
}

