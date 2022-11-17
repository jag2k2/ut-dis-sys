import java.io.IOException;

public class DbShopper implements CanShop {
    private final CriticalSection criticalSection;

    public DbShopper(CriticalSection criticalSection){
        this.criticalSection = criticalSection;
    }

    @Override
    public int purchaseProduct(String userName, String productName, int quantity) {
        int resultSuccess = -3;
        int purchaseOrderId = -1;
        Inventories allItems = criticalSection.getAllItems();

        for (Inventory item : allItems.getList()) {
            if (item.productName.equals(productName)) {
                if ((item.productQuantity - quantity) >= 0) {
                    //the purchase can take place
                    item.productQuantity -= quantity;
                    resultSuccess = 1;
                } else {
                    resultSuccess = -2;  // Not Enough
                }
                break;
            } else {
                resultSuccess = -1;  // Do not sell
            }
        }

        //if purchase can take place return an order number
        Orders allOrders = criticalSection.getAllOrders();
        Order newPurchaseOrder = new Order(0, productName, quantity, userName);
        int newOrderID = 1;
        if (resultSuccess == 1) {
            //find the last successful purchase and append to the Orders list

            Order lastOrder;
            int index = allOrders.getList().size() - 1;


            if (index >= 0) {
                lastOrder = allOrders.getList().get(allOrders.getList().size() - 1);
                newOrderID = lastOrder.orderId + 1;
            }

            newPurchaseOrder.orderId = newOrderID;
            allOrders.add(newPurchaseOrder);

            //now write to  the file of Orders
            try {
                criticalSection.saveOrders(allOrders);
                criticalSection.saveInventory(allItems);
                purchaseOrderId = newOrderID;
            } catch (IOException e) {
                //return a failure error code
                purchaseOrderId = -3;
            }


        } else {
            //Inventory not found
            purchaseOrderId = resultSuccess;
        }
        return purchaseOrderId;
    }

    @Override
    public int cancelOrder(int orderToCancel) {
        int orderCanceled = -1;
        Inventories currentInventory = criticalSection.getAllItems();
        Orders allOrders = criticalSection.getAllOrders();
        Order cancelThisOrder = null;

        for (Order order : allOrders.getList()) {
            if (order.orderId == orderToCancel) {
                //cancelation logic, add back to the inventory before removing the Order
                for (Inventory item : currentInventory.getList()) {
                    if (item.productName.equals(order.productName)) {
                        item.productQuantity += order.quantityPurchased;
                    }
                }
                cancelThisOrder = order;
                orderCanceled = order.orderId;
            }
        }

        if (cancelThisOrder != null) {
            allOrders.getList().remove(cancelThisOrder);
        } else {
            orderCanceled = -1;
        }
        //now write to  the file of Orders and Inventories for persistence
        try {
            criticalSection.saveOrders(allOrders);
            criticalSection.saveInventory(currentInventory);
        } catch (IOException e) {
            //return a failure error code
            orderCanceled = -2;
        }
        return orderCanceled;
    }

    @Override
    public Orders search(String userName) {
        Orders allOrders = criticalSection.getAllOrders();
        Orders searchResult = new Orders();

        if( allOrders.getList().size() > 0) {
            for (Order y : allOrders.getList()) {
                if (y.username.equals(userName)) {
                    searchResult.add(y);
                }
            }
        }
        return searchResult;
    }

    @Override
    public Inventories list() {
        return criticalSection.getAllItems();
    }
}
