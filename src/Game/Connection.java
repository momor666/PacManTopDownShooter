package Game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Rappresenta la connessione tra due giocatori.
 *
 * @author carloblasi
 */
public class Connection implements Runnable {

    // È un server con un metodo sendTo()
    private DatagramSocket socket;
    private boolean running;
    InetSocketAddress address;
    String opponentPosition = "";
    private boolean setting = true;
    private int opponentScreenWidth, opponentScreenHeight;
    //Info oppInfo = new Info(0, 0, 0, 0, 0);

    /**
     *
     * @param port
     * @param address
     * @throws SocketException
     */
    public void connect(int port, InetSocketAddress address) throws SocketException {
        try {
            this.socket = new DatagramSocket(port);
            this.address = address;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "INVALID ADDRESS or PORT/S", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void start() {

        this.running = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stop() {

        this.running = false;
        this.socket.close();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[128];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        /*while (setting) {
            try {
                this.socket.receive(packet);
                Game.opponentScreenWidth = Integer.parseInt(new String(buffer, 0, packet.getLength()));

                this.socket.receive(packet);
                Game.opponentScreenHeight = Integer.parseInt(new String(buffer, 0, packet.getLength()));


            } catch (IOException e) {
                break;
            }
        }*/

        while (running) {
            try {
                this.socket.receive(packet);

                this.opponentPosition = new String(buffer, 0, packet.getLength());

                //oppInfo = Info.deserialize(buffer);

                if (this.opponentPosition.equals("2")) {

                    MultiplayerGamePlayState.quitGame();
                    this.stop();
                }
                else if (this.opponentPosition.equals("1")) {
                    Game.opponentFired = true;
                }

            } catch (IOException e) {
                break;
            }
        }
    }

    /**
     *
     * @param msg
     * @throws IOException
     */
    public void send(String msg) throws IOException {

        try {
            byte[] buffer = msg.getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            packet.setSocketAddress(this.address);

            this.socket.send(packet);
        } catch (IllegalArgumentException e) {
            //JOptionPane.showMessageDialog(null, "INVALID ADDRESS or PORT/S", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     *
     * @param ba
     * @throws IOException
     */
    public void send(byte[] ba) throws IOException {

        byte[] buffer = ba;

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        packet.setSocketAddress(this.address);

        this.socket.send(packet);
    }

    /**
     *
     * @return
     */
    public String getOpponentPosition() {
        return this.opponentPosition;
    }

    /*public Info getInfo() {
        return this.oppInfo;
    }*/
}
