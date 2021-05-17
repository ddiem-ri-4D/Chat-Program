package Handler;

import java.io.Serializable;
import java.util.Vector;

/**
 * @author by Pham Nguyen My Diem
 * @version 1.0
 * @date 5/16/2021 10:33 PM
 */
public class Message implements Serializable {
    private String type;
    private String message;
    private String from;
    private Vector<String> users;
    private byte[] data;

    public Message(String type, String message, String from, Vector<String> users, byte[] data) {
        super();
        this.type = type;
        this.message = message;
        this.from = from;
        this.users = users;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Vector<String> getUsers() {
        return users;
    }

    public void setUsers(Vector<String> user) {
        this.users = user;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getData() {
        return data;
    }
}
