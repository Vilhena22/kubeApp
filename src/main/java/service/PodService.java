package service;

import Handlers.HttpSendRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;

import io.kubernetes.client.util.Yaml;
import model.PrometheusResponse;
import model.Result;

import java.util.List;

public class PodService {
    private final CoreV1Api api;

    public PodService(CoreV1Api api) {
        this.api = api;
    }

    public V1PodList getAllPodsOnNamespace(String namespace) throws Exception {
        return api.listNamespacedPod(namespace)
                .execute();
    }

    public V1PodList getAllPods() throws Exception {
        return api.listPodForAllNamespaces().execute();
    }


    public V1PodList listPodsByNode(String nodeName) throws Exception {
        return api.listPodForAllNamespaces().fieldSelector("spec.nodeName="+ nodeName).execute();
    }

    public V1Pod getPod(String name, String namespace) throws Exception {
        return api.readNamespacedPod(name, namespace)
                .execute();
    }

    public void deletePod(String name, String namespace) throws Exception {
        api.deleteNamespacedPod(name, namespace)
                .execute();
    }

    public void createPod(String name, String nameSpace, String description, String image, String containerName, String restartPolicy, String pullPolicy) throws ApiException {
        V1Pod pod = new V1Pod()
                .metadata(new V1ObjectMeta()
                        .name(name)
                        .namespace(nameSpace)
                        .putLabelsItem("app", name)
                        .putAnnotationsItem(
                                "description",
                                description
                        )
                )
                .spec(new V1PodSpec()
                        .restartPolicy(restartPolicy)
                        .runtimeClassName("crun")
                        .overhead(null)
                        .containers(List.of(

                                new V1Container()
                                        .name(containerName)
                                        .image(image)
                                        .imagePullPolicy(pullPolicy)
                        )).securityContext(new V1PodSecurityContext()
                                .runAsNonRoot(true)
                                .runAsUser(1000L))
                );

        api.createNamespacedPod(nameSpace, pod).execute();
    }

    public List<Result> getTopFivePodsByCpuUsage() throws Exception {
        String json = HttpSendRequest.sendRequestGet("topk(5, sum(rate(container_cpu_usage_seconds_total[5m])) by (pod))");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }

    public List<Result> getTopFivePodsByMemoryUsage() throws Exception {
        String json = HttpSendRequest.sendRequestGet("topk(5, sum(container_memory_working_set_bytes) by (pod))");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }

    public List<Result> getPodsRunning() throws Exception {
        String json = HttpSendRequest.sendRequestGet("count(kube_pod_status_phase{phase=\"Running\"})");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }

    public List<Result> getPodsNotReady() throws Exception {
        String json = HttpSendRequest.sendRequestGet("sum(kube_pod_status_ready{condition=\"false\"})");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }

    public List<Result> getRestartCountByPods() throws Exception {
        String json = HttpSendRequest.sendRequestGet("sum(kube_pod_container_status_restarts_total) by (pod)");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }


}
