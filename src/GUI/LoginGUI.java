package GUI;

import Handler.Client;
import Handler.Server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

/**
 * @author by Pham Nguyen My Diem
 * @version 1.0
 * @date 5/16/2021 5:28 PM
 */
public class LoginGUI extends JFrame {
    private JPanel contentPane;
    private JTextField userField;
    private JPasswordField passField;

    public LoginGUI() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setBounds(100, 100, 686, 394);
        setBounds(80, 80, 750, 440);
        setLocationRelativeTo(null);
        setTitle("Login");
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 251, 251));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel headerLabel = new JLabel();
        headerLabel.setText("LOGIN");
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setForeground(Color.decode("#192a56"));
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setBounds(245, 47, 250, 44);
        contentPane.add(headerLabel);

        JLabel userLabel = new JLabel("Username");
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userLabel.setForeground(Color.decode("#192a56"));
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setBounds(0, 117, 207, 36);
        contentPane.add(userLabel);

        JLabel passLabel = new JLabel();
        passLabel.setText("Password");
        passLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passLabel.setForeground(Color.decode("#192a56"));
        passLabel.setFont(new Font("Arial", Font.BOLD, 16));
        passLabel.setBounds(0, 187, 207, 36);
        contentPane.add(passLabel);

        userField = new JTextField();
        userField.setFont(new Font("Arial", Font.BOLD, 14));
        userField.setColumns(10);
        userField.setForeground(Color.decode("#192a56"));
        userField.setBackground(new Color(255, 251, 251));
        userField.setBounds(165, 117, 500, 44);
        contentPane.add(userField);

        passField = new JPasswordField();
        passField.setFont(new Font("Arial", Font.BOLD, 14));
        passField.setColumns(10);
        passField.setForeground(Color.decode("#192a56"));
        passField.setBackground(new Color(255, 251, 251));
        passField.setBounds(165, 187, 500, 44);
        contentPane.add(passField);

        JButton execButton = new JButton();
        execButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                passField.setText("");
                if (Server.users.size() > 0) {
                    boolean outFlag = false;
                    for (Vector<String> user : Server.users) {
                        if (user.get(0).equals(username) && user.get(1).equals(password)) {
                            outFlag = true;
                            boolean flag = true;
                            for (Client c : Server.active) {
                                if (c.getUsername().equals(username)) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (!flag) {
                                JOptionPane.showMessageDialog(null, "You need to login account!");
                                break;
                            } else {
                                userField.setText("");
                                EventQueue.invokeLater(new Runnable() {
                                    public void run() {
                                        try {
                                            ClientGUI d = new ClientGUI(username);
                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }
                        }
                    }
                    if (!outFlag)
                        JOptionPane.showMessageDialog(null, "The Username or Password is Incorrect!");
                }
            }
        });

        execButton.setText("Login");
        execButton.setForeground(Color.WHITE);
        execButton.setHorizontalAlignment(SwingConstants.CENTER);
        execButton.setFont(new Font("Arial", Font.BOLD, 16));
        execButton.setBackground(Color.decode("#192a56"));
        execButton.setBounds(295, 257, 150, 36);
        contentPane.add(execButton);

        JButton registerButton = new JButton();
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        try {

                            RegisterGUI frame = new RegisterGUI();
                        } catch (Exception f) {
                            f.printStackTrace();
                        }
                    }
                });
            }
        });
        registerButton.setText("Register");
        registerButton.setForeground(Color.WHITE);
        registerButton.setHorizontalAlignment(SwingConstants.CENTER);
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(Color.decode("#e67e22"));
        registerButton.setBounds(295, 327, 150, 36);
        contentPane.add(registerButton);

        setVisible(true);
    }
}
