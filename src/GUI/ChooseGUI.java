package GUI;

import Handler.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Vector;

/**
 * @author by Pham Nguyen My Diem
 * @version 1.0
 * @date 5/17/2021 8:36 AM
 */
public class ChooseGUI extends JFrame {
    private JPanel contentPane;

    public ChooseGUI(String username, DefaultListModel<String> d, ObjectOutputStream oos) {
        setTitle("Your Online Choices");
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setBounds(100, 100, 443, 489);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(25, 42, 86));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        DefaultListModel<String> model = new DefaultListModel<String>();
        for (int i = 0; i < d.size(); i++) {
            if (!d.get(i).contains(",")) {
                model.addElement(d.get(i));
            }
        }
        JList<String> list = new JList<String>(model);
        list.setBackground(new Color(255, 251, 251));
        list.setFont(new Font("Arial", Font.BOLD, 15));
        list.setCellRenderer(new CheckboxListCellRenderer());
        list.setBounds(10, 32, 417, 369);
        list.setPreferredSize(list.getPreferredSize());
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(10, 32, 417, 369);
        contentPane.add(scrollPane);

        JButton groupButton = new JButton("Create group chat");
        groupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> s = (ArrayList<String>) list.getSelectedValuesList();
                Vector<String> selected = new Vector<String>();
                for (String user : s) {
                    selected.add(user);
                }
                selected.add(0, username);
                if (selected.size() < 3) {
                    JOptionPane.showMessageDialog(null, "Must choose at least 2 people!");
                } else {
                    try {
                        oos.writeObject(new Message("addGroup", null, username, selected, null));
                        dispose();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });
        groupButton.setFont(new Font("Arial", Font.BOLD, 14));
        groupButton.setBackground(Color.decode("#e67e22"));
        groupButton.setForeground(Color.white);
        groupButton.setBounds(114, 412, 207, 33);
        contentPane.add(groupButton);
        setVisible(true);
    }
}

class CheckboxListCellRenderer extends JCheckBox implements ListCellRenderer<String> {
    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        setComponentOrientation(list.getComponentOrientation());
        setFont(list.getFont());
        setBackground(list.getBackground());
        setForeground(list.getForeground());
        setSelected(isSelected);
        setEnabled(list.isEnabled());

        setText(value == null ? "" : value.toString());

        return this;
    }
}