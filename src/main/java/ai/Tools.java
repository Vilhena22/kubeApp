package ai;

import Handlers.AppSetup;
import api.KubernetesClient;
import dev.langchain4j.agent.tool.Tool;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1PodList;
import model.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Tools {
    private KubernetesClient client;

    public Tools(KubernetesClient client) {
        this.client = client;
    }

    @Tool("Say Hi to user")
    public String sayHi() {
        return "Hello Fried!";

    }


    @Tool("Get cluster status")
    public String getClusterStatus() {
        try {

            double cpu = client.getClusterService().getCPUPercentage();
            double ram = client.getClusterService().getRamPercentage();
            return "Cluster is " + cpu + "% and " + ram + "%";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Tool("Get nodes status")
    public String getNodesStatus() {
        try {
            List<Object[]> nodeNames = new ArrayList<>();
            int up =0;
            int down =0;
            for (Result result : client.getNodeService().getReadyNodes()) {
                String status = result.getValue().getLast().toString();
                if (status.equals("0")){
                    down++;
                }else {
                    up++;
                }
            }
            return "At the moment are " + down + " down and " + up+ "up";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @Tool("""
Get the status of a Kubernetes node by node name.
Use this tool when the user asks about a specific node status.
Example node names: tl2master, worker1, node-01.
Input should only be the node name.
""")
    public String getStatusOfOneNode(String nodeName) {
            try {

                long runningPods = client.getNodeService().getPodsRunOnPod(nodeName);
                String status = "Not Running";
                if (runningPods > 0) {
                    status = "Running";
                }
                return """
                Node Name: %s
                Status: %s
                Running Pods: %d
                """
                        .formatted(
                                nodeName,
                                status,
                                runningPods
                        );

        } catch (Exception e) {

            return "Node " + nodeName + " was not found or is unreachable.";
        }
    }

}
