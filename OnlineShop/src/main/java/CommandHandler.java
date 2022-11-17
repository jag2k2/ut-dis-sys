import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.util.concurrent.ArrayBlockingQueue;

public class CommandHandler implements Runnable {
    public CriticalSection resourceToUseWithAQueue;
    public ArrayBlockingQueue<QueuePacket> sharedQueue;
    public CanShop shopper;

    public CommandHandler(ArrayBlockingQueue<QueuePacket> sharedQueue, CriticalSection criticalSection, CanShop shopper) {
        this.sharedQueue = sharedQueue;
        this.resourceToUseWithAQueue = criticalSection;
        this.shopper = shopper;
    }

    @Override
    public void run() {

        try {
            boolean isTcp = true;
            while (true) {
                try {
                    QueuePacket queueObj = sharedQueue.take();
                    String[] tokens;
                    ByteArrayOutputStream byteArrayOutputStream = null;
                    ObjectOutputStream responseWriter = null;

                    if ((queueObj.concreteWriter instanceof TcpComms)) {
                        //parse according to a tcp packet
                        isTcp = true;
                        tokens = ((TcpComms) queueObj.concreteWriter).commandTokens;

                    } else if (queueObj.concreteWriter instanceof UdpComms) {
                        // parse according to a udp packet
                        isTcp = false;
                        tokens = ((UdpComms) queueObj.concreteWriter).commandtokens;
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        responseWriter = new ObjectOutputStream(byteArrayOutputStream);
                    } else {
                        continue;
                    }

                    switch (tokens[0]) {
                        case "purchase": {
                            String userName = tokens[1];
                            String productName = tokens[2];
                            int quantity = Integer.parseInt(tokens[3]);

                            int purchaseOrderId = shopper.purchaseProduct(userName, productName, quantity);

                            if (isTcp) {
                                this.send(((TcpComms) queueObj.concreteWriter), purchaseOrderId);
                            } else {
                                responseWriter.writeObject(purchaseOrderId);
                                this.send(((UdpComms) queueObj.concreteWriter), responseWriter, byteArrayOutputStream);
                            }


                            break;
                        }
                        case "cancel": {
                            int orderToCancel = Integer.parseInt(tokens[1]);
                            int orderCanceled = shopper.cancelOrder(orderToCancel);

                            if (isTcp) {
                                this.send(((TcpComms) queueObj.concreteWriter), orderCanceled);
                            } else {
                                responseWriter.writeObject(orderCanceled);
                                this.send(((UdpComms) queueObj.concreteWriter), responseWriter, byteArrayOutputStream);
                            }
                            break;
                    }
                        case "search": {
                            String userName = tokens[1];
                            Orders searchResult = shopper.search(userName);

                            if (isTcp) {

                                this.send(((TcpComms) queueObj.concreteWriter), searchResult);
                            } else {
                                responseWriter.writeObject(searchResult);
                                this.send(((UdpComms) queueObj.concreteWriter), responseWriter, byteArrayOutputStream);
                            }


                            break;
                        }
                        case "list":
                            Inventories allInventories = shopper.list();

                            if (isTcp) {
                                this.send(((TcpComms) queueObj.concreteWriter), allInventories);
                            } else {
                                responseWriter.writeObject(allInventories);
                                this.send(((UdpComms) queueObj.concreteWriter), responseWriter, byteArrayOutputStream);
                            }
                            break;
                    }

                } catch (InterruptedException e) {
                    System.out.println("^^ exiting due to the above exception ^^");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Command Handler is closing!!!!");
            e.printStackTrace();
        }

    }

    public void send(TcpComms writer, Object tcpResponse) throws IOException {
        writer.write(tcpResponse);
    }

    public void send(UdpComms writer,
                     ObjectOutputStream responseWriter,
                     ByteArrayOutputStream byteArrayOutputStream) throws IOException {
        DatagramPacket realUdpResponse = createUdpPacket(responseWriter, byteArrayOutputStream, writer);

        writer.write(realUdpResponse);
    }

    public DatagramPacket createUdpPacket(ObjectOutputStream responseWriter,
                                          ByteArrayOutputStream byteArrayOutputStream,
                                          UdpComms context) throws IOException {
        responseWriter.flush();
        byte[] responseBuffer = byteArrayOutputStream.toByteArray();
        return new DatagramPacket(
                responseBuffer,
                responseBuffer.length,
                context.receivedPacket.getAddress(),
                context.receivedPacket.getPort()
        );
    }
}