package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Metric {
    // do campo "metric"
    public String pod;
    public String instance;
    public String job;
    public String namespace;
    public String container;
    public String node;

    // para saber o tipo de métrica (cpu, memory, network)
    @JsonProperty("__name__")
    public String metricName;

    public String getPod() {
        return pod;
    }

    public String getJob() {
        return job;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getContainer() {
        return container;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getNode() {
        return node;
    }
}