package GUI;

import Handler.Server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

/**
 * @author by Pham Nguyen My Diem
 * @version 1.0
 * @date 5/16/2021 2:35 PM
 */


public class RegisterGUI extends JFrame implements FocusListener {
    private JPanel contentPane;
    private JTextField userField;
    private JPasswordField passField;
    private JPasswordField rePassField;

    public RegisterGUI(){
        setVisible(true);
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //setBounds(90, 90, 581, 369);
        setBounds(100, 100, 686, 394);
        setLocationRelativeTo(null);
        setTitle("New User Account Registration");
        contentPane = new JPanel();
        contentPane.setBackground(new Color(251, 251, 251));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel headerLabel = new JLabel();
        headerLabel.setText("REGISTER NEW ACCOUNT");
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 23));
        headerLabel.setForeground(Color.decode("#192a56"));
        //headerLabel.setBounds(159, 30, 254, 31);
        headerLabel.setBounds(179, 33, 324, 31);
        contentPane.add(headerLabel);

        JLabel userLabel = new JLabel("Username");
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(Color.decode("#192a56"));
        //userLabel.setBounds(0, 82, 164, 31);
        userLabel.setBounds(0, 92, 164, 31);
        contentPane.add(userLabel);

        userField = new JTextField();
        userField.setFont(new Font("Arial", Font.BOLD, 14));
        userField.setForeground(Color.decode("#192a56"));
        userField.setBorder(BorderFactory.createLineBorder(Color.decode("#192a56")));
        userField.setBackground(new Color(255, 251, 251));
        //userField.setBounds(130, 82, 330, 32);
        userField.setBounds(165, 89, 474, 40);
        userField.addFocusListener(this);
        contentPane.add(userField);
        userField.setColumns(10);

        passField =  new JPasswordField();
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setForeground(Color.decode("#192a56"));
        passField.setBorder(BorderFactory.createLineBorder(Color.decode("#192a56")));
        passField.setBackground(new Color(255, 251, 251));
        passField.setColumns(10);
        //passField.setBounds(130, 142, 330, 32);
        passField.setBounds(165, 159, 474, 40);
        passField.addFocusListener(this);
        contentPane.add(passField);

        rePassField = new JPasswordField();
        rePassField.setFont(new Font("Arial", Font.PLAIN, 16));
        rePassField.setBackground(new Color(255, 251, 251));
        rePassField.setForeground(Color.decode("#192a56"));
        rePassField.setBorder(BorderFactory.createLineBorder(Color.decode("#192a56")));
        rePassField.setColumns(10);
       //rePassField.setBounds(130, 202, 330, 32);
        rePassField.setBounds(165, 229, 474, 40);
        rePassField.addFocusListener(this);
        contentPane.add(rePassField);

        JButton execButton = new JButton();
        execButton.setForeground(Color.WHITE);
        execButton.setBackground(Color.decode("#192a56"));
        execButton.setText("Register");
        execButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                String rePass = new String(rePassField.getPassword());
                CharSequence[] special = {",",".",";",":","\'","\"","/","!","@","#","$","%","^","&","*",
                        "(",")","<",">","[","]","{","}"," "};
                boolean f = true;
                for (int i = 0; i<special.length; i++){
                    if(username.contains(special[i])){
                        f = false;
                        break;
                    }
                }
                if(f == false){
                    JOptionPane.showMessageDialog(null, "Username cannot contain special characters!");
                }
                else if(!password.equals(rePass)){
                    JOptionPane.showMessageDialog(null, "Password and confirm password does not match!");
                }
                else if(username.isEmpty() || password.isEmpty() || rePass.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Field cannot be left blank!");
                }
                else{
                    boolean flag = true;
                    for(Vector<String> user: Server.users){
                        if(user.get(0).equals(username)){
                            flag = false;
                            break;
                        }
                    }
                    if(!flag){
                        JOptionPane.showMessageDialog(null, "Username is a Duplicate of Another User Account!");
                    }
                    else{
                        Server.addUser(username, password);
                        dispose();
                        JOptionPane.showMessageDialog(null, "Your Account has been Successfully Registered!");
                    }
                }
            }
        });
        execButton.setFont(new Font("Arial", Font.BOLD, 14));
        execButton.setBounds(285, 301, 108, 31);
        contentPane.add(execButton);


        JLabel passLabel = new JLabel();
        passLabel.setText("Password");
        passLabel.setForeground(Color.decode("#192a56"));
        passLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        //passLabel.setBounds(0, 162, 164, 31);
        passLabel.setBounds(0, 162, 164, 31);
        contentPane.add(passLabel);

        JLabel rePassLabel = new JLabel();
        rePassLabel.setText("Confirm password");
        rePassLabel.setForeground(Color.decode("#192a56"));
        rePassLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rePassLabel.setFont(new Font("Arial", Font.BOLD, 14));
        //rePassLabel.setBounds(0, 232, 164, 31);
        rePassLabel.setBounds(0, 232, 164, 31);
        contentPane.add(rePassLabel);
    }

    @Override
    public void focusGained(FocusEvent e) {
        for (Component c:getContentPane().getComponents())
        {
            if (c instanceof JTextField )
            {
                ((JTextField) c).setCaretColor(Color.BLACK);
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
