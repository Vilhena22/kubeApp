package ui;

import api.KubernetesClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Namespace;
import model.IconType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CreatePod extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JButton buttonBack;
    private JButton buttonNext;
    private JPanel podPanel;
    private JPanel containerPanel;
    private JTextField textFieldPodName;
    private JComboBox comboBoxNameSpace;
    private JTextField textFieldDescription;
    private JTextField textFieldContainerName;
    private JTextField textFieldContainerImage;
    private JComboBox comboBoxPullPolicies;
    private JComboBox comboBoxRestartPolicies;
    private JTextField textFieldAddNameSpace;
    private JPanel nameSpaceCard;
    private JButton buttonOk;
    private final KubernetesClient client;

    public CreatePod(KubernetesClient client) {
        this.client = client;
        setTitle("Create Pod");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonNext);
        pack();
        setLocationRelativeTo(null);

        try {
            styleButtons();
            styleTextField();
            styleComboBoxes();
            fillFilterCombobox(comboBoxNameSpace);
            fillRestartPolicyCombo();
            fillPullPolicyCombo();
            buttonBack.setVisible(false);
            buttonOk.setVisible(false);

        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

        buttonBack.addActionListener(e -> onBack());

        buttonCancel.addActionListener(e -> onCancel());

        buttonNext.addActionListener(e -> onNext());

        buttonOk.addActionListener(e -> {
            try {
                onOk();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
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
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        setVisible(true);
    }

    private void onOk() throws Exception {

        String podName = textFieldPodName.getText().toLowerCase();
        String description = textFieldDescription.getText();
        String containerName = textFieldContainerName.getText();
        String containerImage = textFieldContainerImage.getText();

        Object restartObj = comboBoxRestartPolicies.getSelectedItem();
        Object namespaceObj = comboBoxNameSpace.getSelectedItem();
        Object pullObj = comboBoxPullPolicies.getSelectedItem();

        if (podName.isBlank()
                || containerName.isBlank()
                || containerImage.isBlank()
                || restartObj == null
                || namespaceObj == null
                || pullObj == null) {
            new InfoDialog("Empty Fields", IconType.WARNING);
        }else {

            String restartPolicy = restartObj.toString();
            String nameSpace = namespaceObj.toString();
            String pullPolicy = pullObj.toString();

            try {

                client.getNamespaceService().getNamespace(nameSpace);

            } catch (ApiException e) {
                if (e.getCode() == 404) {

                    client.getNamespaceService().createNamespace(nameSpace);

                } else
                    throw e;
            }

            try {
                client.getPodService().createPod(podName,nameSpace,description,containerImage,containerName,restartPolicy,pullPolicy);

                new InfoDialog("Pod Added Successfully", IconType.SUCCESS);
                dispose();
            }catch (ApiException e){
                if (e.getCode() == 409) {
                    new InfoDialog("Error : Pod Already Exists!", IconType.ERROR);

                }else{
                    String error = e.getResponseBody();
                    if (error.contains("metadata.name")) {
                        new InfoDialog("<html>Invalid pod name.<br>" +
                                "Use lowercase letters, numbers and '-'.</html>", IconType.WARNING);
                    }
                }
            }

        }

    }

    private void onBack() {
        // add your code here
        podPanel.setVisible(true);
        containerPanel.setVisible(false);
        buttonNext.setVisible(true);
        buttonBack.setVisible(false);
        buttonOk.setVisible(false);
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void onNext() {
        // add your code here
        podPanel.setVisible(false);
        containerPanel.setVisible(true);
        buttonBack.setVisible(true);
        buttonNext.setVisible(false);
        buttonOk.setVisible(true);
    }

    private void fillPullPolicyCombo(){
        comboBoxPullPolicies.removeAllItems();
        comboBoxPullPolicies.addItem("Always");
        comboBoxPullPolicies.addItem("IfNotPresent");
        comboBoxPullPolicies.addItem("Never");
        comboBoxPullPolicies.setSelectedIndex(-1);
    }

    private void fillRestartPolicyCombo(){
        comboBoxRestartPolicies.removeAllItems();
        comboBoxRestartPolicies.addItem("Always");
        comboBoxRestartPolicies.addItem("OnFailure");
        comboBoxRestartPolicies.addItem("Never");
        comboBoxRestartPolicies.setSelectedIndex(-1);
    }

    private void fillFilterCombobox(JComboBox comboBox) throws ApiException {
        comboBox.removeAllItems();
        for (V1Namespace namespace : client.getNamespaceService().getAllNamespaces().getItems()){
            if (namespace.getMetadata() != null) {
                comboBox.addItem(namespace.getMetadata().getName());
            }
        }
        comboBox.setSelectedIndex(-1);

        CardLayout cl = (CardLayout) nameSpaceCard.getLayout();

        comboBox.addItem("+ Add new namespace...");

        comboBox.addItemListener(e -> {

            if (e.getStateChange() != ItemEvent.SELECTED) return;

            Object selected = e.getItem();

            if ("+ Add new namespace...".equals(selected)) {

                SwingUtilities.invokeLater(() -> {

                    cl.show(nameSpaceCard, "CardText");

                    textFieldAddNameSpace.setText("");
                    textFieldAddNameSpace.requestFocusInWindow();

                    comboBox.setSelectedIndex(-1);
                });
            }
        });




        textFieldAddNameSpace.addActionListener(e -> {


            String value = textFieldAddNameSpace.getText();

            if (value != null && !value.isBlank()) {

                comboBox.insertItemAt(value, comboBox.getItemCount() - 1);
                comboBox.setSelectedItem(value);
            }

            cl.show(nameSpaceCard, "CardCombo");

        });


        textFieldAddNameSpace.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {

                String value = textFieldAddNameSpace.getText();

                if (value != null && !value.isBlank()) {

                    comboBox.insertItemAt(value, comboBox.getItemCount() - 1);
                    comboBox.setSelectedItem(value);
                }

                SwingUtilities.invokeLater(() ->
                        cl.show(nameSpaceCard, "CardCombo")
                );
            }
        });

    }

    private void styleComboBoxes(){
        JComboBox[] comboBoxes = {
                comboBoxNameSpace,comboBoxPullPolicies,comboBoxRestartPolicies,
        };
        for (JComboBox combo : comboBoxes){
            StyleFunctions.setComboBoxStyle(combo);
        }
    }

    private void styleTextField() {
        JTextField[] textFields = {
                textFieldPodName, textFieldContainerImage,textFieldAddNameSpace,textFieldDescription,textFieldContainerName,
        };
        for (JTextField textField : textFields){
            StyleFunctions.setTextFieldStyle(textField,true);
        }
    }

    private void styleButtons(){
        AbstractButton[] buttons = {
                buttonOk, buttonNext,buttonBack,buttonCancel
        };

        for (AbstractButton btn : buttons) {

            StyleFunctions.hoverButtonEffect(btn);
        }
    }
}
