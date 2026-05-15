
import com.formdev.flatlaf.FlatDarkLaf;
import ui.Dashboard;
import io.kubernetes.client.openapi.models.*;

import javax.swing.*;
import java.awt.*;

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
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);

            /*KubernetesClient client = new KubernetesClient(
                    AppSetup.getK3sHost(),
                    AppSetup.getK3sToken()
            );

            client.getNamespaceService().getAllNamespaces().getItems().forEach(System.out::println);*/



        } catch (Exception e) {
            System.out.println("Error Connecting: " + e.getMessage());
        }

    }
}
