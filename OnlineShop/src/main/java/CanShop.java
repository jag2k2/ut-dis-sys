public interface CanShop {
    int purchaseProduct(String userName, String productName, int quantity);
    int cancelOrder(int orderId);
    Orders search(String userName);
    Inventories list();
}
