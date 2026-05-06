package model;

public class MetricResult {
    // do campo "metric"
    private String pod;
    private String instance;
    private String job;
    private String namespace;
    private String container;

    // do campo "value"
    private long timestamp;
    private double value;

    // para saber o tipo de métrica (cpu, memory, network)
    private String metricName;
}