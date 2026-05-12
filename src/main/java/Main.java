import Handlers.AppSetup;
import api.KubernetesClient;
import model.Result;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            KubernetesClient client = new KubernetesClient(
                    AppSetup.getK3sHost(),
                    AppSetup.getK3sToken()
            );


             List<Result> results= client.getNodeService().getReadyNodes();
             for (Result result : results) {
                 System.out.println("Node: " + result.getMetric().getNode());
                 System.out.println("Status: " + result.getValue().getLast());
             }


        } catch (Exception e) {
            System.out.println("Error Connecting: " + e.getMessage());
        }

    }
}
