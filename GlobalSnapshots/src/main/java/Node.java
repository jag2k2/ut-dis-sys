public class Node implements Runnable {
    private final String name;
    
    public Node(String name){
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("Hello " + name);
    }
}