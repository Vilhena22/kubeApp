package service;

import api.KubernetesClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.PrometheusResponse;
import model.Result;

import java.util.List;

public class ClusterService {
    private final KubernetesClient client;

    public ClusterService(KubernetesClient client) {
        this.client = client;
    }



    public List<Result> getCPUPercentage() throws Exception {
        String json = client.sendRequestGet("sum(rate(container_cpu_usage_seconds_total[5m])) by (pod)");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }

    public double getTotalRam() throws Exception {
        String json = client.sendRequestGet("sum(machine_memory_bytes)");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        String value = (String) response.data.getResult().getFirst().getValue().getLast();
        double bytes = Double.parseDouble(value);
        return (bytes / Math.pow(1024,3));

    }

}
