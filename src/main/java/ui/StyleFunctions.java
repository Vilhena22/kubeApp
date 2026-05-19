package ui;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class StyleFunctions {
    private static final Color hover = new Color(6, 63, 154);
    private static final Color bg = new Color(30, 41, 60);
    private static final Color text = new Color(222,222,222);
    private static final Color border = new Color(30, 41, 60);
    private static final Color iconButtonBG = new Color(19,27,47);
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
        btn.setBackground(iconButtonBG);
        btn.setForeground(text);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setOpaque(false);
        btn.setFont(font);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(iconButtonBG));
        /*btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(iconButtonBG);
            }
        });*/
    }

    public  static void setTextFieldStyle(JTextField searchBar){
        searchBar.setBackground(new Color(19,27,47));
        searchBar.setForeground(text);
        searchBar.setCaretColor(text);
        searchBar.setSelectionColor(hover);
        searchBar.setSelectedTextColor(text);
        searchBar.setBorder(BorderFactory.createLineBorder(border));

        searchBar.setFont(new Font("JetBrains Mono Medium", Font.PLAIN, 16));

        searchBar.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                searchBar.setBorder(BorderFactory.createLineBorder(hover));
            }

            @Override
            public void focusLost(FocusEvent e) {
                searchBar.setBorder(BorderFactory.createLineBorder(border));
            }
        });
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
}
