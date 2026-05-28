package ui;

import api.KubernetesClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Namespace;
import model.IconType;

import javax.swing.*;
import java.awt.event.*;

public class CreateDeployment extends JDialog {
    private JPanel contentPane;
    private JButton buttonCreate;
    private JButton buttonCancel;
    private JTextField labelApp;
    private JTextField containerName;
    private JTextField containerImage;
    private JTextField namespace;
    private JTextField depName;
    private final KubernetesClient client;

    public CreateDeployment(KubernetesClient client) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCreate);
        this.client = client;

        StyleFunctions.hoverButtonEffect(buttonCreate);
        StyleFunctions.hoverButtonEffect(buttonCancel);
        StyleFunctions.setTextFieldStyle(namespace, true);
        StyleFunctions.setTextFieldStyle(labelApp, true);
        StyleFunctions.setTextFieldStyle(containerImage, true);
        StyleFunctions.setTextFieldStyle(containerName, true);
        StyleFunctions.setTextFieldStyle(depName, true);

        buttonCreate.addActionListener(new ActionListener() {
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

        String ns = namespace.getText();
        String deploymentName = depName.getText();
        String app = labelApp.getText();
        String container = containerName.getText();
        String img = containerImage.getText();

        if (ns.isBlank() || deploymentName.isBlank()
                || app.isBlank() || container.isBlank()
                || img.isBlank()) {

            new InfoDialog("Empty Fields", IconType.WARNING);
            return;
        }

        try {

            boolean namespaceExists = false;

            for (V1Namespace v1Namespace :
                    client.getNamespaceService().getAllNamespaces().getItems()) {

                if (ns.equals(v1Namespace.getMetadata().getName())) {
                    namespaceExists = true;
                    break;
                }
            }

            if (!namespaceExists) {

                JOptionPane.showMessageDialog(
                        this,
                        "Namespace does not exist!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            client.getDeploymentService().createDeployment(
                    ns,
                    deploymentName,
                    img,
                    app,
                    container
            );

            dispose();

            new InfoDialog(
                    "Deployment created successfully!",
                    IconType.SUCCESS
            );

        } catch (ApiException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getResponseBody(),
                    "Kubernetes Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
