package ui;

import model.IconType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class DeleteDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel iconLabel;
    private JTextField confirmTextField;
    boolean confirm;

    public DeleteDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setUndecorated(true);
        pack();
        setLocationRelativeTo(null);
        confirm = false;
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        iconLabel.setText("Type delete to confirm operation");
        iconLabel.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 16));
        iconLabel.setIcon(new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/warning.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH)));
        StyleFunctions.setTextFieldStyle(confirmTextField,false);
        StyleFunctions.hoverButtonEffect(buttonOK);
        StyleFunctions.hoverButtonEffect(buttonCancel);

        setVisible(true);
    }

    private void onOK() {
        if (!confirmTextField.getText().equals("delete")) {
            new InfoDialog("Type exactly \"delete\" to confirm", IconType.WARNING);
        }else{
            confirm = true;
            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }

    public boolean isConfirmed() {
        return confirm;
    }
}
