package ui;

import Handlers.AppSetup;
import api.KubernetesClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1Status;
import model.Result;
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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
    private JLabel titleCard1;
    private JLabel iconCard1;
    private JLabel valueCard1;
    private JLabel titleCard2;
    private JLabel iconCard2;
    private JLabel valueCard2;
    private JLabel titleCard3;
    private JPanel graphicPanel;
    private JList listCard3;
    private JLabel iconCard3;
    private JPanel Card1;
    private JPanel Card2;
    private JLabel iconCard4;
    private JPanel Card4;
    private JLabel titleCard4;
    private JList listCard4;
    private JList listCard5;
    private JLabel iconCard5;
    private JLabel titleCard5;
    private JButton createDeploymentButton;
    private JButton deleteDeploymentButton;
    private JComboBox comboBoxDeployment;
    private JTable deploymentTable;
    private JButton createNamespaceButton;
    private JButton deleteNamespaceButton;
    private JTable namespaceTable;
    private JTable valueTableCard3;
    private KubernetesClient client;

    public Dashboard() throws Exception {

        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/logo.png"))).getImage().getScaledInstance(100, 70, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(logo));

        logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/search.png"))).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        searchIcon.setIcon(new ImageIcon(logo));

        setStyle();

        //Create Client
        try {
            this.client = connectClient();
        }catch (Exception e){
            System.out.println("Error Connecting: " + e.getMessage());
        }


        /*getCPUPercentage();
        getRAMPercentage();
        getNodesStatus();
        buildGraphic();*/

        deploymentsButton.addActionListener(this::btnDeleteDeployment);


        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Component c : navbarPanel.getComponents()){
                    if(c.getClass().equals(JButton.class)){
                        resetButton((JButton) c);
                    }
                }
                buttonEffect(dashboardButton);
                hideContentPanels();
                metricsPanel.setVisible(true);


            }
        });
        nodesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Component c : navbarPanel.getComponents()){
                    if(c.getClass().equals(JButton.class)){
                        resetButton((JButton) c);
                    }
                }
                buttonEffect(nodesButton);
                hideContentPanels();
                nodesPanel.setVisible(true);

            }
        });

        podsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Component c : navbarPanel.getComponents()){
                    if(c.getClass().equals(JButton.class)){
                        resetButton((JButton) c);
                    }
                }
                buttonEffect(podsButton);
                hideContentPanels();
                podsPanel.setVisible(true);

            }
        });

        deploymentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Component c : navbarPanel.getComponents()){
                    if(c.getClass().equals(JButton.class)){
                        resetButton((JButton) c);
                    }
                }
                buttonEffect(deploymentsButton);
                hideContentPanels();
                deploymentsPanel.setVisible(true);

                try {
                    fillDeploymentTable();
                } catch (ApiException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        servicesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Component c : navbarPanel.getComponents()){
                    if(c.getClass().equals(JButton.class)){
                        resetButton((JButton) c);
                    }
                }
                buttonEffect(servicesButton);
                hideContentPanels();
                servicesPanel.setVisible(true);

            }
        });

        namespacesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Component c : navbarPanel.getComponents()){
                    if(c.getClass().equals(JButton.class)){
                        resetButton((JButton) c);
                    }
                }
                buttonEffect(namespacesButton);
                hideContentPanels();
                namespacesPanel.setVisible(true);

                fillNamespaceTable();

            }
        });

        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                String text = searchBar.getText();
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !text.isBlank()) {
                    serachFunction(text);
                }
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

    private void fillDeploymentTable() throws ApiException {

        comboBoxDeployment.addItem(""); // opção vazia por default

        for (V1Namespace namespace : client.getNamespaceService().getAllNamespaces().getItems()) {
            comboBoxDeployment.addItem(namespace.getMetadata().getName());
        }


        String[] columnNames = {"Name", "Namespace", "Resource Version"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            for (V1Deployment dep : client.getDeploymentService().getAllDeployments().getItems()) {
                Object[] row = {
                        dep.getMetadata().getName(),
                        dep.getMetadata().getNamespace(),
                        dep.getMetadata().getResourceVersion()
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

    private void getCPUPercentage() throws Exception {
        Result result = client.getClusterService().getCPUPercentage().getLast();
        double value = Double.parseDouble(result.getValue().getLast().toString());
        value = value * 100;
        if (value > 75.00) {
            valueCard1.setForeground(Color.ORANGE);
        }else if (value > 85.00) {
            valueCard1.setForeground(Color.RED);
        }
        valueCard1.setText(String.format("%02.2f%%", value ));

        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/cpu.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        iconCard1.setIcon(new ImageIcon(logo));
        titleCard1.setText("CPU USAGE");
        titleCard1.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 16));

    }

    private void getRAMPercentage() throws Exception {
        Result result = client.getClusterService().getRamPercentage().getLast();
        double value = Double.parseDouble(result.getValue().getLast().toString());
        if (value > 75.00) {
            valueCard2.setForeground(Color.ORANGE);
        }else if (value > 85.00) {
            valueCard2.setForeground(Color.RED);
        }
        valueCard2.setText(String.format("%02.2f%%", value ));

        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/ram.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        iconCard2.setIcon(new ImageIcon(logo));
        titleCard2.setText("RAM USAGE");
        titleCard2.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 16));

    }


    private void getNodesStatus() throws Exception {
        DefaultListModel listModel = new DefaultListModel();

        for (Result result :client.getNodeService().getReadyNodes()){
            String nodeName = result.getMetric().getNode();
            String status = result.getValue().getLast().toString();
            if (status.equals("0")){
                status = "Down";
            }else{
                status = "Up";
            }

            listModel.addElement(nodeName + ":" + status );

        }
        listCard3.setModel(listModel);
        listCard3.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                String[] parts = value.toString().split(":");

                String nodeName = parts[0];
                String status = parts[1];

                String statusColor;
                System.out.println(status);
                if (status.equalsIgnoreCase("Down")) {
                    statusColor = "red";
                } else {
                    statusColor = "#00ff00";
                }

                label.setText(
                        "<html><div style='text-align:center;'>"
                                + "<span style='color:white;'>"
                                + nodeName
                                + "</span>"
                                + " : "
                                + "<span style='color:" + statusColor + ";'>"
                                + status
                                + "</span>"
                                + "</div></html>"
                );


                label.setHorizontalAlignment(SwingConstants.LEFT);

                Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/nodesList.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
                iconCard3.setIcon(new ImageIcon(logo));
                label.setFont(new Font("JetBrains Mono Medium", Font.PLAIN, 16));

                return label;
            }
        });
        listCard3.setVisible(true);
        titleCard3.setText("Nodes Status");
        titleCard3.setFont(new Font("JetBrains Mono Medium", Font.BOLD, 16));

    }

    private void setStyle() {
        //Define Font style
        Font font = new Font("JetBrains Mono Medium", Font.BOLD, 16);

        //Set Background Colors
        // --- Botões ---
        AbstractButton[] buttons = {
                dashboardButton,nodesButton,podsButton,namespacesButton, deploymentsButton,servicesButton
        };

        for (AbstractButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(false);
            btn.setFont(font);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/"+ btn.getText().toLowerCase() +".png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(logo));

            logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/"+ btn.getText().toLowerCase() +"Selected.png"))).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            btn.setRolloverIcon(new ImageIcon(logo));

        }

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
    }

    private void buttonEffect(JButton button) {
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


        JFreeChart chart = ChartFactory.createLineChart(
                "Cluster Usage Overview",
                "Time",
                "Usage %",
                dataset
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
                (datasete, row, column) -> {

                    return String.format(
                            "<html>" +
                                    "<div style='padding:5px'>" +
                                    "<b>%s</b><br>" +
                                    "Usage: %s%%" +
                                    "</div></html>",
                            datasete.getColumnKey(column),
                            datasete.getValue(row, column)
                    );
                }
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




}
