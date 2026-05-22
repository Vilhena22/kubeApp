package ui;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;


public class StyleFunctions {
    private static final Color hover = new Color(6, 63, 154);
    private static final Color bg = new Color(30, 41, 60);
    private static final Color text = new Color(222,222,222);
    private static final Color border = new Color(30, 41, 60);
    private static final Color secondaryBG = new Color(19,27,47);
    private static final Font font = new Font("JetBrains Mono Medium", Font.BOLD, 16);


    public static void hoverButtonEffect(AbstractButton btn){
        btn.setBackground(bg);
        btn.setForeground(text);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setOpaque(false);
        btn.setFont(font);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(border));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
    }

    public static void setIconsButton(AbstractButton btn){
        btn.setBackground(secondaryBG);
        btn.setForeground(text);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setOpaque(false);
        btn.setFont(font);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(secondaryBG));
    }

    public  static void setTextFieldStyle(JTextComponent textField, boolean primaryColor){
        setTextAreaStyle(textField,primaryColor);

        textField.setFont(new Font("JetBrains Mono Medium", Font.PLAIN, 16));

        textField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createLineBorder(hover));
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createLineBorder(border));
            }
        });
    }

    public  static void setTextAreaStyle(JTextComponent textArea,boolean primaryColor){
        if (!primaryColor){
            textArea.setBackground(secondaryBG);
        }else {
            textArea.setBackground(bg);
        }
        textArea.setForeground(text);
        textArea.setCaretColor(text);
        textArea.setSelectionColor(hover);
        textArea.setSelectedTextColor(text);
        textArea.setBorder(BorderFactory.createLineBorder(border));
        textArea.setFont(new Font("JetBrains Mono Medium", Font.PLAIN, 16));
    }

    public static void setComboBoxStyle(JComboBox comboBox){
        comboBox.setBackground(bg);
        comboBox.setForeground(text);
        comboBox.setBorder(BorderFactory.createLineBorder(border));
        comboBox.setFocusable(false);

        comboBox.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                label.setBackground(isSelected ? hover : bg);
                label.setForeground(text);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                return label;
            }
        });


        comboBox.setUI(new BasicComboBoxUI() {

            @Override
            protected JButton createArrowButton() {

                JButton button = new JButton("▼");

                button.setBorder(BorderFactory.createEmptyBorder());
                button.setFocusPainted(false);
                button.setContentAreaFilled(true);

                button.setBackground(bg);
                button.setForeground(text);

                return button;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g,
                                                    Rectangle bounds,
                                                    boolean hasFocus) {

                g.setColor(bg);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }

            @Override
            public void installUI(JComponent c) {
                super.installUI(c);

                JComboBox<?> box = (JComboBox<?>) c;
                box.setBackground(bg);
                box.setForeground(text);
                box.setBorder(BorderFactory.createLineBorder(border));
            }
        });

        comboBox.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

                JComboBox<?> box = (JComboBox<?>) e.getSource();

                Object comp = box.getUI().getAccessibleChild(box, 0);

                if (comp instanceof javax.swing.JPopupMenu popup) {
                    popup.setBorder(BorderFactory.createLineBorder(border));
                    popup.setBackground(bg);
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
    }

    public static void setBadgesInList(JList list) {
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

    public static void formatTables(JTable table) {
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

    private static Color[] getBadgeColors(String type) {
        return switch (type.toLowerCase()) {
            case "node"       -> new Color[]{new Color(20, 60, 100),  new Color(80, 160, 255), new Color(40, 100, 180)};
            case "deployment" -> new Color[]{new Color(80, 40, 120),  new Color(180, 80, 255), new Color(120, 50, 180)};
            case "pod"        -> new Color[]{new Color(20, 80, 60),   new Color(0, 220, 130),  new Color(0, 160, 90)};
            case "namespace"  -> new Color[]{new Color(100, 60, 0),   new Color(255, 180, 0),  new Color(180, 120, 0)};
            case "up", "running", "active" -> new Color[]{new Color(0, 80, 40),    new Color(0, 255, 100),  new Color(0, 200, 80)};
            case "down"       -> new Color[]{new Color(80, 20, 20),   new Color(255, 80, 80),  new Color(200, 50, 50)};
            case "pending"   -> new Color[]{new Color(90, 70, 20), new Color(255, 210, 80),new Color(180, 140, 40)};
            case "succeeded" -> new Color[]{new Color(20, 60, 120),new Color(80, 180, 255),new Color(40, 120, 200)};
            case "failed"    -> new Color[]{new Color(100, 20, 20),new Color(255, 60, 60),new Color(180, 30, 30)};
            case "unknown"   -> new Color[]{new Color(70, 70, 70),new Color(180, 180, 180),new Color(120, 120, 120)};
            case "service" -> new Color[]{new Color(20, 90, 100),new Color(0, 220, 255),new Color(0, 150, 180)};
            case "clusterip" ->new Color[]{new Color(30, 50, 120),new Color(90, 140, 255),new Color(50, 90, 200)};
            case "nodeport" ->new Color[]{new Color(120, 70, 10),new Color(255, 170, 40),new Color(200, 120, 20)};
            case "loadbalancer" ->new Color[]{new Color(90, 20, 120),new Color(220, 80, 255),new Color(160, 40, 200)};
            default           -> new Color[]{new Color(50, 50, 50),   new Color(180, 180, 180),new Color(100, 100, 100)};
        };
        // [0] = background, [1] = text, [2] = border
    }

    private static void paintBadge(Graphics2D g2, Color badgeBg, Color badgeText, Color badgeBorder, String text, Font badgeFont, FontMetrics fm, int arc, int padX, int badgeW, int width, int height) {
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

}
