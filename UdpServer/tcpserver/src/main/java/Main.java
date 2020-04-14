import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Main{


    public static void main(String[] args) throws IOException, InterruptedException {

        DatagramSocket dgs = new DatagramSocket(6780);
        byte[] recv = new byte[1000]; //1500 er max


        while (true){
            String msgrecived = null;
            DatagramPacket dtg = null;

            try {
                dtg = new DatagramPacket(recv, recv.length);
                dgs.receive(dtg);
                msgrecived = new String(recv, 0, dtg.getLength());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (msgrecived != null){
                EchoListener echoListener = new EchoListener(dtg, msgrecived, dgs);
                Thread thread = new Thread(echoListener);
                thread.start();
            }

        }

    }
}
