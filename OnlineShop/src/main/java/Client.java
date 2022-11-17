import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String hostAddress;
        int tcpPort;
        int udpPort;

        if (args.length != 3) {
            System.out.println("ERROR: Provide 3 arguments");
            System.out.println("\t(1) <hostAddress>: the address of the server");
            System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
            System.out.println("\t(3) <udpPort>: the port number for UDP connection");
            System.exit(-1);
        }

        hostAddress = args[0];
        tcpPort = Integer.parseInt(args[1]);
        udpPort = Integer.parseInt(args[2]);

        CanCommunicate connection = new TcpConnection(hostAddress, tcpPort);
        CanShop shopper = new Shopper(connection);

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String cmd = sc.nextLine();
            String[] tokens = cmd.split(" ");

            if (tokens[0].equals("setmode")) {
                if (tokens[1].equals("T")) {
                    System.out.println("Setting mode to TCP");
                    connection = new TcpConnection(hostAddress, tcpPort);
                    shopper = new Shopper(connection);
                } else if (tokens[1].equals("U")) {
                    System.out.println("Setting mode to UDP");
                    connection = new UdpConnection(hostAddress, udpPort);
                    shopper = new Shopper(connection);
                } else {
                    System.out.println("Mode not recognized. Not changing the mode.");
                }
            } else if (tokens[0].equals("purchase")) {
                String userName = tokens[1];
                String productName = tokens[2];
                int quantity = Integer.parseInt(tokens[3]);
                int orderId = shopper.purchaseProduct(userName, productName, quantity);
                if (orderId == -2) {
                    System.out.println("Not Available - Not enough items.");
                }
                else if (orderId == -1) {
                    System.out.println("Not Available - We do not sell this product.");
                } else {
                    System.out.println("Your order has been placed, " +
                            orderId + " " +
                            userName + " " +
                            productName + " " +
                            quantity);
                }
            } else if (tokens[0].equals("cancel")) {
                int orderId = Integer.parseInt(tokens[1]);
                int canceledOrderId = shopper.cancelOrder(orderId);
                if (canceledOrderId == -1){
                    System.out.println(orderId + " not found, no such order.");
                } else {
                    System.out.println("Order " + orderId + " is canceled.");
                }
            } else if (tokens[0].equals("search")) {
                String userName = tokens[1];
                Orders allOrders = shopper.search(userName);
                if (allOrders.size() == 0){
                    System.out.println("No order found for " + userName + ".");
                } else {
                    System.out.println(allOrders.toString());
                }

            } else if (tokens[0].equals("list")) {
                Inventories allInventory = shopper.list();
                System.out.println(allInventory.toString());
            } else {
                System.out.println("ERROR: No such command");
            }
        }
    }
}
