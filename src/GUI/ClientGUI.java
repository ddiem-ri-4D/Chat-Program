package GUI;

import Handler.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * @author by Pham Nguyen My Diem
 * @version 1.0
 * @date 5/16/2021 6:51 PM
 */
public class ClientGUI extends JFrame {
    private final int serverPort = 5000;
    private JPanel contentPane;
    private Socket s;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private JTextArea textArea;
    private JPanel onlinePanel;
    private JPanel outPanel;
    private JPanel chatPanel;
    private JButton sendButton;
    private JButton fileButton;
    private JList<String> list;
    private String previous = null;
    private ReadThread r;
    private String user;
    private Vector<ChatPanel> chats;

    public void init() throws UnknownHostException, IOException {
        this.s = new Socket("localhost", serverPort);
        this.oos = new ObjectOutputStream(s.getOutputStream());
        this.ois = new ObjectInputStream(s.getInputStream());
        chats = new Vector<ChatPanel>();
    }

    public ClientGUI(String username) throws UnknownHostException, IOException {
        init();
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        setSize(1000, 530);
        setLocationRelativeTo(null);
        user = new String(username);
        r = new ReadThread();
        Thread t = new Thread(r);
        t.start();
        t.setPriority(Thread.MAX_PRIORITY);
        list = new JList<String>();
        list.setBackground(new Color(255, 251, 251));
        oos.writeObject(new Message("update", "setName", username, null, null));
        oos.writeObject(new Message("update", "add", username, null, null));
        setTitle("Hello " + username);
        addWindowListener(new WindowAdapter() {
            @SuppressWarnings("deprecation")
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    t.stop();
                    oos.writeObject(new Message("update", "remove", username, null, null));
                    ois.close();
                    oos.close();
                    s.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setBackground(new Color(255, 118, 117));
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        setContentPane(contentPane);

        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 994, 45);
        //headerPanel.setBackground(new Color(25, 42, 86));
        headerPanel.setBackground(new Color(255, 118, 117));
        contentPane.add(headerPanel);
        headerPanel.setLayout(null);

        JLabel lblNewLabel = new JLabel("Online List ");
        lblNewLabel.setFont(new Font("Arial", Font.BOLD, 16));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setForeground(Color.WHITE);
        lblNewLabel.setBounds(10, 11, 235, 23);
        headerPanel.add(lblNewLabel);

        JButton groupButton = new JButton(" Create group chat");
        groupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(((DefaultListModel<String>) list.getModel()).size()>0){
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                ChooseGUI c = new ChooseGUI(username, (DefaultListModel<String>) list.getModel(), oos);
                            }
                            catch(Exception f){
                                f.printStackTrace();
                            }
                        }
                    });
                }
                else{
                    JOptionPane.showMessageDialog(null, "No Players Online!");
                }
            }
        });

        groupButton.setBackground(Color.orange);
        groupButton.setFont(new Font("Arial", Font.BOLD, 13));
        groupButton.setBounds(1147, 5, 137, 33);
        headerPanel.add(groupButton);

        onlinePanel = new JPanel();
        onlinePanel.setBounds(0, 45, 248, 656);
        contentPane.add(onlinePanel);
        onlinePanel.setLayout(new BorderLayout(0, 0));

        outPanel = new JPanel();
        outPanel.setBackground(new Color(255, 118, 117));
        outPanel.setBounds(248, 45, 1046, 656);
        contentPane.add(outPanel);
        outPanel.setLayout(null);

        chatPanel = new JPanel();
        chatPanel.setBounds(32, 0, 993, 550);
        chatPanel.setBackground(new Color(255, 118, 117));
        chatPanel.setLayout(null);
        //outPanel.add(chatPanel);

        textArea = new JTextArea();
        textArea.setBackground(new Color(255, 251, 251));
        textArea.setBounds(32, 572, 865, 73);
        textArea.setFont(new Font("Arial",Font.BOLD,17));

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    String msg = textArea.getText();
                    textArea.setText("");
                    if(msg.length() > 100){
                        JOptionPane.showMessageDialog(null, "Should not exceed more than 100 characters.");
                    }
                    else if(!msg.isEmpty()){

                    }

                }
            }
        });
        setVisible(true);
    }

    class ReadThread implements Runnable {
        @Override
        public void run() {

        }
    }

    class ChatPanel {
    }
}
