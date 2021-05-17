package GUI;

import Handler.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.StringTokenizer;
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
                if (((DefaultListModel<String>) list.getModel()).size() > 0) {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ChooseGUI c = new ChooseGUI(username, (DefaultListModel<String>) list.getModel(), oos);
                            } catch (Exception f) {
                                f.printStackTrace();
                            }
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(null, "No Players Online!");
                }
            }
        });

        groupButton.setBackground(Color.decode("#192a56"));
        groupButton.setForeground(Color.WHITE);
        groupButton.setFont(new Font("Arial", Font.BOLD, 14));
        groupButton.setBounds(700, 5, 200, 35);
        headerPanel.add(groupButton);

        onlinePanel = new JPanel();
        onlinePanel.setBounds(0, 45, 248, 656);
        contentPane.add(onlinePanel);
        onlinePanel.setLayout(new BorderLayout(0, 0));

        outPanel = new JPanel();
        //outPanel.setBackground(new Color(25,42,86));
        outPanel.setBackground(new Color(255, 118, 117));
        outPanel.setBounds(268, 45, 746, 656);
        contentPane.add(outPanel);
        outPanel.setLayout(null);

        chatPanel = new JPanel();
        chatPanel.setBounds(32, 0, 993, 550);
        chatPanel.setBackground(new Color(255, 118, 117));
        chatPanel.setLayout(null);
        //outPanel.add(chatPanel);

        textArea = new JTextArea();
        textArea.setBackground(new Color(25, 42, 86));
        textArea.setBounds(32, 572, 865, 73);
        textArea.setFont(new Font("Arial", Font.BOLD, 17));

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String msg = textArea.getText();
                    textArea.setText("");
                    if (msg.length() > 100) {
                        JOptionPane.showMessageDialog(null, "Should not exceed more than 100 characters.");
                    } else if (!msg.isEmpty()) {
                        try {
                            Vector<String> s = new Vector<String>();
                            JLabel jLabel = createMyMessage(msg);
                            chatPanel.setVisible(false);
                            chatPanel.add(jLabel);
                            chatPanel.setVisible(true);
                            if (!previous.contains(",")) {
                                s.add(previous);
                                oos.writeObject(new Message("message", msg, username, s, null));
                            } else {
                                StringTokenizer st = new StringTokenizer(previous, ", ");
                                while (st.hasMoreTokens()) {
                                    s.add(st.nextToken());
                                }
                                oos.writeObject(new Message("message", msg, previous + ", " + username,
                                        s, null));
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = textArea.getText();
                textArea.setText("");
                if (msg.length() > 100) {
                    JOptionPane.showMessageDialog(null, "Should not exceed more than 100 characters.");
                } else if (!msg.isEmpty()) {
                    try {
                        Vector<String> s = new Vector<String>();
                        JLabel jLabel = createMyMessage(msg);
                        chatPanel.setVisible(false);
                        chatPanel.add(jLabel);
                        chatPanel.setVisible(true);
                        if (!previous.contains(",")) {
                            s.add(previous);
                            oos.writeObject(new Message("message", msg, username, s, null));
                        } else {
                            StringTokenizer st = new StringTokenizer(previous, ", ");
                            while (st.hasMoreTokens()) {
                                s.add(st.nextToken());
                            }
                            oos.writeObject(new Message("message", msg, previous + ", " + username, s, null));
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        sendButton.setBackground(Color.decode("#192a56"));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBounds(607, 405, 118, 40);

        fileButton = new JButton("Send File");
        fileButton.setBackground(Color.decode("#192a56"));
        fileButton.setForeground(Color.WHITE);
        fileButton.setFont(new Font("Arial", Font.BOLD, 17));
        fileButton.setBounds(607, 370, 118, 30);

        fileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setDialogTitle("Select file to send");
                if (fileChooser.showOpenDialog(fileButton) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (file.isFile()) {
                        try {
                            byte[] data = Files.readAllBytes(file.toPath());
                            Vector<String> users = new Vector<String>();
                            if (!previous.contains(",")) {
                                users.add(previous);
                                oos.writeObject(new Message("file", file.getName(), username, users, data));
                            } else {
                                StringTokenizer st = new StringTokenizer(previous, ", ");
                                while (st.hasMoreTokens()) {
                                    users.add(st.nextToken());
                                }
                                users.add(username);
                                oos.writeObject(new Message("file", file.getName(), username, users, data));
                            }
                            JLabel j = createMyMessage(file.getName());
                            j.setFont(new Font("Arial", Font.ITALIC, 17));
                            j.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    JFileChooser dChooser = new JFileChooser();
                                    dChooser.setDialogTitle("Select path");
                                    dChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                    dChooser.setAcceptAllFileFilterUsed(false);
                                    if (dChooser.showOpenDialog(chatPanel) == JFileChooser.APPROVE_OPTION) {
                                        try {
                                            File f = new File(dChooser.getSelectedFile().toString() + "\\" + file.getName());
                                            f.createNewFile();
                                            FileOutputStream fos = new FileOutputStream(f);
                                            fos.write(data);
                                            fos.close();
                                            JOptionPane.showMessageDialog(chatPanel, "File has been downloaded successfully!");
                                        } catch (IOException e1) {
                                            JOptionPane.showMessageDialog(chatPanel, "Error downloading file!");
                                        }
                                    }
                                }
                            });
                            chatPanel.setVisible(false);
                            chatPanel.add(j);
                            chatPanel.setVisible(true);

                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        setVisible(true);
    }

    public JLabel createMyMessage(String msg) {
        int bound = 0;
        int index = 0;
        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).getUsername().equals(previous)) {
                bound = chats.get(i).getBound();
                index = i;
                break;
            }
        }
        JLabel piece = new JLabel(msg);
        piece.setOpaque(true);
        piece.setFont(new Font("Arial", Font.BOLD, 16));
        piece.setSize(piece.getPreferredSize());
        piece.setBackground(new Color(255, 118, 117));
        piece.setForeground(Color.WHITE);
        piece.setBounds(chatPanel.getWidth() - 35 - piece.getWidth(), bound, piece.getWidth() + 15, piece.getHeight() + 5);
        piece.setHorizontalAlignment(JLabel.CENTER);
        chats.get(index).setBound(chats.get(index).getBound() + piece.getHeight() + 10);
        if (chats.get(index).getBound() >= chatPanel.getPreferredSize().getHeight()) {
            chatPanel.setPreferredSize(new Dimension(993, chats.get(index).getBound() + 40));
        }
        return piece;
    }

    public JLabel createYourMessage(String msg, String from) {
        int bound = 0;
        int index = 0;
        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).getUsername().equals(from)) {
                bound = chats.get(i).getBound();
                index = i;
                break;
            }
        }
        JLabel piece = new JLabel(msg);
        piece.setOpaque(true);
        piece.setFont(new Font("Arial", Font.BOLD, 16));
        piece.setSize(piece.getPreferredSize());
        piece.setBounds(13, bound, piece.getWidth() + 15, piece.getHeight() + 5);
        piece.setHorizontalAlignment(JLabel.CENTER);
        piece.setBackground(new Color(255, 251, 251));
        piece.setForeground(Color.BLACK);
        chats.get(index).setBound(chats.get(index).getBound() + piece.getHeight() + 10);
        if (chats.get(index).getBound() >= chatPanel.getPreferredSize().getHeight()) {
            chats.get(index).getChat().setPreferredSize(new Dimension(993, chats.get(index).getBound() + 40));
        }
        return piece;
    }

    public void replacePanel(String username, JPanel jPanel) {
        for (ChatPanel c : chats) {
            if (c.getUsername().equals(username)) {
                c.setChat(jPanel);
                break;
            }
        }
    }

    public JPanel getPanel(String username) {
        for (ChatPanel c : chats) {
            if (c.getUsername().equals(username)) {
                return c.getChat();
            }
        }
        return null;
    }

    public JPanel create() {
        JPanel chatPanel = new JPanel();
        chatPanel.setBounds(32, 0, 793, 350);
        chatPanel.setPreferredSize(new Dimension(793, 350));
        chatPanel.setBackground(new Color(255, 251, 251));
        chatPanel.setLayout(null);
        return chatPanel;
    }

    class ReadThread implements Runnable {
        @Override
        public void run() {
            while (true) {


            }

        }
    }
}

