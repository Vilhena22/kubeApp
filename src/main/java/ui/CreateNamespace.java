package ui;

import api.KubernetesClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import model.IconType;

import javax.swing.*;
import javax.xml.stream.events.Namespace;
import java.awt.event.*;

public class CreateNamespace extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField newNamespace;
    private final KubernetesClient client;

    public CreateNamespace(KubernetesClient client) {
        setTitle("Create Namespace");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.client = client;

        StyleFunctions.hoverButtonEffect(buttonOK);
        StyleFunctions.hoverButtonEffect(buttonCancel);
        StyleFunctions.setTextFieldStyle(newNamespace, true);

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
    }

    private void onOK() {
        // add your code here
        String newNS;
        newNS = newNamespace.getText();

        try {
            if (newNS.isBlank()){
                new InfoDialog("Namespace name cannot be empty!", IconType.WARNING);
            }else {
                try {
                    V1NamespaceList nsl = client.getNamespaceService().getAllNamespaces();
                    for (V1Namespace NS : nsl.getItems()){
                        if (NS.getMetadata().getName().equals(newNS)){
                            new InfoDialog("Namespace name cannot be repeated!", IconType.WARNING);
                            return;
                        }
                    }
                    client.getNamespaceService().createNamespace(newNS);
                    new InfoDialog(
                            "Namespace created successfully!",
                            IconType.SUCCESS
                    );
                    dispose();
                } catch (ApiException e) {
                    String error = e.getResponseBody();
                    if (error.contains("metadata.name")) {
                        new InfoDialog("<html>Invalid namespace name.<br>" +
                                "Use lowercase letters, numbers and '-'.</html>", IconType.WARNING);
                    }else {
                        new InfoDialog("Something went wrong!", IconType.ERROR);
                    }
                }
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
