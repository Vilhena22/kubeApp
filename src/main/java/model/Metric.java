package model;

public class Metric {
    // do campo "metric"
    public String pod;
    public String instance;
    public String job;
    public String namespace;
    public String container;

    // para saber o tipo de métrica (cpu, memory, network)
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
}