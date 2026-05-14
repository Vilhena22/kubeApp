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

    public double getTotalRam() throws Exception {
        String json = HttpSendRequest.sendRequestGet("sum(machine_memory_bytes)");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        String value = (String) response.data.getResult().getFirst().getValue().getLast();
        double bytes = Double.parseDouble(value);
        return (bytes / Math.pow(1024,3));

    }

    public List<Result> getRamPercentage() throws Exception {
        String json = HttpSendRequest.sendRequestGet("sum(container_memory_working_set_bytes{container!=\"\"}) / sum(machine_memory_bytes) * 100");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }

}
