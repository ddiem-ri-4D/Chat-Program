package Handler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * @author by Pham Nguyen My Diem
 * @version 1.0
 * @date 5/16/2021 2:33 PM
 */
public class Server {
    public static Vector<Vector<String>> users;
    private static final int serverPort = 5000;
    public static Vector<Client> active;

    public static boolean addUser(String username, String password) {
        for (Vector<String> temp : users) {
            if (temp.get(0).equals(username)) {
                return false;
            }
        }
        Vector<String> user = new Vector<>();
        user.add(username);
        user.add(password);
        users.add(user);
        return true;
    }

    public static void init() {
        try {
            users = new Vector<Vector<String>>();
            active = new Vector<Client>();
            ServerSocket ss = new ServerSocket(serverPort);
            do {
                System.out.println("Waiting for a client!");
                Socket s = ss.accept();
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                System.out.println("Welcome client!");
                Client c = new Client(s, "", ois, oos);
                c.start();
            } while (true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
