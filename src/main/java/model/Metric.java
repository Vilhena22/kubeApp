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
    public String internal_ip;

    // para saber o tipo de métrica (cpu, memory, network)
    @JsonProperty("__name__")
    public String metricName;
    @JsonProperty("kernel_version")
    public String kernelVersion;
    @JsonProperty("kubelet_version")
    public String kubeletVersion;
    @JsonProperty("os_image")
    public String osImage;


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

    public String getInternal_ip() {return internal_ip;}

    public String getInstance() {
        return instance;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public String getKubeletVersion() {
        return kubeletVersion;
    }

    public String getOsImage() {
        return osImage;
    }
}