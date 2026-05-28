
import Handlers.AppSetup;
import Handlers.ClusterDAO;
import ai.AiFactory;
import ai.Assistant;
import api.KubernetesClient;
import com.formdev.flatlaf.FlatDarkLaf;
import model.IconType;
import ui.*;
import io.kubernetes.client.openapi.models.*;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;

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


            /*KubernetesClient client = new KubernetesClient(
                    AppSetup.getK3sHost(),
                    AppSetup.getK3sToken()
            );*/
            //client.getPodService().getAllPodsOnNamespace("").getItems().forEach(System.out::println);

            //client.getServiceManager().getAllServicesOnNamespace("").getItems().forEach(System.out::println);
            //new CreatePod(client);

            //new CreateNode(client);
            //new CreateService(client);

            //new InfoDialog("teste",IconType.INFO);

        } catch (Exception e) {
            System.out.println("Error Connecting: " + e.getMessage());
        }

    }
}
