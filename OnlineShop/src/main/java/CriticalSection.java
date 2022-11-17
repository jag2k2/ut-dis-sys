import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CriticalSection {
    private final File inventoriesFile;
    private final File ordersFile;

    private PrintWriter inventoriesWriter;
    private BufferedReader inventoriesReader;

    private PrintWriter ordersWriter;
    private final BufferedReader ordersReaders;

    public CriticalSection(String nameOfResource) throws IOException {
        // constructor to initialize everything should only be called once when the server is initialized
        inventoriesFile = new File(nameOfResource);
        String ordersName = "orders_generated.txt";
        ordersFile = new File(ordersName);

        if(inventoriesFile.exists()){
            System.out.println("Inventory Loaded");
            inventoriesWriter = new PrintWriter(new FileWriter(inventoriesFile, true));
            inventoriesReader = new BufferedReader(new FileReader(inventoriesFile));
        }
        else{
            System.out.println("Server contains no  critical resource of file named: " + nameOfResource);
        }

        if (!ordersFile.exists()) {
            ordersFile.createNewFile();
        }
        ordersReaders = new BufferedReader(new FileReader(ordersFile));
        ordersWriter = new PrintWriter(new FileWriter(ordersFile, true));
    }

    public void close() throws IOException {
        if(inventoriesWriter != null) {
            inventoriesWriter.close();
        }
        if(inventoriesReader != null) {
            inventoriesReader.close();
        }

        if(ordersReaders != null){
            ordersReaders.close();
        }
        if(ordersWriter != null){
            ordersWriter.close();
        }
    }

    public void saveInventory(Inventories newInventories) throws IOException {
        //lets do a little hack and delete the entire file then overwrite
        inventoriesWriter.close();
        inventoriesWriter = new PrintWriter(new FileWriter(inventoriesFile, false)); //deletes file automatically

        //overwrites entire file with newInventories Inventories object
        inventoriesWriter.write(newInventories.toString());

        //now once done close the sharedWriter and open the original
        inventoriesWriter.close();
        inventoriesWriter = new PrintWriter(new FileWriter(inventoriesFile, true)); //ready for reads without deleting the file
    }

    public void saveOrders(Orders newOrders) throws IOException {
        //lets do a little hack and delete the entire file then overwrite
        ordersWriter.close();
        ordersWriter = new PrintWriter(new FileWriter(ordersFile, false)); //deletes file automatically

        //overwrites entire file with newInventories Inventories object
        ordersWriter.write(newOrders.saveOrdersToString());

        //now once done close the sharedWriter and open the original
        ordersWriter.close();
        ordersWriter = new PrintWriter(new FileWriter(ordersFile, true)); //ready for reads without deleting the file
    }

    public Inventories getAllItems() {
        Inventories allInventories = new Inventories();

        List<String> rawInventory = readInventoryLines();

        assert rawInventory != null;
        for (String item : rawInventory) {
            String[] fullArray = item.split(" ");
            String name = fullArray[0];
            String quantity = fullArray[1];

            allInventories.add(new Inventory(name, Integer.parseInt(quantity)));
        }

        return allInventories;
    }

    public Orders getAllOrders() {
        Orders allOrders = new Orders();
        List<String> rawOrdersList = readOrdersLines();

        assert rawOrdersList != null;
        for (String item : rawOrdersList) {
            String[] fullArray = item.split(" ");
            int orderNum = Integer.parseInt(fullArray[0]);
            String itemName = fullArray[1];
            int itemQuantity = Integer.parseInt(fullArray[2]);
            String orderUser = fullArray[3];

            allOrders.add(new Order(orderNum, itemName, itemQuantity, orderUser));
        }
        return allOrders;
    }

    private List<String> readInventoryLines() {
        ArrayList<String> result = new ArrayList<>();

        try {
            Scanner reader = new Scanner(inventoriesFile);
            while (reader.hasNextLine()) {
                result.add(reader.nextLine());
            }
        }catch(FileNotFoundException e){
            return null;
        }
        return result;
    }

    private List<String> readOrdersLines() {
        ArrayList<String> result = new ArrayList<>();

        try {
            Scanner reader = new Scanner(ordersFile);

            while (reader.hasNext()) {
                result.add(reader.nextLine());
            }
        }catch(FileNotFoundException e){
            return null;
        }
        return result;
    }
}
