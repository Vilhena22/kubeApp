package service;

import Handlers.HttpSendRequest;
import api.KubernetesClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import model.PrometheusResponse;
import model.Result;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ClusterService {
    private final KubernetesClient client;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    public ClusterService(KubernetesClient client) {
        this.client = client;
    }

    public V1NodeList getAllMastersNodes() throws Exception {
        V1NodeList masters = new V1NodeList();
        for (V1Node node : client.getNodeService().getAllNodes().getItems()) {
            Map<String, String> labels = node.getMetadata().getLabels();

            if (labels != null &&
                    (labels.containsKey("node-role.kubernetes.io/control-plane") ||
                            labels.containsKey("node-role.kubernetes.io/master"))) {

                masters.addItemsItem(node);
            }
        }
        return masters;
    }

    public double getCPUPercentage() throws Exception {
        String json = HttpSendRequest.sendRequestGet("100 - (avg by (instance) (irate(node_cpu_seconds_total{mode=\"idle\"}[5m])) * 100)");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        Result result = response.data.getResult().getLast();
        double value = Double.parseDouble(result.getValue().getLast().toString());

        return value * 100;

    }

    public String getTotalRam() throws Exception {
        String json = HttpSendRequest.sendRequestGet("kube_node_status_capacity{resource='memory', node='tl2master'}");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        String value = (String) response.data.getResult().getFirst().getValue().getLast();
        double bytes = Double.parseDouble(value);
        double gb = bytes / Math.pow(1024, 3);

        if (gb >= 1024) {
            return String.format("%.2f TB", gb / 1024);
        } else if (gb >= 1) {
            return String.format("%.2f GB", gb);
        } else {
            double mb = bytes / Math.pow(1024, 2);
            return String.format("%.2f MB", mb);
        }

    }

    public int getTotalCpuCores() throws Exception {
        String json = HttpSendRequest.sendRequestGet("kube_node_status_capacity{resource='cpu', node='tl2master'}");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        String value = (String) response.data.getResult().getFirst().getValue().getLast();
        return Integer.parseInt(value);
    }

    public double getRamPercentage() throws Exception {
        String json = HttpSendRequest.sendRequestGet("sum(container_memory_working_set_bytes{container!=\"\"}) / sum(machine_memory_bytes) * 100");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        Result result = response.data.getResult().getLast();
        return Double.parseDouble(result.getValue().getLast().toString());
    }

    public List<Result> getClusterSpecs() throws Exception {
        String json = HttpSendRequest.sendRequestGet("kube_node_info{node='tl2master'}");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();
    }

}
