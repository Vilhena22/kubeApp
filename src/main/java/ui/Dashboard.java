package ui;

import Handlers.AppSetup;
import api.KubernetesClient;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.openapi.models.*;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.Objects;

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
    private JTable deploymentTable;
    private JButton createNamespaceButton;
    private JButton deleteNamespaceButton;
    private JTable namespaceTable;
    private JTable valueTableCard3;
    private JTable tableDeployments;
    private JTable tableNodes;
    private JPanel searchPanel;
    private JTable tableSearch;
    private JTable tablePods;
    private JComboBox comboBoxFilterPodByNamespace;
    private JButton addPodButton;
    private JButton deletePodButton;
    private KubernetesClient client;

    public Dashboard() throws Exception {

        setStyle();

        //Create Client
        try {
            this.client = connectClient();
        }catch (Exception e){
            System.out.println("Error Connecting: " + e.getMessage());
        }


        getCPUPercentage();
        getRAMPercentage();
        getNodeStatusTable();
        getPodCpuRank();
        getPodRamRank();
        getClusterSpecs();
        buildGraphic();
        navbarButtonEffect(dashboardButton);

        deploymentsButton.addActionListener(this::btnDeleteDeployment);

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
                fillFilterCombobox(comboBoxFilterPodByNamespace);
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
    }

    private void btnDeleteDeployment(ActionEvent actionEvent) {

        String name = "";
        String namespace = "";

        for (int row : deploymentTable.getSelectedRows()) {
            try {
                name = deploymentTable.getValueAt(row,0).toString();
                namespace = deploymentTable.getValueAt(row, 1 ).toString();
                client.getDeploymentService().deleteDeployment(name, namespace);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    ///################Nodes#################
    ///

    private void fillNodesTable() throws Exception {
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
            V1PodList podList = client.getPodService().listPodsByNode(nodeName); // listPodForAllNamespaces
            long runningPods = podList.getItems().stream()
                    .filter(pod -> pod.getSpec() != null
                            && Objects.requireNonNull(nodeName).equals(pod.getSpec().getNodeName())
                            && pod.getStatus() != null
                            && "Running".equals(pod.getStatus().getPhase()))
                    .count();

            Object[] row = {
                    nodeName,
                    status,
                    ip,
                    runningPods,
                    capacityPods
            };
            model.addRow(row);
        }
        tableNodes.setModel(model);
        formatTables(tableNodes);

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


    private void fillPodsTable(String namespace) throws Exception {
        String[] columNames = new String[]{"Name", "Status", "Node", "Namespace", "Pod IP", "Creation Date"};
        DefaultTableModel model = new DefaultTableModel(columNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        V1PodList podList = client.getPodService().getAllPods();
        if (!namespace.isEmpty()) {
            podList.getItems().clear();
            podList = client.getPodService().getAllPodsOnNamespace(namespace);
        }
        for (V1Pod pod : podList.getItems()) {
            Object[] row = getObjects(pod);
            model.addRow(row);
        }
        tablePods.setModel(model);
        formatTables(tablePods);

    }

    @NotNull
    private static Object[] getObjects(V1Pod pod) {
        String podName = Objects.requireNonNull(pod.getMetadata()).getName();
        String status = Objects.requireNonNull(pod.getStatus()).getPhase();
        String nameSpace = pod.getMetadata().getNamespace();
        String node = Objects.requireNonNull(pod.getSpec()).getNodeName();
        String ip = pod.getStatus().getPodIP();
        String date = Objects.requireNonNull(pod.getMetadata().getCreationTimestamp()).toString();
        return new Object[]{
                podName,
                status,
                node,
                nameSpace,
                ip,
                date
        };
    }

    ////################Deployments#################
    ///
    private void fillDeploymentTable() throws ApiException {

        comboBoxDeployment.addItem(""); // opção vazia por default

        for (V1Namespace namespace : client.getNamespaceService().getAllNamespaces().getItems()) {
            comboBoxDeployment.addItem(namespace.getMetadata().getName());
        }


        String[] columnNames = {"Name", "Namespace", "Resource Version"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        fillFilterCombobox(comboBoxDeployment);
        String[] columNames = {"Name", "Namespace", "Resource Version"};
        try {
            for (V1Deployment dep : client.getDeploymentService().getAllDeployments().getItems()) {
                Object[] row = {
                        dep.getMetadata() != null ? dep.getMetadata().getName() : null,
                        dep.getMetadata() != null ? dep.getMetadata().getNamespace() : null,
                        dep.getMetadata() != null ? dep.getMetadata().getResourceVersion() : null
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        deploymentTable.setModel(model);

        //centrar o texto das colunas 1 e 2
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 1; i < deploymentTable.getColumnCount(); i++) {
            deploymentTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public void fillNamespaceTable(){

        String[] columnNames = {"Name", "Creation Timestamp", "State"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            for (V1Namespace nm : client.getNamespaceService().getAllNamespaces().getItems()) {
                Object[] row = {
                        nm.getMetadata().getName(),
                        nm.getMetadata().getCreationTimestamp(),
                        nm.getStatus().getPhase()
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        namespaceTable.setModel(model);

        //centrar o texto das colunas 1 e 2
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 1; i < namespaceTable.getColumnCount(); i++) {
            namespaceTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public JPanel getPanel() {
        return mainPanel;
    }


    private void serachFunction(String text) {

    }

    private KubernetesClient connectClient(){
            return new KubernetesClient(
                    AppSetup.getK3sHost(),
                    AppSetup.getK3sToken()
            );
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

        if (model.getRowCount() > 0) {
            tableSearch.setModel(model);
            formatTables(tableSearch);
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

        setBadgesInList(listPodRamRank);
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

        setBadgesInList(listPodCpuRank);
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
        Result result = client.getClusterService().getCPUPercentage().getLast();
        double value = Double.parseDouble(result.getValue().getLast().toString());
        value = value * 100;
        if (value > 75.00 && value < 85.00) {
            valueCard1.setForeground(Color.ORANGE);
        }else if (value > 85.00) {
            valueCard1.setForeground(Color.RED);
        }
        valueCard1.setText(String.format("%02.2f%%", value ));

        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/cpu.png"))).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        iconCard1.setIcon(new ImageIcon(logo));
        iconCard1.setText("Cluster CPU Usage");
        iconCard1.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 16));
        valueCard1.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 48));

    }

    private void getRAMPercentage() throws Exception {
        Result result = client.getClusterService().getRamPercentage().getLast();
        double value = Double.parseDouble(result.getValue().getLast().toString());
        if (value > 75.00 && value < 85.00) {
            valueCard2.setForeground(Color.ORANGE);
        }else if (value > 85.00) {
            valueCard2.setForeground(Color.RED);
        }
        valueCard2.setText(String.format("%02.2f%%", value ));

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
        formatTables(tableNodeStat);
        titleTable.setText("Nodes Status");
        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/nodesList.png"))).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        titleTable.setIcon(new ImageIcon(logo));
        titleTable.setFont(font);

    }

    ////################Utilities Functions#################
    ///

    private void fillFilterCombobox(JComboBox comboBox) throws ApiException {
        comboBox.removeAllItems();
        for (V1Namespace namespace : client.getNamespaceService().getAllNamespaces().getItems()){
            if (namespace.getMetadata() != null) {
                comboBox.addItem(namespace.getMetadata().getName());
            }
        }
        comboBox.setSelectedIndex(-1);
        StyleFunctions.setComboBoxStyle(comboBox);
    }

    private void formatTables(JTable table) {
        table.getTableHeader().setReorderingAllowed(false);

        Color color1 = new Color(19,27,47);   // adjust to your liking
        Color color2 = new Color(30,41,60);   // adjust to your liking
        Color colorText = new Color(222, 222, 222);
        Color highlight = new Color(56,124,245);
        Font font = new Font("JetBrains Mono Medium", Font.BOLD, 16);
        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {

            private final JPanel badgePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (currentStatus == null) return;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int arc  = 12;
                    int padX = 12;
                    FontMetrics fm = g2.getFontMetrics(font);
                    int textW  = fm.stringWidth(currentStatus);
                    int badgeW = textW + padX * 2;
                    paintBadge(g2, currentBg, currentText, currentBorder, currentStatus, font, fm, arc, padX, badgeW, getWidth(), getHeight());
                }
            };

            private String currentStatus;
            private Color currentBg, currentText, currentBorder;
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                if (value == null) value = "N/A";

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                Color rowBg;

                if (isSelected) {
                    rowBg = highlight;
                } else {
                    rowBg = (row % 2 == 0) ? color2 : color1;
                }
                label.setBackground(rowBg);
                label.setForeground(colorText);
                label.setOpaque(true);

                if (column == 1) {
                    // Return a custom badge panel instead of a plain label
                    currentStatus = value.toString();
                    Color[] colors = getBadgeColors(currentStatus);
                    currentBg     = colors[0];
                    currentText   = colors[1];
                    currentBorder = colors[2];
                    badgePanel.setBackground(rowBg);
                    badgePanel.setOpaque(true);
                    return badgePanel;

                } else {
                    label.setText(
                            "<html><div style='width:100%; text-align:center; color:white;'>"
                                    + value + "</div></html>"
                    );
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setVerticalAlignment(SwingConstants.CENTER);
                    label.setFont(font);
                    label.setForeground(colorText);
                    label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                    return label;
                }
            }

        };

        final int[] hoveredColumn = getInts(table);


        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                Color hoverColor = new Color(45, 60, 90);

                if (column == hoveredColumn[0]) {
                    label.setBackground(hoverColor);
                } else {
                    label.setBackground(color1);
                }
                label.setForeground(colorText);
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(font);
                label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

                return label;
            }
        };

        // This locks the header background regardless of hover/press states
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        table.getTableHeader().setBackground(color1);
        table.getTableHeader().setForeground(colorText);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));

        table.getTableHeader().setUI(new BasicTableHeaderUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                g.setColor(color1);
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
                super.paint(g, c);
            }
        });

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);
            table.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        TableRowSorter<TableModel> sorter =
                new TableRowSorter<>(table.getModel());

        table.setRowSorter(sorter);
        table.getTableHeader().setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );


        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setSelectionBackground(highlight);
        table.setRowHeight(35);
        table.setVisible(true);
    }

    @NotNull
    private static int[] getInts(JTable table) {
        final int[] hoveredColumn = {-1};

        JTableHeader header = table.getTableHeader();

        header.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = header.columnAtPoint(e.getPoint());

                if (hoveredColumn[0] != col) {
                    hoveredColumn[0] = col;
                    header.repaint();
                }
            }
        });

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredColumn[0] = -1;
                header.repaint();
            }
        });
        return hoveredColumn;
    }

    private void setBadgesInList(JList list) {
        Color color1 = new Color(19,27,47);   // adjust to your liking
        Color color2 = new Color(30,41,60);   // adjust to your liking
        Color colorText = new Color(222,222,222);
        list.setCellRenderer(new ListCellRenderer<String>() {
            @Override
            public Component getListCellRendererComponent(
                    JList<? extends String> list, String value,
                    int index, boolean isSelected, boolean cellHasFocus) {

                JPanel row = new JPanel(new BorderLayout(10, 0));
                row.setOpaque(true);
                row.setBackground((index % 2 == 0) ? color2 : color1);
                row.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                // Badge panel
                int badgeNumber = index + 1;
                JPanel badge = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Badge color changes per rank
                        Color badgeBg;
                        Color badgeText;
                        Color badgeBorder;
                        switch (badgeNumber) {
                            case 1 -> { badgeBg = new Color(120, 80, 0);  badgeBorder = new Color(255, 180, 0);  badgeText = new Color(255, 210, 0);  } // gold
                            case 2 -> { badgeBg = new Color(60, 70, 80);  badgeBorder = new Color(160, 180, 200); badgeText = new Color(200, 215, 230); } // silver
                            case 3 -> { badgeBg = new Color(80, 40, 10);  badgeBorder = new Color(180, 100, 40);  badgeText = new Color(210, 130, 60);  } // bronze
                            default -> { badgeBg = new Color(40, 40, 60); badgeBorder = new Color(100, 100, 140); badgeText = new Color(180, 180, 220); } // default
                        }

                        String text = "#" + badgeNumber;
                        Font badgeFont = new Font("JetBrains Mono Medium", Font.BOLD, 13);
                        FontMetrics fm = g2.getFontMetrics(badgeFont);
                        int arc = 10;
                        int padX = 8;
                        int badgeW = fm.stringWidth(text) + padX * 2;
                        paintBadge(g2, badgeBg, badgeText, badgeBorder, text, badgeFont, fm, arc, padX, badgeW, getWidth(), getHeight());
                    }


                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(45, 30);
                    }
                };

                badge.setOpaque(false);

                // Pod name label
                JLabel nameLabel = new JLabel(value);
                nameLabel.setFont(new Font("JetBrains Mono Medium", Font.ITALIC, 14));
                nameLabel.setForeground(colorText);

                row.add(badge, BorderLayout.WEST);
                row.add(nameLabel, BorderLayout.CENTER);

                return row;
            }
        });
    }

    private Color[] getBadgeColors(String type) {
        return switch (type.toLowerCase()) {
            case "node"       -> new Color[]{new Color(20, 60, 100),  new Color(80, 160, 255), new Color(40, 100, 180)};
            case "deployment" -> new Color[]{new Color(80, 40, 120),  new Color(180, 80, 255), new Color(120, 50, 180)};
            case "pod"        -> new Color[]{new Color(20, 80, 60),   new Color(0, 220, 130),  new Color(0, 160, 90)};
            case "namespace"  -> new Color[]{new Color(100, 60, 0),   new Color(255, 180, 0),  new Color(180, 120, 0)};
            case "up", "running" -> new Color[]{new Color(0, 80, 40),    new Color(0, 255, 100),  new Color(0, 200, 80)};
            case "down"       -> new Color[]{new Color(80, 20, 20),   new Color(255, 80, 80),  new Color(200, 50, 50)};
            case "pending"   -> new Color[]{new Color(90, 70, 20), new Color(255, 210, 80),new Color(180, 140, 40)};
            case "succeeded" -> new Color[]{new Color(20, 60, 120),new Color(80, 180, 255),new Color(40, 120, 200)};
            case "failed"    -> new Color[]{new Color(100, 20, 20),new Color(255, 60, 60),new Color(180, 30, 30)};
            case "unknown"   -> new Color[]{new Color(70, 70, 70),new Color(180, 180, 180),new Color(120, 120, 120)};
            default           -> new Color[]{new Color(50, 50, 50),   new Color(180, 180, 180),new Color(100, 100, 100)};
        };
        // [0] = background, [1] = text, [2] = border
    }

    private void paintBadge(Graphics2D g2, Color badgeBg, Color badgeText, Color badgeBorder, String text, Font badgeFont, FontMetrics fm, int arc, int padX, int badgeW, int width, int height) {
        int badgeH = 22;
        int x = (width - badgeW) / 2;
        int y = (height - badgeH) / 2;

        g2.setColor(badgeBg);
        g2.fillRoundRect(x, y, badgeW, badgeH, arc, arc);

        g2.setColor(badgeBorder);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(x, y, badgeW, badgeH, arc, arc);

        g2.setColor(badgeText);
        g2.setFont(badgeFont);
        int textX = x + padX;
        int textY = y + (badgeH - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);

        g2.dispose();
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
                createDeploymentButton,deleteDeploymentButton, //Deployment Buttons
                addPodButton,deletePodButton //Pods Buttons
        };

        for (AbstractButton btn : buttons) {
            StyleFunctions.hoverButtonEffect(btn);
        }

        StyleFunctions.setTextFieldStyle(searchBar);

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

    private void buildGraphic(){
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
    private static DefaultCategoryDataset buildDataset() {
        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();

        dataset.addValue(20, "CPU", "10:00");
        dataset.addValue(35, "CPU", "10:05");
        dataset.addValue(50, "CPU", "10:10");
        dataset.addValue(65, "CPU", "10:15");
        dataset.addValue(40, "CPU", "10:20");


        dataset.addValue(50, "RAM", "11:00");
        dataset.addValue(75, "RAM", "11:05");
        dataset.addValue(90, "RAM", "11:10");
        dataset.addValue(45, "RAM", "11:15");
        dataset.addValue(40, "RAM", "11:20");
        return dataset;
    }


}
