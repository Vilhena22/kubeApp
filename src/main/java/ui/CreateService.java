package ui;

import api.KubernetesClient;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import model.IconType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateService extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel cardPanel;
    private JComboBox comboBoxServiceType;
    private JTable tableDeployments;
    private JButton nextButton;
    private JButton backButton;
    private JTextField textFieldPortCluster;
    private JTextField textFieldTargetCluster;
    private JTextField textFieldNodePort;
    private JTextField textFieldPortNode;
    private JTextField textFieldTargetNode;
    private JTextField textFieldServiceName;
    private JTextField textFieldExternal;
    private final KubernetesClient client;
    private final CardLayout cl;
    Map<String, String> selector;
    private String nameSpace;


    public CreateService(KubernetesClient client) {
        this.client = client;
        setTitle("Create Service");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        pack();
        setLocationRelativeTo(null);
        cl = (CardLayout) cardPanel.getLayout();

        String[] columNames = {"Name","Namespace", "Label"};
        DefaultTableModel model = new DefaultTableModel(columNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        try {
            for (V1Deployment dep : client.getDeploymentService().getAllDeployments().getItems()) {
                Object[] row = {
                        dep.getMetadata() != null ? dep.getMetadata().getName() : null,
                        dep.getMetadata() != null ? dep.getMetadata().getNamespace(): null,
                        dep.getMetadata() != null ? dep.getMetadata().getLabels() != null ? dep.getMetadata().getLabels().values() : null : null
                };
                model.addRow(row);
            }
            tableDeployments.setModel(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        comboBoxServiceType.addItem("ClusterIP");
        comboBoxServiceType.addItem("LoadBalancer");
        comboBoxServiceType.addItem("NodePort");
        comboBoxServiceType.addItem("ExternalName");
        comboBoxServiceType.setSelectedIndex(-1);
        StyleFunctions.setComboBoxStyle(comboBoxServiceType);
        StyleFunctions.hoverButtonEffect(nextButton);
        StyleFunctions.hoverButtonEffect(backButton);
        StyleFunctions.hoverButtonEffect(buttonOK);
        StyleFunctions.hoverButtonEffect(buttonCancel);
        StyleFunctions.formatTables(tableDeployments);
        tableDeployments.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tableDeployments.setRowHeight(70);

        JTextField[] text = {textFieldServiceName,textFieldTargetCluster,textFieldPortCluster,textFieldPortNode,textFieldTargetNode,textFieldExternal,textFieldNodePort};
        for (JTextField textField : text){
            StyleFunctions.setTextFieldStyle(textField,true);
        }



        buttonOK.addActionListener(e -> onOK());

        nextButton.addActionListener(e -> onNext());

        backButton.addActionListener(e -> onBack());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        buttonOK.setVisible(false);
        backButton.setVisible(false);
        setVisible(true);
    }

    private void onBack() {
        cl.show(cardPanel, "typeCard");
        backButton.setVisible(false);
        nextButton.setVisible(true);
        buttonOK.setVisible(false);

    }

    private void onNext() {
        if (comboBoxServiceType.getSelectedIndex() != -1 && tableDeployments.getSelectedRow() != -1 && !textFieldServiceName.getText().isEmpty()){
            backButton.setVisible(true);
            nextButton.setVisible(false);
            buttonOK.setVisible(true);
            switch (comboBoxServiceType.getSelectedIndex()) {
                case 0, 2:
                    cl.show(cardPanel, "clusterCard");
                    break;
                case 1:
                    cl.show(cardPanel, "nodeCard");
                    break;
                case 3:
                    cl.show(cardPanel, "externalCard");
                    break;
            }
            selector = new HashMap<>();
            try {
                nameSpace = tableDeployments.getModel().getValueAt(tableDeployments.getSelectedRow(), 1).toString();
                String deploymentName = tableDeployments.getModel().getValueAt(tableDeployments.getSelectedRow(), 0).toString();

                V1Deployment deployment= client.getDeploymentService().getDeploymentsByName(nameSpace, deploymentName);
                Map<String, String> labels =
                        (deployment.getSpec() != null ? deployment.getSpec()
                                                        .getTemplate()
                                                        .getMetadata() : null) != null ? deployment.getSpec()
                                                         .getTemplate()
                                                         .getMetadata()
                                                         .getLabels() : null;

                if (labels != null) {
                    selector.putAll(labels);
                }

            } catch (ApiException e) {
                new InfoDialog("Something went wrong!", IconType.WARNING);
            }
        }else{
            new InfoDialog("Fill all fields and select only one deployment", IconType.WARNING);
        }

    }

    private void onOK()  {
        V1ServicePort servicePort = null;
        V1ServiceSpec spec=null;
        switch (comboBoxServiceType.getSelectedIndex()) {
            case 0, 2 -> servicePort = new V1ServicePort()
                    .port(Integer.parseInt(textFieldPortCluster.getText()))
                    .targetPort(new IntOrString(Integer.parseInt(textFieldTargetCluster.getText())))
                    .protocol("TCP");
            case 1 ->  servicePort =new V1ServicePort()
                    .port(Integer.parseInt(textFieldPortNode.getText()))
                    .targetPort(new IntOrString(Integer.parseInt(textFieldTargetNode.getText())))
                    .nodePort(Integer.parseInt(textFieldNodePort.getText()))
                    .protocol("TCP");
            case 3 -> spec = new V1ServiceSpec()
                    .type(Objects.requireNonNull(comboBoxServiceType.getSelectedItem()).toString())
                    .externalName(textFieldExternal.getText());
            default ->  servicePort = new V1ServicePort()
                    .port(80)
                    .targetPort(new IntOrString(8080))
                    .protocol("TCP");
        }
        V1Service newService;
        if (spec == null) {
            newService = new V1Service().metadata(new V1ObjectMeta().name(textFieldServiceName.getText()))
                    .spec(new V1ServiceSpec()
                            .type(Objects.requireNonNull(comboBoxServiceType.getSelectedItem()).toString())
                            .selector(selector)
                            .ports(Collections.singletonList(servicePort)));
        }else{
            newService = new V1Service().metadata(new V1ObjectMeta().name(textFieldServiceName.getText()))
                    .spec(spec);
        }

        V1Service service = null;
        try {
            service = client.getServiceManager().createService(nameSpace,newService);
        } catch (ApiException e) {
            String error = e.getResponseBody();
            if (error.contains("metadata.name")) {
                new InfoDialog("<html>Invalid service name.<br>" +
                        "Use lowercase letters, numbers and '-'.</html>", IconType.WARNING);
            }else {
                new InfoDialog("Something went wrong!", IconType.ERROR);

            }

        }
        if (service != null){
            new InfoDialog("Service Created!", IconType.SUCCESS);
            dispose();
        }

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}