class ChatPanel {
    private String username;
    private int bound;
    private JPanel chat;

    public String getUsername() {
        return username;
    }

    public int getBound() {
        return bound;
    }

    public void setBound(int bound) {
        this.bound = bound;
    }

    public JPanel getChat() {
        return chat;
    }

    public void setChat(JPanel chat) {
        this.chat = chat;
    }

    public ChatPanel(String username, JPanel chat, int bound) {
        super();
        this.username = username;
        this.bound = bound;
        this.chat = chat;
    }
}

class OnlineRenderer extends JPanel implements ListCellRenderer<String> {
    private JLabel lbName = new JLabel();

    public OnlineRenderer() {
        setLayout(new BorderLayout(5, 5));
        JPanel panelText = new JPanel(new CardLayout());
        panelText.add(lbName);
        add(panelText, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        lbName.setText(value);
        lbName.setFont(new Font("Arial", Font.BOLD, 22));
        lbName.setOpaque(true);
        lbName.setForeground(Color.white);
        lbName.setHorizontalAlignment(SwingConstants.CENTER);

        if (isSelected) {
            lbName.setBackground(new Color(255, 118, 117));
            setBackground(list.getSelectionBackground());
        } else {
            lbName.setBackground(list.getBackground());
            setBackground(list.getBackground());
        }
        return this;
    }
}
