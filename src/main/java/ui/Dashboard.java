package ui;

import Handlers.AppSetup;
import api.KubernetesClient;
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
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private JLabel iconCard1;
    private JLabel valueCard1;
    private JLabel iconCard2;
    private JLabel valueCard2;
    private JLabel titleTable;
    private JPanel graphicPanel;
    private JPanel Card1;
    private JPanel Card2;
    private JPanel Card3;
    private JLabel iconCard3;
    private JPanel Card4;
    private JLabel iconCard4;
    private JList listPodCpuRank;
    private JList listPodRamRank;
    private JTable tableNodes;
    private JLabel iconCard5;
    private JPanel Card5;
    private JLabel cpuLabelCard5;
    private JLabel ramLabelCard5;
    private JLabel ipLabelCard5;
    private JLabel kernelLabelCard5;
    private JLabel kubLabelCard5;
    private JLabel osLabelCard5;
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


        getCPUPercentage();
        getRAMPercentage();
        getNodeStatusTable();
        getPodCpuRank();
        getPodRamRank();
        getClusterSpecs();
        buildGraphic();
        buttonEffect(dashboardButton);


        dashboardButton.addActionListener(e -> {
            for (Component c : navbarPanel.getComponents()){
                if(c.getClass().equals(JButton.class)){
                    resetButton((JButton) c);
                }
            }
            buttonEffect(dashboardButton);
            hideContentPanels();
            metricsPanel.setVisible(true);


        });
        nodesButton.addActionListener(e -> {
            for (Component c : navbarPanel.getComponents()){
                if(c.getClass().equals(JButton.class)){
                    resetButton((JButton) c);
                }
            }
            buttonEffect(nodesButton);
            hideContentPanels();
            nodesPanel.setVisible(true);

        });

        podsButton.addActionListener(e -> {
            for (Component c : navbarPanel.getComponents()){
                if(c.getClass().equals(JButton.class)){
                    resetButton((JButton) c);
                }
            }
            buttonEffect(podsButton);
            hideContentPanels();
            podsPanel.setVisible(true);

        });

        deploymentsButton.addActionListener(e -> {
            for (Component c : navbarPanel.getComponents()){
                if(c.getClass().equals(JButton.class)){
                    resetButton((JButton) c);
                }
            }
            buttonEffect(deploymentsButton);
            hideContentPanels();
            deploymentsPanel.setVisible(true);

        });

        servicesButton.addActionListener(e -> {
            for (Component c : navbarPanel.getComponents()){
                if(c.getClass().equals(JButton.class)){
                    resetButton((JButton) c);
                }
            }
            buttonEffect(servicesButton);
            hideContentPanels();
            servicesPanel.setVisible(true);

        });

        namespacesButton.addActionListener(e -> {
            for (Component c : navbarPanel.getComponents()){
                if(c.getClass().equals(JButton.class)){
                    resetButton((JButton) c);
                }
            }
            buttonEffect(namespacesButton);
            hideContentPanels();
            namespacesPanel.setVisible(true);

        });

        searchBar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                String text = searchBar.getText();
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !text.isBlank()) {
                    searchFunction(text);
                }
            }
        });
    }


    public JPanel getPanel() {
        return mainPanel;
    }

    private void searchFunction(String text) {

    }

    private KubernetesClient connectClient(){
        return new KubernetesClient(
                    AppSetup.getK3sHost(),
                    AppSetup.getK3sToken()
            );
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

        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/trofeuRam.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
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

        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/trofeuCpu.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
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


        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/nodesList.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
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

        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/cpu.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
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

        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/ram.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
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
        tableNodes.setModel(model);
        tableNodes.getTableHeader().setReorderingAllowed(false);

        Color color1 = new Color(19,27,47);   // adjust to your liking
        Color color2 = new Color(30,41,60);   // adjust to your liking
        Color colorText = new Color(222, 222, 222);
        Font font = new Font("JetBrains Mono Medium", Font.BOLD, 16);
        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setBackground((row % 2 == 0) ? color2 : color1);
                label.setForeground(colorText);
                label.setOpaque(true);

                if (column == 1) {
                    // Return a custom badge panel instead of a plain label
                    String status = value.toString();
                    return getJPanel(row, status);

                } else {
                    label.setText(
                            "<html><div style='width:100%; text-align:center; color:white;'>"
                                    + value.toString() + "</div></html>"
                    );
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setVerticalAlignment(SwingConstants.CENTER);
                    label.setFont(font);
                    label.setForeground(colorText);
                    label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                    return label;
                }
            }

            @NotNull
            private JPanel getJPanel(int row, String status) {
                boolean isUp = status.equalsIgnoreCase("Up");

                Color badgeText = isUp ? new Color(0, 255, 100) : new Color(255, 80, 80);
                Color badgeBorder = isUp ? new Color(0, 200, 80) : new Color(200, 50, 50);
                Color badgeBg = isUp ? new Color(0, 80, 40) : new Color(80, 20, 20);
                Color rowBg = (row % 2 == 0) ? color2 : color1;

                JPanel badge = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int arc = 12;
                        int padX = 12;
                        FontMetrics fm = g2.getFontMetrics(font);
                        int textW = fm.stringWidth(status);
                        int badgeW = textW + padX * 2;
                        paintBadge(g2, badgeBg, badgeText, badgeBorder, status, font, fm, arc, padX, badgeW, getWidth(), getHeight());
                    }
                };

                badge.setBackground(rowBg);
                badge.setOpaque(true);
                return badge;
            }
        };

// Header renderer — color1 so row 0 starts on color2, creating the alternating effect
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setBackground(color1);
                label.setForeground(colorText);
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(font);
                label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

                return label;
            }
        };

        // This locks the header background regardless of hover/press states
        tableNodes.getTableHeader().setDefaultRenderer(headerRenderer);
        tableNodes.getTableHeader().setBackground(color1);
        tableNodes.getTableHeader().setForeground(colorText);
        tableNodes.getTableHeader().setPreferredSize(new Dimension(0, 35));

        tableNodes.getTableHeader().setUI(new BasicTableHeaderUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                g.setColor(color1);
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
                super.paint(g, c);
            }
        });

        for (int i = 0; i < tableNodes.getColumnCount(); i++) {
            tableNodes.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);
            tableNodes.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        tableNodes.setRowHeight(35);
        tableNodes.setVisible(true);
        titleTable.setText("Nodes Status");
        Image logo = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("./icons/nodesList.png"))).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        titleTable.setIcon(new ImageIcon(logo));
        titleTable.setFont(font);

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
