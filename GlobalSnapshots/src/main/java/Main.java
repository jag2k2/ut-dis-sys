public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        Thread nodeThread1 = new Thread(new Node("Jeff"));
        Thread nodeThread2 = new Thread(new Node("Mindi"));
        Thread nodeThread3 = new Thread(new Node("Aubrey"));
        Thread nodeThread4 = new Thread(new Node("Brooklyn"));

        nodeThread1.start();
        nodeThread2.start();
        nodeThread3.start();
        nodeThread4.start();

        try {
            nodeThread1.join();
            nodeThread2.join();
            nodeThread3.join();
            nodeThread4.join();
        } catch(InterruptedException err){
            System.out.println(err.toString());
        }

        System.out.println("Exiting");
    }
}