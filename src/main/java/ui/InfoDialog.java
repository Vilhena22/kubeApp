package ui;

import model.IconType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class InfoDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel messageLabel;

    public InfoDialog(String message, IconType type) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setUndecorated(true);
        setLocationRelativeTo(null);
        pack();

        buttonOK.addActionListener(e -> onOK());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onOK(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        messageLabel.setText(message);
        messageLabel.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 16));
        Image logo = switch (type) {
            case ERROR ->
                    new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/error.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            case WARNING ->
                    new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/warning.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            case SUCCESS ->
                    new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/check.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            case NOTFOUND ->
                    new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/notFound.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            default ->
                    new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/exclamation.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        };
        messageLabel.setIcon(new ImageIcon(logo));
        setVisible(true);
    }

    private void onOK() {
        // add your code here
        dispose();
    }
}
