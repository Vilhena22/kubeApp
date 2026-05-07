import api.KubernetesClient;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1Pod;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        try {
            KubernetesClient client = new KubernetesClient(
                    AppSetup.getK3sHost(),
                    AppSetup.getK3sToken()
            );

            List<V1Pod> pods = client.getPodService().listPods("default");

            pods.forEach(pod ->
                    System.out.println("Pod: " + Objects.requireNonNull(pod.getMetadata()).getName())
            );

            System.out.println(client.getNodeService().getNode("tl2node1"));


            /*List<V1Node> nodes = client.getNodeService().listNodes();

            nodes.forEach(node ->
                    System.out.println("Node: " + Objects.requireNonNull(node.getMetadata()).getName())
            );*/

        } catch (Exception e) {
            System.out.println("Error Connecting: " + e.getMessage());
        }

    }
}
