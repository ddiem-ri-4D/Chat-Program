package GUI;

import Handler.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
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
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        //setSize(1000, 530);
        setSize(1300, 730);
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
        contentPane.setBackground(Color.decode("#74b9ff"));
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        setContentPane(contentPane);

        JPanel headerPanel = new JPanel();
        //headerPanel.setBounds(0, 0, 994, 45);
        headerPanel.setBounds(0, 0, 1294, 45);
        headerPanel.setBackground(Color.decode("#74b9ff"));
        //headerPanel.setBackground(new Color(255, 118, 117));
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

        groupButton.setBackground(new Color(25, 42, 86));
        groupButton.setForeground(Color.WHITE);
        groupButton.setFont(new Font("Arial", Font.BOLD, 14));
        //groupButton.setBounds(700, 5, 200, 35);
        groupButton.setBounds(1047, 5, 237, 33);
        headerPanel.add(groupButton);

        onlinePanel = new JPanel();
        onlinePanel.setBounds(0, 45, 248, 646);
        contentPane.add(onlinePanel);
        onlinePanel.setLayout(new BorderLayout(0, 0));

        outPanel = new JPanel();
        outPanel.setBackground(Color.decode("#74b9ff"));
        //outPanel.setBackground(new Color(255, 118, 117));
        //outPanel.setBounds(268, 45, 746, 656);
        outPanel.setBounds(248, 45, 1046, 656);
        contentPane.add(outPanel);
        outPanel.setLayout(null);

        chatPanel = new JPanel();
        chatPanel.setBounds(32, 0, 993, 550);
        //chatPanel.setBackground(new Color(255, 118, 117));
        chatPanel.setBackground(Color.decode("#74b9ff"));
        chatPanel.setLayout(null);
        //outPanel.add(chatPanel);

        textArea = new JTextArea();
        textArea.setForeground(new Color(25, 42, 86));
        textArea.setBackground(new Color(255, 251, 251));
        textArea.setBounds(32, 572, 865, 63);
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

        sendButton.setBackground(new Color(25, 42, 86));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        //sendButton.setBounds(607, 405, 88, 30);
        sendButton.setBounds(907, 605, 118, 30);

        fileButton = new JButton("Send File");
        fileButton.setBackground(new Color(25, 42, 86));
        fileButton.setForeground(Color.WHITE);
        fileButton.setFont(new Font("Arial", Font.BOLD, 14));
        //fileButton.setBounds(607, 370, 88, 30);
        fileButton.setBounds(907, 570, 118, 30);

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
        piece.setFont(new Font("Arial", Font.BOLD, 14));
        piece.setSize(piece.getPreferredSize());
        piece.setBackground(new Color(25, 42, 86));
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
        piece.setBackground(new Color(220, 220, 220));
        piece.setForeground(new Color(25, 42, 86));
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
        // chatPanel.setBounds(32, 0, 793, 350);
        chatPanel.setBounds(32, 0, 993, 550);
        chatPanel.setPreferredSize(new Dimension(793, 350));
        chatPanel.setBackground(new Color(255, 251, 251));
        chatPanel.setLayout(null);
        return chatPanel;
    }

    class ReadThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Message msg = (Message) ois.readObject();
                    switch (msg.getType()) {
                        case "addGroupFailed": {
                            JOptionPane.showMessageDialog(chatPanel, "Someone in the group has exited!",
                                    "Add Group Failed!", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                        case "addGroup": {
                            DefaultListModel<String> d = (DefaultListModel<String>) list.getModel();
                            String groupName = msg.getMessage();
                            boolean isValid = true;
                            for (int i = 0; i < d.size(); i++) {
                                if (d.get(i).contains(", ")) {
                                    boolean flag = false;
                                    StringTokenizer s = new StringTokenizer(groupName, ", ");
                                    while (s.hasMoreTokens()) {
                                        String k = s.nextToken();
                                        if (!d.get(i).contains(k)) {
                                            flag = true;
                                            break;
                                        }
                                    }
                                    if (flag == false) {
                                        isValid = false;
                                        break;
                                    }
                                }
                            }
                            if (isValid) {
                                list.setVisible(false);
                                d.addElement(groupName);
                                chats.add(new ChatPanel(groupName, create(), 10));
                                if (msg.getFrom().equals("yes")) {
                                    JOptionPane.showMessageDialog(chatPanel, "Add Group Successful!");
                                }
                                list.setVisible(true);
                            } else {
                                if (msg.getFrom().equals("yes")) {
                                    JOptionPane.showMessageDialog(chatPanel, "The group has been created!",
                                            "Add Group Failed!", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            break;
                        }
                        case "message": {
                            if (!msg.getFrom().contains(", ")) {
                                JLabel piece = createYourMessage(msg.getMessage(), msg.getFrom());
                                if (msg.getFrom().equals(previous)) {

                                    chatPanel.setVisible(false);
                                    chatPanel.add(piece);
                                    chatPanel.setVisible(true);
                                } else {
                                    JPanel panel = getPanel(msg.getFrom());
                                    panel.add(piece);
                                    replacePanel(msg.getFrom(), panel);
                                }
                            } else {
                                StringTokenizer st = new StringTokenizer(msg.getFrom(), ", ");
                                ArrayList<String> tokens = new ArrayList<String>();
                                while (st.hasMoreTokens()) {
                                    tokens.add(st.nextToken());
                                }
                                tokens.remove(user);
                                String correct = new String();
                                for (ChatPanel c : chats) {
                                    ArrayList<String> subs = new ArrayList<String>();
                                    String name = c.getUsername();
                                    StringTokenizer st1 = new StringTokenizer(name, ", ");
                                    while (st1.hasMoreTokens()) {
                                        subs.add(st1.nextToken());
                                    }
                                    boolean flag = true;
                                    for (String k : tokens) {
                                        if (!subs.contains(k)) {
                                            flag = false;
                                            break;
                                        }
                                    }
                                    if (flag == true) {
                                        correct = c.getUsername();
                                        break;
                                    }
                                }
                                JLabel piece = createYourMessage(msg.getUsers().get(0) + ":  " +
                                        msg.getMessage(), correct);
                                if (correct.equals(previous)) {
                                    chatPanel.setVisible(false);
                                    chatPanel.add(piece);
                                    chatPanel.setVisible(true);
                                } else {
                                    JPanel panel = getPanel(correct);
                                    panel.add(piece);
                                    replacePanel(correct, panel);
                                }
                            }
                            break;
                        }
                        case "update": {
                            Vector<String> users = msg.getUsers();
                            DefaultListModel<String> d = (DefaultListModel<String>) list.getModel();
                            list.setVisible(false);
                            switch (msg.getMessage()) {
                                case "add": {
                                    for (String user : users) {
                                        if (!d.contains(user)) {
                                            d.addElement(user);
                                            chats.add(new ChatPanel(user, create(), 10));
                                        }
                                    }
                                    break;
                                }
                                case "remove": {
                                    for (int i = 0; ; ) {
                                        if (i == d.size())
                                            break;
                                        if (d.get(i).contains(msg.getFrom())) {
                                            d.remove(i);
                                        } else
                                            i++;
                                    }
                                    for (int i = 0; ; ) {
                                        if (i == chats.size())
                                            break;
                                        if (chats.get(i).getUsername().contains(msg.getFrom())) {
                                            chats.remove(i);
                                        } else
                                            i++;
                                    }
                                    break;
                                }
                            }
                            list.setModel(d);
                            list.setVisible(true);
                            break;
                        }
                        case "initial": {
                            Vector<String> users = msg.getUsers();
                            DefaultListModel<String> model = new DefaultListModel<String>();
                            for (String user : users) {
                                model.addElement(user);
                                chats.add(new ChatPanel(user, create(), 10));
                            }
                            list.setVisible(false);
                            list.setModel(model);
                            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                            list.setCellRenderer(new OnlineRenderer());
                            list.setBorder(new LineBorder(Color.decode("#192a56"), 2));
                            onlinePanel.add(list, BorderLayout.CENTER);
                            list.addListSelectionListener(new ListSelectionListener() {
                                public void valueChanged(ListSelectionEvent e) {
                                    if (!e.getValueIsAdjusting()) {
                                        chatPanel.setBackground(new Color(255, 250, 205));
                                        if (previous == null) {
                                            previous = list.getSelectedValue();
                                            if ((getPanel(previous) != null)) {
                                                outPanel.setVisible(false);
                                                chatPanel = getPanel(previous);
                                                JScrollPane chatScrollPane = new JScrollPane(chatPanel,
                                                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                                                chatScrollPane.setBounds(32, 0, 993, 559);
                                                outPanel.removeAll();
                                                outPanel.add(chatScrollPane);
                                                outPanel.add(fileButton);
                                                outPanel.add(textArea);
                                                outPanel.add(sendButton);
                                                outPanel.setVisible(true);
                                            }
                                        } else {
                                            replacePanel(previous, chatPanel);

                                            String prev = list.getSelectedValue();

                                            if (prev != null) {
                                                previous = prev;
                                                outPanel.setVisible(false);
                                                outPanel.removeAll();
                                                chatPanel = getPanel(previous);
                                                JScrollPane chatScrollPane = new JScrollPane(chatPanel,
                                                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.
                                                        HORIZONTAL_SCROLLBAR_NEVER);
                                                chatScrollPane.setBounds(32, 0, 993, 559);
                                                outPanel.add(chatScrollPane);
                                                outPanel.add(textArea);
                                                outPanel.add(fileButton);
                                                outPanel.add(sendButton);
                                                outPanel.setVisible(true);
                                            } else {
                                                DefaultListModel<String> d = (DefaultListModel<String>)
                                                        list.getModel();
                                                if (!d.contains(previous)) {
                                                    outPanel.setVisible(false);
                                                    outPanel.removeAll();
                                                    outPanel.setVisible(true);
                                                } else {
                                                    list.setSelectedValue(previous, true);
                                                }
                                                previous = prev;
                                            }
                                        }
                                    }
                                }
                            });
                            list.setVisible(true);
                            break;
                        }
                        case "file": {
                            if (msg.getUsers().size() < 2) {
                                String from = msg.getFrom();
                                JLabel piece = createYourMessage(msg.getMessage(), from);
                                piece.setFont(new Font("Arial", Font.ITALIC, 17));
                                piece.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        JFileChooser dChooser = new JFileChooser();
                                        dChooser.setDialogTitle("Select Path");
                                        dChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                        dChooser.setAcceptAllFileFilterUsed(false);
                                        if (dChooser.showOpenDialog(chatPanel) == JFileChooser.APPROVE_OPTION) {
                                            try {
                                                File f = new File(dChooser.getSelectedFile().toString() + "\\" + msg.getMessage());
                                                f.createNewFile();
                                                FileOutputStream fos = new FileOutputStream(f);
                                                fos.write(msg.getData());
                                                fos.close();
                                                JOptionPane.showMessageDialog(chatPanel, "File download successful!");
                                            } catch (IOException e1) {
                                                JOptionPane.showMessageDialog(chatPanel, "File download failed!");
                                            }
                                        }
                                    }
                                });
                                if (msg.getFrom().equals(previous)) {

                                    chatPanel.setVisible(false);
                                    chatPanel.add(piece);
                                    chatPanel.setVisible(true);
                                } else {
                                    JPanel panel = getPanel(msg.getFrom());
                                    panel.add(piece);
                                    replacePanel(msg.getFrom(), panel);
                                }
                            } else {
                                Vector<String> tokens = msg.getUsers();
                                tokens.remove(user);
                                String correct = new String();
                                for (ChatPanel c : chats) {
                                    ArrayList<String> subs = new ArrayList<String>();
                                    String name = c.getUsername();
                                    StringTokenizer st1 = new StringTokenizer(name, ", ");
                                    while (st1.hasMoreTokens()) {
                                        subs.add(st1.nextToken());
                                    }
                                    boolean flag = true;
                                    for (String k : tokens) {
                                        if (!subs.contains(k)) {
                                            flag = false;
                                            break;
                                        }
                                    }
                                    if (flag == true) {
                                        correct = c.getUsername();
                                        break;
                                    }
                                }
                                JLabel piece = createYourMessage(msg.getFrom() + ": " + msg.getMessage(), correct);
                                piece.setFont(new Font("Arial", Font.ITALIC, 17));
                                piece.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        JFileChooser dChooser = new JFileChooser();
                                        dChooser.setDialogTitle("Select Path");
                                        dChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                        dChooser.setAcceptAllFileFilterUsed(false);
                                        if (dChooser.showOpenDialog(chatPanel) == JFileChooser.APPROVE_OPTION) {
                                            try {
                                                File f = new File(dChooser.getSelectedFile().toString() + "\\" + msg.getMessage());
                                                f.createNewFile();
                                                FileOutputStream fos = new FileOutputStream(f);
                                                fos.write(msg.getData());
                                                fos.close();
                                                JOptionPane.showMessageDialog(chatPanel, "File download successful!");
                                            } catch (IOException e1) {
                                                JOptionPane.showMessageDialog(chatPanel, "File download failed!");
                                            }
                                        }
                                    }
                                });
                                if (correct.equals(previous)) {
                                    chatPanel.setVisible(false);
                                    chatPanel.add(piece);
                                    chatPanel.setVisible(true);
                                } else {
                                    JPanel panel = getPanel(correct);
                                    panel.add(piece);
                                    replacePanel(correct, panel);
                                }
                            }
                            break;
                        }
                    }
                } catch (EOFException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
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
        lbName.setForeground(new Color(25, 42, 86));
        lbName.setHorizontalAlignment(SwingConstants.CENTER);

        if (isSelected) {
            lbName.setBackground(new Color(25, 42, 86));
            lbName.setForeground(Color.white);
            setBackground(list.getSelectionBackground());
        } else {
            lbName.setBackground(list.getBackground());
            setBackground(list.getBackground());
        }
        return this;
    }
}
