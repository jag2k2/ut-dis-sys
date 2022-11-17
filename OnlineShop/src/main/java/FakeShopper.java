public class FakeShopper implements CanShop{

    @Override
    public int purchaseProduct(String userName, String productName, int quantity) {
        if(productName.equals("Gadgets")){
            return -1;
        }
        if(quantity > 5000){
            return -2;
        }
        return 2002;
    }

    @Override
    public int cancelOrder(int orderId) {
        if (orderId == 2003)
            return -1;
        else
            return orderId;
    }

    @Override
    public Orders search(String userName) {
        Orders allOrders = new Orders();
        if (userName.equals("Blake")){
            return allOrders;
        }
        allOrders.add(new Order(154, "Gizmo", 3, "Blake"));
        allOrders.add(new Order(4897, "Widget", 50, "Blake"));
        allOrders.add(new Order(13, "Thingy", 5871, "Blake"));
        return allOrders;
    }

    @Override
    public Inventories list() {
        Inventories allInventories = new Inventories();
        allInventories.add(new Inventory("Games", 58));
        allInventories.add(new Inventory("Apples", 451));
        allInventories.add(new Inventory("Gizmo", 1398));
        allInventories.add(new Inventory("Widget", 0));
        allInventories.add(new Inventory("Thingy", 871));
        return allInventories;
    }
}
