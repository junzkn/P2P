package PTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.regex.Pattern;

/**
 *
 * @author Leo Luo
 *
 */
public class UDPAgent implements Runnable {


    static String ipPattern = "([0-9]{1,3}.){3}[0-9]{1,3}";

    static String portPattern = "[0-9]{1,5}";

    static Pattern sendPattern = Pattern.compile("send " + ipPattern + " " + portPattern + " .*");


    DatagramSocket ds; //UDP码头

    byte[] recbuf = new byte[1024];

    DatagramPacket rec = new DatagramPacket(recbuf, recbuf.length);


    int port;

    public UDPAgent(int port) {
        this.port = port;

    }

    public void init() throws Exception {
        if (port < 1024 || port > 655535) {
            ds = new DatagramSocket();
        } else {
            ds = new DatagramSocket(port);
        }
    }

    public void start() throws Exception {
        println("start");
        println("LocalPort:" + port);
        init();
        new Thread(this).start();// recive thread
        receive();
    }

    public void receive() {
        for (;;) {
            try {
                ds.receive(rec);
                String msg = new String(rec.getData(), rec.getOffset(), rec.getLength());
                String line = rec.getSocketAddress() + ":" + msg;
                println(line);
                onReceive(rec);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onReceive(DatagramPacket rec) {

    }

    public void doCommand(String cmd) throws Exception {
        // command:
        // 1. send xxx.xxx.xxx.xxx xxx *******************
        if (sendPattern.matcher(cmd).matches()) {
            doSend(cmd);
        }
    }

    public void doSend(String cmd) throws Exception {
        println("CMD: " + cmd);
        String[] s = cmd.split(" ", 4);
        int port = Integer.parseInt(s[2]);
        InetSocketAddress target = new InetSocketAddress(s[1], port);
        byte[] bs = s[3].getBytes();
        doSend(target, bs);
    }

    public void doSend(SocketAddress addr, byte[] data) throws Exception {
        DatagramPacket pack = new DatagramPacket(data, data.length, addr);
        ds.send(pack);
    }

    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line = reader.readLine();
            while (!"exit".equals(line)) {
                doCommand(line);
                line = reader.readLine();
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void println(String s) {
        System.out.println(s);
    }
}