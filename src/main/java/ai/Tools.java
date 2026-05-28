package ai;


import api.KubernetesClient;
import dev.langchain4j.agent.tool.Tool;
import io.kubernetes.client.openapi.models.V1Pod;

import io.kubernetes.client.openapi.models.V1PodList;
import model.Result;

public class Tools {
    private final KubernetesClient client;

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

    @Tool("""
Get the status of a Kubernetes nodes.
Use this tool when the user asks about nodes status in general.
Example node names: tl2master, worker1, node-01.
Input should only be the node name.
""")
    public String getNodesStatus() {
        try {
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


    @Tool("""
List all pods.
Use this tool when the user asks for list all pods or when you need to check all existent pods
and the status and other information.
""")
    public String listPods() {
        try {

            V1PodList podsList = client.getPodService().getAllPods();

            return "This is a list of  all pods " + podsList.getItems();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Tool("""
Get the status of a Kubernetes pods.
Use this tool when the user asks about pods status.
""")
    public String getPodsStatus() {
        try {
            int up =0;
            int down =0;
            for (V1Pod pod : client.getPodService().getAllPods().getItems()) {
                String status = pod.getStatus() != null ? pod.getStatus().getPhase() : null;
                if (status != null && (status.equals("Failed") || status.equals("Unknown"))) {
                    down++;
                }else{
                    up++;
                }
            }
            return "At the moment are " + down + " pods down and " + up+ "up";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Tool("""
Restart pods that are down.
Use this tool when the user asks for restart or fix the pods down.
""")
    public String restartPods() {
        try {
            for (V1Pod pod : client.getPodService().getAllPods().getItems()) {
                String status = pod.getStatus() != null ? pod.getStatus().getPhase() : null;
                if (status != null && (status.equals("Failed") || status.equals("Unknown"))) {
                    client.getPodService().deletePod(pod.getMetadata() != null ? pod.getMetadata().getName() : null, pod.getMetadata().getNamespace());
                }
            }
            return "All pods have been restarted.";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
