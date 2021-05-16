package Handler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * @author by Pham Nguyen My Diem
 * @version 1.0
 * @date 5/16/2021 2:34 PM
 */
public class Client extends Thread {
    private final Socket s;
    private String username;
    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;

    public Client(Socket s, String username, ObjectInputStream ois, ObjectOutputStream oos) throws UnknownHostException, IOException {
        this.s = s;
        this.oos = oos;
        this.ois = ois;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void run() {
        while (this.s.isConnected()) {
            try {
                Message received = (Message) ois.readObject();
                switch (received.getType()) {
                    case "Add Group": {
                        boolean outFlag = true;
                        Vector<String> users = received.getUsers();
                        for (int i = 0; i < users.size(); i++) {
                            boolean flag = false;
                            for (Client c : Server.active) {
                                if (c.username.equals(users.get(i))) {
                                    flag = true;
                                    break;
                                }
                            }
                            if (flag == false) {
                                oos.writeObject(new Message("Add Group Failed", null, null, null, null));
                                outFlag = false;
                                break;
                            }
                        }
                        if (outFlag == true)
                        {
                            for (String user:users)
                            {
                                for (Client c:Server.active)
                                {
                                    if (c.username.equals(user))
                                    {
                                        StringBuilder groupName = new StringBuilder();
                                        for (String u:users)
                                        {
                                            if (!c.username.equals(u))
                                            {
                                                groupName.append(u+", ");
                                            }
                                        }
                                        groupName.delete(groupName.length()-2, groupName.length());
                                        if (!c.username.equals(received.getFrom()))
                                            c.oos.writeObject
                                                    (new Message("Add Group", groupName.toString(), "no", null, null));
                                        else
                                            c.oos.writeObject
                                                    (new Message("Add Group", groupName.toString(), "yes", null, null));
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case "message" :
                    {
                        for (Client c : Server.active)
                        {
                            if (received.getUsers().size()<2)
                            {
                                if (c.getUsername().equals(received.getUsers().get(0)))
                                {
                                    Message m = new Message(received.getType(), received.getMessage(),received.getFrom(),
                                            received.getUsers(), null);
                                    c.oos.writeObject(m);
                                    break;
                                }
                            }
                            else
                            {

                                if (received.getUsers().contains(c.getUsername()))
                                {
                                    Vector<String> users = new Vector<String>();
                                    users.add(username);
                                    Message m = new Message(received.getType(), received.getMessage(),received.getFrom(),
                                            users, null);

                                    c.oos.writeObject(m);
                                }
                            }
                        }
                        break;
                    }
                    case "update" :
                    {
                        switch (received.getMessage())
                        {
                            case "setName":
                            {
                                this.setUsername(received.getFrom());
                                break;
                            }
                            case "add":
                            {
                                Server.active.add(this);
                                Message m = new Message(received.getType(),received.getMessage(), null, null, null);
                                Message n = new Message("initial","getList",null,null,null);
                                Vector<String> users = new Vector<String>();
                                for (Client c:Server.active)
                                {
                                    //System.out.println("Active: "+c.username);
                                    users.add(c.username);
                                }
                                for (Client c:Server.active)
                                {
                                    Vector<String> user = (Vector<String>) users.clone();
                                    user.remove(c.username);
                                    m.setUsers(user);
                                    if (!c.username.equals(this.username))
                                        c.oos.writeObject(m);
                                    else
                                    {
                                        n.setUsers(user);
                                        c.oos.writeObject(n);
                                    }
                                }
                                break;
                            }
                            case "remove":
                            {
                                Server.active.remove(this);
                                Message m = new Message(received.getType(), received.getMessage(),
                                        received.getFrom(),null, null);
                                Vector<String> users = new Vector<String>();

                                for (Client c:Server.active)
                                {
                                    c.oos.writeObject(m);
                                }
                                stop();
                                break;
                            }

                        }
                        break;
                    }
                    case "file":
                    {
                        Vector<String> users = received.getUsers();
                        for (String user : users)
                        {
                            for (Client c:Server.active)
                            {
                                if (c.username.equals(user)&&!c.username.equals(username))
                                {
                                    c.oos.writeObject(received);
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
