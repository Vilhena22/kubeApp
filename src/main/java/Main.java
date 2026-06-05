
import Handlers.ClusterDAO;
import com.formdev.flatlaf.FlatDarkLaf;
import ui.*;
import io.kubernetes.client.openapi.models.*;

import javax.swing.*;
import java.awt.*;
public class Main {
    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
            ClusterDAO dao = new ClusterDAO();
            dao.createTable();
            JFrame frame = new JFrame("Dashboard");
            Dashboard dashboard = new Dashboard();
            frame.setContentPane(dashboard.getPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);


        } catch (Exception e) {
            System.out.println("Error Connecting: " + e.getMessage());
        }

    }
}
