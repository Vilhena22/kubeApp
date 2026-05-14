import Handlers.AppSetup;
import api.KubernetesClient;
import com.formdev.flatlaf.FlatDarkLaf;
import model.Result;
import ui.Dashboard;
import io.kubernetes.client.openapi.models.*;
import service.DeploymentService;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        try {

//
            FlatDarkLaf.setup();
            JFrame frame = new JFrame("Dashboard");
            Dashboard dashboard = new Dashboard();
            frame.setContentPane(dashboard.getPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);


        } catch (Exception e) {
            System.out.println("Error Connecting: " + e.getMessage());
        }

    }
}
