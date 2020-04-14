import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EchoListener implements Runnable {


    private DatagramPacket datagramPacket;
    private Random random = new Random();
    private static ArrayList<User> addresses = new ArrayList<>();
    private DatagramSocket dgs;
    private String initmsg;
    private boolean join = false;


    EchoListener(DatagramPacket datagramPacket, String initmsg, DatagramSocket dgs) throws SocketException {
        this.datagramPacket = datagramPacket;
        this.initmsg = initmsg;
        this.dgs = dgs;
    }

    @Override
    public void run() {
        try {
            boolean q = false;
            for (User userX : addresses){
                if (datagramPacket.getPort() == userX.getDatagramPacket().getPort() && datagramPacket.getAddress().toString().equals( userX.getDatagramPacket().getAddress().toString())){
                    q = true;
                }

            }
            if (q != true){
                System.out.println("a new udp pakcet came in, ip and port saved as new User and alias = " + initmsg);
                User user = new User(datagramPacket);
                addresses.add(user);
                user.setAlias(initmsg);
                join = true;

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            String msg = initmsg;

            if (msg != null) {

                if (msg.startsWith("close")) {
                    //Using iterator to avoid ConcurrentModificationException
                    try {
                        List<User> toRemove = new ArrayList<User>();
                        for (User user : addresses) {
                            if (datagramPacket.getAddress().toString().equals( user.getDatagramPacket().getAddress().toString()) && user.getDatagramPacket().getPort() == datagramPacket.getPort()) {
                                toRemove.add(user);
                                System.out.println("Information regarding" + user.getAlias() + " is now being deleted");
                                System.out.println("There are now: "+ addresses.size() +" active clients");
                            }
                        }
                        addresses.removeAll(toRemove);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            for (User user : addresses) {

                if (user.getDatagramPacket().getPort() == datagramPacket.getPort() && datagramPacket.getAddress().toString().equals(user.getDatagramPacket().getAddress().toString())) {

                    String alias = user.getAlias();
                    String msgEcho;
                    if (join != true){
                        msgEcho = (msg + " from " + alias);
                    }else {
                        msgEcho = (alias + " joined the chat");
                    }

                    for (User userX : addresses) { // send message to all
                        byte[] toSend = msgEcho.getBytes();
                        DatagramPacket datagramPacket = new DatagramPacket(toSend, toSend.length, userX.getDatagramPacket().getAddress(), userX.getDatagramPacket().getPort());
                        dgs.send(datagramPacket);
                    }
                    System.out.println(msgEcho);

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(random.nextInt(200));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
