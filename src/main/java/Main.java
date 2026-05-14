import Handlers.AppSetup;
import api.KubernetesClient;
import io.kubernetes.client.openapi.models.*;
import service.DeploymentService;

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

//            List<V1Pod> pods = client.getPodService().listPods("default");
//
//            pods.forEach(pod ->
//                    System.out.println("Pod: " + Objects.requireNonNull(pod.getMetadata()).getName())
//            );

            //System.out.println(client.getNodeService().getNode("tl2node1"));


            /*List<V1Node> nodes = client.getNodeService().listNodes();

            nodes.forEach(node ->
                    System.out.println("Node: " + Objects.requireNonNull(node.getMetadata()).getName())
            );*/

//            V1DeploymentList deps = client.getDeploymentService().getAllDeployments();
//
//            for (V1Deployment dep : deps.getItems()){
//                System.out.println(dep.getMetadata().getName());
//            }

            V1NamespaceList nm = client.getNamespaceService().getAllNamespaces();

            for (V1Namespace namespace : nm.getItems()){
                V1ObjectMeta metadata = namespace.getMetadata();
                System.out.println(metadata.getName());
            }

        } catch (Exception e) {
            System.out.println("Error Connecting: " + e.getMessage());
        }

    }
}
