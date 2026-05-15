package service;

import Handlers.HttpSendRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.PrometheusResponse;
import model.Result;

import java.util.List;

public class ClusterService {

    public List<Result> getCPUPercentage() throws Exception {
        String json = HttpSendRequest.sendRequestGet("sum(rate(container_cpu_usage_seconds_total[5m]))/sum(machine_cpu_cores)");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

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

    public List<Result> getRamPercentage() throws Exception {
        String json = HttpSendRequest.sendRequestGet("sum(container_memory_working_set_bytes{container!=\"\"}) / sum(machine_memory_bytes) * 100");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }

    public List<Result> getClusterSpecs() throws Exception {
        String json = HttpSendRequest.sendRequestGet("kube_node_info{node='tl2master'}");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();
    }

}
