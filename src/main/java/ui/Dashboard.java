package ui;

import Handlers.AppSetup;
import Handlers.ClusterDAO;
import ai.AiFactory;
import ai.Assistant;
import ai.Tools;
import api.KubernetesClient;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.*;
import model.History;
import model.IconType;
import model.Result;
import org.jetbrains.annotations.NotNull;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Dashboard  {
    private JPanel mainPanel;
    private JLabel logoLabel;
    private JPanel contentPanel;
    private JPanel metricsPanel;
    private JTextField searchBar;
    private JPanel nodesPanel;
    private JPanel navbarPanel;
    private JButton dashboardButton;
    private JButton nodesButton;
    private JButton podsButton;
    private JButton namespacesButton;
    private JButton deploymentsButton;
    private JButton servicesButton;
    private JButton searchIcon;
    private JPanel podsPanel;
    private JPanel deploymentsPanel;
    private JPanel namespacesPanel;
    private JPanel servicesPanel;
    private JLabel iconCard1;
    private JLabel valueCard1;
    private JLabel iconCard2;
    private JLabel valueCard2;
    private JLabel titleTable;
    private JPanel graphicPanel;
    private JLabel iconCard3;
    private JLabel iconCard4;
    private JList listPodCpuRank;
    private JList listPodRamRank;
    private JTable tableNodeStat;
    private JLabel iconCard5;
    private JLabel cpuLabelCard5;
    private JLabel ramLabelCard5;
    private JLabel ipLabelCard5;
    private JLabel kernelLabelCard5;
    private JLabel kubLabelCard5;
    private JLabel osLabelCard5;
    private JButton createDeploymentButton;
    private JButton deleteDeploymentButton;
    private JComboBox comboBoxDeployment;
    private JButton createNamespaceButton;
    private JButton deleteNamespaceButton;
    private JTable namespaceTable;
    private JTable tableDeployments;
    private JTable tableNodes;
    private JPanel searchPanel;
    private JTable tableSearch;
    private JTable tablePods;
    private JComboBox comboBoxFilterPodByNamespace;
    private JButton addPodButton;
    private JButton deletePodButton;
    private JLabel loadingNodes;
    private JPanel nodesLoading;
    private JPanel podsLoading;
    private JLabel loadingPods;
    private JPanel namespacesLoading;
    private JTable tableNamespaces;
    private JPanel deploymentLoading;
    private JLabel loadingDeployments;
    private JLabel loadingNamespaces;
    private JPanel servicesLoading;
    private JLabel loadingServices;
    private JTable tableServices;
    private JButton addNodeButton;
    private JButton deleteNodeButton;
    private JButton buttonChatBot;
    private JTextPane chatBox;
    private JTextArea userInputArea;
    private JPanel chatWindow;
    private JComboBox comboServiceNamespace;
    private JButton deleteServiceButton;
    private JButton createServiceButton;
    private KubernetesClient client;
    private Assistant assistant = null;
    private Tools tools = null;
    private Style userStyle;
    private Style aiStyle;

    public Dashboard() throws Exception {

        setStyle();

        //Create Client
        try {
            this.client = connectClient();
        }catch (Exception e){
            System.out.println("Error Connecting: " + e.getMessage());
        }

        buildGraphic();
        refreshDashboard();
        navbarButtonEffect(dashboardButton);

        deploymentsButton.addActionListener(this::btnDeleteDeployment);
        addNodeButton.addActionListener(this::btnAddNode);
        deleteNodeButton.addActionListener(this::btnDeleteNode);
        buttonChatBot.addActionListener(this::btnOpenChat);

        createServiceButton.addActionListener(this::btnCreateService);
        deleteServiceButton.addActionListener(this::btnDeleteService);


        dashboardButton.addActionListener(e -> {
            for (Component c : navbarPanel.getComponents()){
                if(c.getClass().equals(JButton.class)){
                    resetButton((JButton) c);
                }
            }
            navbarButtonEffect(dashboardButton);
            hideContentPanels();
            metricsPanel.setVisible(true);


        });
        nodesButton.addActionListener(e -> {
            for (Component c : navbarPanel.getComponents()){
                if(c.getClass().equals(JButton.class)){
                    resetButton((JButton) c);
                }
            }
            navbarButtonEffect(nodesButton);
            hideContentPanels();
            try {
                fillNodesTable();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            nodesPanel.setVisible(true);

        });

        podsButton.addActionListener(e -> {
            for (Component c : navbarPanel.getComponents()){
                if(c.getClass().equals(JButton.class)){
                    resetButton((JButton) c);
                }
            }
            navbarButtonEffect(podsButton);
            hideContentPanels();
            try {
                fillFilterComboboxByNamespaces(comboBoxFilterPodByNamespace);
                Object selectedItem = comboBoxFilterPodByNamespace.getSelectedItem();
                if(selectedItem == null){
                    fillPodsTable("");
                }else{
                    fillPodsTable(selectedItem.toString());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            podsPanel.setVisible(true);

        });

        deploymentsButton.addActionListener(e -> {
            for (Component c : navbarPanel.getComponents()){
                if(c.getClass().equals(JButton.class)){
                    resetButton((JButton) c);
                }
            }
            navbarButtonEffect(deploymentsButton);
            hideContentPanels();
            deploymentsPanel.setVisible(true);
            try {
                fillDeploymentTable();
            } catch (ApiException ex) {
                throw new RuntimeException(ex);
            }
        });

        servicesButton.addActionListener(e -> {
            for (Component c : navbarPanel.getComponents()){
                if(c.getClass().equals(JButton.class)){
                    resetButton((JButton) c);
                }
            }
            navbarButtonEffect(servicesButton);
            hideContentPanels();
            servicesPanel.setVisible(true);
            try{
                fillFilterComboboxByNamespaces(comboServiceNamespace);
                Object selectedItem = comboServiceNamespace.getSelectedItem();
                if(selectedItem == null){
                    fillServicesTable("");
                }else{
                    fillServicesTable(selectedItem.toString());
                }
            }catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        namespacesButton.addActionListener(e -> {
            for (Component c : navbarPanel.getComponents()){
                if(c.getClass().equals(JButton.class)){
                    resetButton((JButton) c);
                }
            }
            navbarButtonEffect(namespacesButton);
            hideContentPanels();
            namespacesPanel.setVisible(true);

                fillNamespaceTable();
        });

        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                String text = searchBar.getText();
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        searchFunction(text);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        searchIcon.addActionListener(e -> {
            String text = searchBar.getText();
            try {
                searchFunction(text);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        addPodButton.addActionListener(this::createPod);
        deletePodButton.addActionListener(actionEvent -> {
            try {
                deletePod();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


        comboBoxFilterPodByNamespace.addItemListener(e -> {
            try {
                Object selectedItem = comboBoxFilterPodByNamespace.getSelectedItem();
                if(selectedItem == null){
                    fillPodsTable("");
                }else{
                    fillPodsTable(selectedItem.toString());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        comboServiceNamespace.addItemListener(e -> {
            try {
                Object selectedItem = comboServiceNamespace.getSelectedItem();
                if(selectedItem == null){
                    fillServicesTable("");
                }else{
                    fillServicesTable(selectedItem.toString());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        /*userInputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()){
                    e.consume();
                    userInputArea.selectAll();
                    String question = userInputArea.getText();
                    userInputArea.setText("");
                    StyledDocument doc = chatBox.getStyledDocument();
                    try {
                        doc.insertString(
                                doc.getLength(),
                                "You: " + question + "\n\n",
                                userStyle
                        );
                    } catch (BadLocationException ex) {
                        throw new RuntimeException(ex);
                    }

                    SwingWorker<String, Void> worker =
                            new SwingWorker<>() {

                                @Override
                                protected String doInBackground() {
                                    return assistant.chat(question);
                                }

                                @Override
                                protected void done() {

                                    try {

                                        String response = get();

                                        StyledDocument doc =
                                                chatBox.getStyledDocument();

                                        doc.insertString(
                                                doc.getLength(),
                                                "KubeBot: " + response + "\n\n",
                                                aiStyle
                                        );

                                    } catch (Exception ex) {

                                        ex.printStackTrace();
                                    }
                                }
                            };

                    worker.execute();
                }
            }
        });*/
    }




    private void btnOpenChat(ActionEvent actionEvent) {
        /*if (assistant==null && tools == null){
            tools = new Tools(client);
            assistant = AiFactory.createAssistant(tools);
        }*/
        boolean isVisible = chatWindow.isVisible();
        chatWindow.setVisible(!isVisible);
    }


    ///################Services#################
    ///

    private void btnCreateService(ActionEvent actionEvent) {
        try {
            new CreateService(client);
            fillServicesTable("");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void btnDeleteService(ActionEvent actionEvent) {
        for (int row : tableServices.getSelectedRows()){
            try {
                client.getServiceManager().deleteService(tableServices.getValueAt(row,2).toString(),tableServices.getValueAt(row,0).toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        new InfoDialog("All Nodes Deleted!",IconType.SUCCESS);
        try {
            fillServicesTable("");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fillServicesTable(String nameSpace){
        CardLayout cl = (CardLayout) servicesLoading.getLayout();
        loadingServices.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/loading.gif"))));
        cl.show(servicesLoading, "loading");
        System.out.println(nameSpace);
        SwingWorker<DefaultTableModel, Void> worker = new SwingWorker<>() {

            @Override
            protected DefaultTableModel doInBackground()  {
                String[] columnNames = {"Name", "Type","Namespace"};
                DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                try {
                    V1ServiceList serviceList;
                    if (nameSpace.isEmpty()) {
                        serviceList = client.getServiceManager().getAllServices();
                    } else {
                        serviceList = client.getServiceManager().getAllServicesOnNamespace(nameSpace);
                    }
                    for (V1Service nm : serviceList.getItems()) {
                        Object[] row = {
                                nm.getMetadata() != null ? nm.getMetadata().getName() : null,
                                nm.getSpec() != null ? nm.getSpec().getType() : null,
                                nm.getMetadata().getNamespace(),
                        };
                        model.addRow(row);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return model;
            }

            @Override
            protected void done() {
                try {
                    DefaultTableModel model = get(); // retrieves result from doInBackground()
                    tableServices.setModel(model);
                    StyleFunctions.formatTables(tableServices);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle or display the error in the UI
                } finally {
                    // setLoadingState(false);
                    cl.show(servicesLoading, "table");
                }
            }
        };

        worker.execute();
    }


    ///################Nodes#################
    ///

    private void btnAddNode(ActionEvent actionEvent){
        try {
            new CreateNode(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void btnDeleteNode(ActionEvent actionEvent) {
        for (int row : tableNodes.getSelectedRows()){
            try {
                client.getNodeService().deleteNode(tableNodes.getValueAt(row,1).toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        new InfoDialog("All Nodes Deleted!",IconType.SUCCESS);
        try {
            fillNodesTable();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fillNodesTable() {
        CardLayout cl = (CardLayout) nodesLoading.getLayout();
        loadingNodes.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/loading.gif"))));
        cl.show(nodesLoading, "loading");

        SwingWorker<DefaultTableModel, Void> worker = new SwingWorker<>() {

            @Override
            protected DefaultTableModel doInBackground() throws Exception {

                String[] columNames = new String[]{"Name", "Status", "Ip", "Pods Running", "Max Pods"};
                DefaultTableModel model = new DefaultTableModel(columNames, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                for (V1Node node : client.getNodeService().getAllNodes().getItems()) {
                    String nodeName = Objects.requireNonNull(node.getMetadata()).getName();
                    String status = Objects.requireNonNull(Objects.requireNonNull(node.getStatus()).getConditions()).stream()
                            .filter(c -> "Ready".equals(c.getType()))
                            .findFirst()
                            .map(c -> "True".equals(c.getStatus()) ? "Up" : "Down")
                            .orElse("Unknown");

                    String ip = Objects.requireNonNull(node.getStatus().getAddresses()).getFirst().getAddress();

                    Map<String, Quantity> capacity = node.getStatus().getCapacity();

                    String capacityPods    = Objects.requireNonNull(capacity).get("pods").getNumber().toString();
                    long runningPods = client.getNodeService().getPodsRunOnPod(nodeName);

                    Object[] row = {
                            nodeName,
                            status,
                            ip,
                            runningPods,
                            capacityPods
                    };
                    model.addRow(row);
                }
                return model;
            }

            @Override
            protected void done() {
                try {
                    DefaultTableModel model = get(); // retrieves result from doInBackground()
                    tableNodes.setModel(model);
                    StyleFunctions.formatTables(tableNodes);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle or display the error in the UI
                } finally {
                    cl.show(nodesLoading, "table");
                }
            }
        };

        worker.execute();
    }

    ////################Pods#################
    ///

    private void createPod(ActionEvent actionEvent) {
        new CreatePod(client);
        try {
            fillPodsTable("");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deletePod() throws Exception {
        for ( int i :  tablePods.getSelectedRows()) {
            String podName = tablePods.getValueAt(i, 0).toString();
            String nameSpace = tablePods.getValueAt(i, 3).toString();
            client.getPodService().deletePod(podName,nameSpace);
        }
        new InfoDialog("All Pods Deleted!",IconType.SUCCESS);
        fillPodsTable("");
    }


    private void fillPodsTable(String namespace)  {
        CardLayout cl = (CardLayout) podsLoading.getLayout();
        loadingPods.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/loading.gif"))));
        cl.show(podsLoading, "loading");

        SwingWorker<DefaultTableModel, Void> worker = new SwingWorker<>() {

            @Override
            protected DefaultTableModel doInBackground() throws Exception {
                String[] columnNames = new String[]{"Name", "Status", "Node", "Namespace", "Pod IP", "Creation Date"};
                DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                V1PodList podList;
                if (namespace.isEmpty()) {
                    podList = client.getPodService().getAllPods();
                } else {
                    podList = client.getPodService().getAllPodsOnNamespace(namespace);
                }

                for (V1Pod pod : podList.getItems()) {
                    String podName = Objects.requireNonNull(pod.getMetadata()).getName();
                    String status = Objects.requireNonNull(pod.getStatus()).getPhase();
                    String nameSpace = pod.getMetadata().getNamespace();
                    String node = Objects.requireNonNull(pod.getSpec()).getNodeName();
                    String ip = pod.getStatus().getPodIP();
                    String date = Objects.requireNonNull(pod.getMetadata().getCreationTimestamp()).toString();
                    Object[] row = new Object[]{
                            podName,
                            status,
                            node,
                            nameSpace,
                            ip,
                            date
                    };
                    model.addRow(row);
                }

                return model;
            }

            @Override
            protected void done() {
                try {
                    DefaultTableModel model = get(); // retrieves result from doInBackground()
                    tablePods.setModel(model);
                    StyleFunctions.formatTables(tablePods);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle or display the error in the UI
                } finally {
                    // setLoadingState(false);
                    cl.show(podsLoading, "table");
                }
            }
        };

        worker.execute();

    }

    ////################Deployments#################
    ///

    private void btnDeleteDeployment(ActionEvent actionEvent) {

        String name ;
        String namespace ;

        for (int row : tableDeployments.getSelectedRows()) {
            try {
                name = tableDeployments.getValueAt(row,0).toString();
                namespace = tableDeployments.getValueAt(row, 1 ).toString();
                client.getDeploymentService().deleteDeployment(name, namespace);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void fillDeploymentTable() throws ApiException {

        CardLayout cl = (CardLayout) deploymentLoading.getLayout();
        loadingDeployments.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/loading.gif"))));
        cl.show(deploymentLoading, "loading");

        SwingWorker<DefaultTableModel, Void> worker = new SwingWorker<>() {

            @Override
            protected DefaultTableModel doInBackground() throws Exception {
                fillFilterComboboxByNamespaces(comboBoxDeployment);
                String[] columNames = {"Name", "Namespace", "Resource Version"};
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
                                dep.getMetadata() != null ? dep.getMetadata().getNamespace() : null,
                                dep.getMetadata() != null ? dep.getMetadata().getResourceVersion() : null
                        };
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return model;
            }

            @Override
            protected void done() {
                try {
                    DefaultTableModel model = get(); // retrieves result from doInBackground()
                    tableDeployments.setModel(model);
                    StyleFunctions.formatTables(tableDeployments);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle or display the error in the UI
                } finally {
                    // setLoadingState(false);
                    cl.show(deploymentLoading, "table");
                }
            }
        };

        worker.execute();


    }

    ////################Namespaces#################
    ///

    public void fillNamespaceTable(){

        CardLayout cl = (CardLayout) namespacesLoading.getLayout();
        loadingNamespaces.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/loading.gif"))));
        cl.show(namespacesLoading, "loading");


        SwingWorker<DefaultTableModel, Void> worker = new SwingWorker<>() {

            @Override
            protected DefaultTableModel doInBackground() {
                String[] columnNames = {"Name", "State","Creation Timestamp"};
                DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                try {
                    for (V1Namespace nm : client.getNamespaceService().getAllNamespaces().getItems()) {
                        Object[] row = {
                                nm.getMetadata() != null ? nm.getMetadata().getName() : null,
                                nm.getStatus() != null ? nm.getStatus().getPhase() : null,
                                nm.getMetadata().getCreationTimestamp(),
                        };
                        model.addRow(row);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return model;
            }

            @Override
            protected void done() {
                try {
                    DefaultTableModel model = get(); // retrieves result from doInBackground()
                    tableNamespaces.setModel(model);
                    StyleFunctions.formatTables(tableNamespaces);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle or display the error in the UI
                } finally {
                    // setLoadingState(false);
                    cl.show(namespacesLoading, "table");
                }
            }
        };

        worker.execute();

    }


    ////################Dashboard#################
    ///

    private void searchFunction(String searchText) throws Exception {
        String[] columNames = new String[]{"Name", "Type", "Location", "Ip"};
        DefaultTableModel model = new DefaultTableModel(columNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        try {
            V1NodeList nodeList = client.getNodeService().getAllNodes();
            if (nodeList != null) {
                nodeList.getItems().stream()
                        .filter(node -> node.getMetadata() != null
                                && node.getMetadata().getName() != null
                                && node.getMetadata().getName().contains(searchText))
                        .forEach(node -> {
                            assert node.getStatus() != null;
                            V1NodeAddress ipAddress = Objects.requireNonNull(node.getStatus().getAddresses()).stream()
                                    .filter(a -> "InternalIP".equals(a.getType()))
                                    .findFirst()
                                    .orElse(null);

                            Object[] row = {
                                    node.getMetadata().getName(),
                                    "Node",
                                    "NA",
                                    ipAddress != null ? ipAddress.getAddress() : "N/A"
                            };
                            model.addRow(row);
                        });
            }
        } catch (Exception ignored) {

        }

        try {
            V1NamespaceList namespaceList = client.getNamespaceService().getAllNamespaces();
            if (namespaceList != null) {
                namespaceList.getItems().stream()
                        .filter(namespace -> namespace.getMetadata() != null
                                && namespace.getMetadata().getName() != null
                                && namespace.getMetadata().getName().contains(searchText))
                        .forEach(namespace -> {
                            Object[] row = {
                                    namespace.getMetadata().getName(),
                                    "Namespace",
                                    "NA",
                                    "NA"
                            };
                            model.addRow(row);
                        });
            }
        } catch (Exception ignored) {

        }


        for (V1Namespace name :  client.getNamespaceService().getAllNamespaces().getItems()) {
            if (name.getMetadata() != null) {
                String nameSpaceSearch = name.getMetadata().getName();
                if (nameSpaceSearch != null) {
                    try {
                        V1PodList podList = client.getPodService().getAllPodsOnNamespace(nameSpaceSearch);
                        if (podList != null) {
                            podList.getItems().stream()
                                    .filter(pod -> pod.getMetadata() != null
                                            && pod.getMetadata().getName() != null
                                            && pod.getMetadata().getName().contains(searchText))
                                    .forEach(pod -> {
                                        Object[] row = {
                                                pod.getMetadata().getName(),
                                                "Pod",
                                                nameSpaceSearch,
                                                pod.getStatus() != null ? pod.getStatus().getPodIP() : "N/A"
                                        };
                                        model.addRow(row);
                                    });
                        }
                    } catch (Exception ignored) {

                    }
                    try {
                        V1DeploymentList deploymentList = client.getDeploymentService().getAllDeployments();
                        if (deploymentList != null) {
                            deploymentList.getItems().stream()
                                    .filter(dep -> dep.getMetadata() != null
                                            && dep.getMetadata().getName() != null
                                            && dep.getMetadata().getName().contains(searchText))
                                    .forEach(dep -> {
                                        Object[] row = {
                                                dep.getMetadata().getName(),
                                                "Deployment",
                                                nameSpaceSearch,
                                                "NA"
                                        };
                                        model.addRow(row);
                                    });
                        }
                    } catch (Exception ignored) {

                    }

                }
            }

        }

        try {
            V1ServiceList serviceList = client.getServiceManager().getAllServices();
            if (serviceList != null) {
                serviceList.getItems().stream()
                        .filter(service -> service.getMetadata() != null
                                && service.getMetadata().getName() != null
                                && service.getMetadata().getName().contains(searchText))
                        .forEach(service -> {
                            Object[] row = {
                                    service.getMetadata().getName(),
                                    "Service",
                                    service.getMetadata().getNamespace(),
                                    "NA"
                            };
                            model.addRow(row);
                        });
            }
        } catch (Exception ignored) {

        }

        if (model.getRowCount() > 0) {
            tableSearch.setModel(model);
            StyleFunctions.formatTables(tableSearch);
            hideContentPanels();
            searchPanel.setVisible(true);

        }else {
            new InfoDialog("No Results Found", IconType.NOTFOUND);

        }
        searchBar.setText("");


    }

    private void getPodRamRank() throws Exception {
        DefaultListModel listModel = new DefaultListModel();
        for (Result result :client.getPodService().getTopFivePodsByMemoryUsage()){
            String podName = result.getMetric().getPod();
            if (podName != null && !podName.isBlank()) {
                listModel.addElement(podName);
            }


        }
        listPodRamRank.setModel(listModel);
        listPodRamRank.setFont(new Font("JetBrains Mono Medium", Font.ITALIC, 14));

        StyleFunctions.setBadgesInList(listPodRamRank);
        iconCard4.setText("Biggest Memory consumers");
        iconCard4.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 16));

        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/trofeuRam.png"))).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        iconCard4.setIcon(new ImageIcon(logo));
    }

    private void getPodCpuRank() throws Exception {
        DefaultListModel listModel = new DefaultListModel();
        for (Result result :client.getPodService().getTopFivePodsByCpuUsage()){
            String podName = result.getMetric().getPod();
            if (podName != null) {
                listModel.addElement(podName);
            }


        }
        listPodCpuRank.setModel(listModel);
        listPodCpuRank.setFont(new Font("JetBrains Mono Medium", Font.ITALIC, 14));

        StyleFunctions.setBadgesInList(listPodCpuRank);
        iconCard3.setText("Biggest CPU consumers");
        iconCard3.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 16));

        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/trofeuCpu.png"))).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        iconCard3.setIcon(new ImageIcon(logo));
    }

    private void getClusterSpecs() throws Exception {
        Result nodeInfo = client.getClusterService().getClusterSpecs().getFirst();
        int cpuCapacity = client.getClusterService().getTotalCpuCores();
        String ramCapacity = client.getClusterService().getTotalRam();
        Font font = new Font("JetBrains Mono Medium", Font.ITALIC, 14);

        cpuLabelCard5.setText("Total CPU Cores: " + cpuCapacity);
        cpuLabelCard5.setFont(font);
        ramLabelCard5.setText("Total Memory: " + ramCapacity);
        ramLabelCard5.setFont(font);

        ipLabelCard5.setText("IP : " + nodeInfo.getMetric().getInternal_ip());
        ipLabelCard5.setFont(font);

        kernelLabelCard5.setText("Kernel Version : " + nodeInfo.getMetric().getKernelVersion());
        kernelLabelCard5.setFont(font);

        kubLabelCard5.setText("Kubelet Version : " + nodeInfo.getMetric().getKubeletVersion());
        kubLabelCard5.setFont(font);

        osLabelCard5.setText("OS : " + nodeInfo.getMetric().getOsImage());
        osLabelCard5.setFont(font);


        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/nodesList.png"))).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        iconCard5.setIcon(new ImageIcon(logo));
        iconCard5.setText("Cluster Information");
        iconCard5.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 16));
    }

    private void getCPUPercentage() throws Exception {
        double value = client.getClusterService().getCPUPercentage();
        ClusterDAO.getInstance().saveHistory("CPU",setValueColor(value, valueCard1));
        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/cpu.png"))).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        iconCard1.setIcon(new ImageIcon(logo));
        iconCard1.setText("Cluster CPU Usage");
        iconCard1.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 16));
        valueCard1.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 48));

    }

    private void getRAMPercentage() throws Exception {
        double value = client.getClusterService().getRamPercentage();
        ClusterDAO.getInstance().saveHistory("RAM",setValueColor(value, valueCard2));
        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/ram.png"))).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        iconCard2.setIcon(new ImageIcon(logo));
        iconCard2.setText("Cluster Memory Usage");
        iconCard2.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 16));
        valueCard2.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 48));

    }

    private void getNodeStatusTable() throws Exception {
        String[] columNames = new String[]{"Name", "Status", "Ip"};
        DefaultTableModel model = new DefaultTableModel(columNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Result result : client.getNodeService().getReadyNodes()) {
            String nodeName = result.getMetric().getNode();
            String status = result.getValue().getLast().toString();
            String ip = result.getMetric().getInternal_ip();
            if (status.equals("0")){
                status = "Down";
            }else{
                status = "Up";
            }
            Object[] row = {
                    nodeName,
                    status,
                    ip
            };
            model.addRow(row);
        }
        tableNodeStat.setModel(model);
        Font font = new Font("JetBrains Mono Medium", Font.BOLD, 16);
        StyleFunctions.formatTables(tableNodeStat);
        titleTable.setText("Nodes Status");
        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/nodesList.png"))).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        titleTable.setIcon(new ImageIcon(logo));
        titleTable.setFont(font);

    }

    ////################Utilities Functions#################
    ///

    private void refreshDashboard() {

        ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {

            try {

                // Backend/data operations
                getCPUPercentage();
                getRAMPercentage();
                getNodeStatusTable();
                getPodCpuRank();
                getPodRamRank();
                getClusterSpecs();

                // UI updates
                SwingUtilities.invokeLater(() -> {
                    try {
                        buildGraphic();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, 0, 5, TimeUnit.MINUTES);
    }

    private String setValueColor(double value, JLabel valueCard1) {
        if (value > 75.00 && value < 85.00) {
            valueCard1.setForeground(Color.ORANGE);
        }else if (value > 85.00) {
            valueCard1.setForeground(Color.RED);
        }
        String stringValue = String.format("%02.2f", value );
        valueCard1.setText(stringValue+"%");
        return stringValue;
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    private KubernetesClient connectClient(){
        return new KubernetesClient(
                AppSetup.getK3sHost(),
                AppSetup.getK3sToken()
        );
    }

    private void fillFilterComboboxByNamespaces(JComboBox comboBox) throws ApiException {
        comboBox.removeAllItems();
        for (V1Namespace namespace : client.getNamespaceService().getAllNamespaces().getItems()){
            if (namespace.getMetadata() != null) {
                comboBox.addItem(namespace.getMetadata().getName());
            }
        }
        comboBox.setSelectedIndex(-1);
        StyleFunctions.setComboBoxStyle(comboBox);
    }

    private void setStyle() {
        //Define Font style
        Font font = new Font("JetBrains Mono Medium", Font.BOLD, 16);

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/logo.png"))).getImage().getScaledInstance(100, 70, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(icon));

        //Set Background Colors
        // --- Botões NavBar ---
        AbstractButton[] buttons = {
                dashboardButton,nodesButton,podsButton,namespacesButton, deploymentsButton,servicesButton
        };

        for (AbstractButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(false);
            btn.setFont(font);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/"+ btn.getText().toLowerCase() +".png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(icon));

            icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/"+ btn.getText().toLowerCase() +"Selected.png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            btn.setRolloverIcon(new ImageIcon(icon));

        }

        searchIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/search.png"))).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        searchIcon.setIcon(new ImageIcon(icon));

        icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/searchSelected.png"))).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        searchIcon.setRolloverIcon(new ImageIcon(icon));


        buttons = new AbstractButton[]{
                addNodeButton,deleteNodeButton, //Nodes
                createServiceButton,deleteServiceButton,//Services
                createDeploymentButton,deleteDeploymentButton, //Deployment Buttons
                addPodButton,deletePodButton, //Pods Buttons
                buttonChatBot
        };

        for (AbstractButton btn : buttons) {
            StyleFunctions.setIconsButton(btn);
            if (Objects.equals(btn.getActionCommand(), "add")){
                icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/add.png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            }
            if (Objects.equals(btn.getActionCommand(), "delete")){
                icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/delete.png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);

            }
            if (Objects.equals(btn.getActionCommand(), "chat")){
                icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/chat.png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);

            }
            btn.setIcon(new ImageIcon(icon));
        }
        buttonChatBot.setBackground(new Color(30,41,60));
        buttonChatBot.setBorderPainted(false);

        StyleFunctions.setTextFieldStyle(searchBar,false);
        StyleFunctions.setTextAreaStyle(userInputArea,true);
        userInputArea.setBackground(new Color(30,41,60));

        setChatStyles();
        chatWindow.setVisible(false);

    }

    private void hideContentPanels() {
        for(Component c : contentPanel.getComponents()){
            if(c.getClass().equals(JPanel.class)){
                c.setVisible(false);
            }
        }
    }

    private void resetButton(JButton button) {
        button.setBackground(new Color(30,41,60));
        button.setForeground(new Color(115,115,115));
        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/"+ button.getText().toLowerCase() +".png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(logo));
        button.setBorder(
                BorderFactory.createEmptyBorder(
                        5, 10, 5, 10
                )
        );
        searchBar.setText("");
    }

    private void navbarButtonEffect(JButton button) {
        button.setBackground(new Color(56,124,245,20));
        button.setForeground(new Color(222, 222, 222));
        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/"+ button.getText().toLowerCase() +"Selected.png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(logo));
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 5, 0, 0,
                        new Color(56,124,245)
                ),
                BorderFactory.createEmptyBorder(
                        5, 10, 5, 10
                )
        ));
    }

    private void buildGraphic() throws Exception {
        DefaultCategoryDataset data = buildDataset();


        JFreeChart chart = ChartFactory.createLineChart(
                "Cluster Usage Overview",
                "Time",
                "Usage %",
                data
        );

        // COLORS
        Color secondaryColor = Color.decode("#1E293C");
        Color highlightColor = Color.decode("#387CF5");
        Color ramcolor = Color.GREEN;
        Color textColor = Color.decode("#DEDEDE");
        Color secondaryTextColor = Color.decode("#737373");

        // CHART BACKGROUND
        chart.setBackgroundPaint(secondaryColor);

        // TITLE
        chart.getTitle().setPaint(textColor);
        chart.getTitle().setFont(
                new Font("SansSerif", Font.BOLD, 18)
        );

        // PLOT
        CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(secondaryColor);

        plot.setOutlinePaint(null);

        // GRIDLINES
        plot.setRangeGridlinePaint(
                secondaryTextColor
        );

        plot.setDomainGridlinesVisible(false);

        // AXIS COLORS
        CategoryAxis domainAxis = plot.getDomainAxis();

        domainAxis.setTickLabelPaint(textColor);
        domainAxis.setLabelPaint(textColor);
        domainAxis.setAxisLinePaint(secondaryTextColor);

        NumberAxis rangeAxis =
                (NumberAxis) plot.getRangeAxis();

        rangeAxis.setTickLabelPaint(textColor);
        rangeAxis.setLabelPaint(textColor);
        rangeAxis.setAxisLinePaint(secondaryTextColor);

        // RENDERER
        LineAndShapeRenderer renderer =
                (LineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesPaint(0, highlightColor);
        renderer.setSeriesPaint(1, ramcolor);

        renderer.setSeriesStroke(
                0,
                new BasicStroke(3f)
        );

        renderer.setDefaultShapesVisible(true);
        // TOOLTIP
        renderer.setDefaultToolTipGenerator(
                (dataset, row, column) -> String.format(
                        "<html>" +
                                "<div style='padding:5px'>" +
                                "<b>%s</b><br>" +
                                "Usage: %s%%" +
                                "</div></html>",
                        dataset.getColumnKey(column),
                        dataset.getValue(row, column)
                )
        );
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        // LEGEND
        if (chart.getLegend() != null) {

            chart.getLegend().setBackgroundPaint(
                    secondaryColor
            );

            chart.getLegend().setItemPaint(textColor);
        }

        ChartPanel chartPanel = new ChartPanel(chart);


        graphicPanel.add(chartPanel, BorderLayout.CENTER);
    }

    @NotNull
    private static DefaultCategoryDataset buildDataset() throws Exception {
        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();

        List<History> historic = ClusterDAO.getInstance().getLastHourHistory();
        for (History history : historic) {
            dataset.addValue(Double.parseDouble(history.value.replace(",", ".")),history.type,history.time);
        }
        return dataset;
    }

    private void setChatStyles(){

        userStyle = chatBox.addStyle("UserStyle", null);
        StyleConstants.setAlignment(userStyle, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setForeground(userStyle, new Color(222,222,222));
        StyleConstants.setBackground(userStyle, new Color(0, 120, 215,94));
        StyleConstants.setFontFamily(userStyle, "Consolas");
        StyleConstants.setFontSize(userStyle, 14);

        aiStyle = chatBox.addStyle("AIStyle", null);
        StyleConstants.setAlignment(aiStyle, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(aiStyle, new Color(222,222,222));
        StyleConstants.setBackground(aiStyle, new Color(60, 60, 60,94));
        StyleConstants.setFontFamily(aiStyle, "Consolas");
        StyleConstants.setFontSize(aiStyle, 14);
    }


}
