package ui;

import api.KubernetesClient;
import io.kubernetes.client.openapi.models.V1Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class CreateNode extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea textAreaMessage;
    private JLabel labelInfo;
    private JPanel cardPanel;
    private JComboBox comboBoxMasters;
    private JButton buttonBack;
    private JButton buttonCopy;
    private CardLayout cl;
    private KubernetesClient client;

    public CreateNode(KubernetesClient client) throws Exception {
        this.client = client;
        setTitle("How to Add a Node");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        pack();
        setLocationRelativeTo(null);
        cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, "comboCard");
        labelInfo.setText("Please choose master node");

        for (V1Node node : client.getClusterService().getAllMastersNodes().getItems()){
            if (node.getMetadata() != null) {
                comboBoxMasters.addItem(node.getMetadata().getName());
            }
        }
        comboBoxMasters.setSelectedIndex(-1);

        StyleFunctions.setTextAreaStyle(textAreaMessage,true);
        StyleFunctions.hoverButtonEffect(buttonOK);
        StyleFunctions.hoverButtonEffect(buttonCancel);
        StyleFunctions.hoverButtonEffect(buttonBack);
        StyleFunctions.setComboBoxStyle(comboBoxMasters);
        buttonOK.setVisible(false);
        buttonBack.setVisible(false);


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onOK();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        buttonCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCopy();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        buttonBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onBack();
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

        comboBoxMasters.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED && comboBoxMasters.getSelectedIndex() != -1) {
                    buttonOK.setVisible(true);

                    buttonOK.revalidate();
                    buttonOK.repaint();
                }
            }
        });

        setVisible(true);
    }

    private void onOK() throws Exception {
        V1Node node = client.getNodeService().getNode(Objects.requireNonNull(comboBoxMasters.getSelectedItem()).toString());
        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/copy.png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        buttonCopy.setIcon(new ImageIcon(logo));
        StyleFunctions.setIconsButton(buttonCopy);

        cl.show(cardPanel, "copyCard");
        labelInfo.setText("Please run this command on new node cli");

        textAreaMessage.setText("curl -sfL https://get.k3s.io | K3S_URL=https://"+ node.getStatus().getAddresses().getFirst().getAddress() + ":6443 K3S_TOKEN=<Token_Master> sh -");
        buttonBack.setVisible(true);
        buttonOK.setVisible(false);
        // add your code here
    }

    private void onCopy() {
        textAreaMessage.selectAll();
        textAreaMessage.copy();
        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/copySelected.png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        buttonCopy.setIcon(new ImageIcon(logo));
    }

    private void onBack() {
        buttonBack.setVisible(false);
        buttonOK.setVisible(true);
        cl.show(cardPanel, "comboCard");
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
