import api.KubernetesClient;
import model.Result;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            KubernetesClient client = new KubernetesClient(
                    AppSetup.getK3sHost(),
                    AppSetup.getK3sToken(),
                    AppSetup.getPrometheusUrl()
            );


            //client.getNodeService().deleteNode("tl2node3");

             List<Result> results= client.getPodService().getCPUAvgByNode();
             for (Result result : results) {
                 System.out.println("Pod: " + result.getMetric().getPod());
                 System.out.println("CPU: " + result.getValue().getLast());
             }

            System.out.println("Total Ram: " + client.getClusterService().getTotalRam());

        } catch (Exception e) {
            System.out.println("Error Connecting: " + e.getMessage());
        }

    }
}
