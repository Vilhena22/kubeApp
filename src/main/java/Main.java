
import Handlers.AppSetup;
import ai.AiFactory;
import ai.Assistant;
import api.KubernetesClient;
import com.formdev.flatlaf.FlatDarkLaf;
import model.IconType;
import ui.CreateNode;
import ui.CreatePod;
import ui.Dashboard;
import io.kubernetes.client.openapi.models.*;
import ui.InfoDialog;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;

public class Main {
    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
            JFrame frame = new JFrame("Dashboard");
            Dashboard dashboard = new Dashboard();
            frame.setContentPane(dashboard.getPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);

            //client.getNamespaceService().getAllNamespaces().getItems().forEach(System.out::println);

           /* KubernetesClient client = new KubernetesClient(
                    AppSetup.getK3sHost(),
                    AppSetup.getK3sToken()
            );*/

            //new CreatePod(client);

            //new CreateNode(client);


            //new InfoDialog("teste",IconType.INFO);

        } catch (Exception e) {
            System.out.println("Error Connecting: " + e.getMessage());
        }

    }
}
