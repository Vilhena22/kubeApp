package service;

import Handlers.HttpSendRequest;
import api.KubernetesClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import model.PrometheusResponse;
import model.Result;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NodeService {
    private final CoreV1Api api;
    private final KubernetesClient client;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    public NodeService(CoreV1Api api, KubernetesClient client) {
        this.api = api;this.client = client;
    }

    public V1NodeList getAllNodes() throws Exception {
        return api.listNode()
                .execute();
    }

    public V1Node getNode(String name) throws Exception {
        V1Node node = api.readNode(name).execute();
        if (Objects.isNull(node)) {
            return null;
        }

        return node;
    }


    public void deleteNode(String name) throws Exception {

        CompletableFuture<V1Status> future = new CompletableFuture<>();
        AtomicInteger attempt = new AtomicInteger(0);
        int maxRetries = 20;

        //Prevent new pods start on this node
        V1Node node = getNode(name);
        if (node == null) {
            return;
        }
        Objects.requireNonNull(node.getSpec()).setUnschedulable(true);
        api.replaceNode(name, node).execute();

        scheduler.scheduleWithFixedDelay(() -> {
            try {
                // 1. Check timeout first
                if (attempt.incrementAndGet() > maxRetries) {
                    future.completeExceptionally(
                            new RuntimeException("Timed out waiting for pods to be evicted from node: " + name)
                    );
                    return;
                }

                // 2. Evict all eligible pods
                V1PodList pods = client.getPodService().listPodsByNode(name);

                // Separate active vs terminating pods
                List<V1Pod> activePods = pods.getItems().stream()
                        .filter(pod -> !isManagedByDaemonSet(pod))
                        .filter(pod -> pod.getMetadata() != null
                                && pod.getMetadata().getDeletionTimestamp() == null) // ← key fix
                        .toList();

                List<V1Pod> allNonDaemonPods = pods.getItems().stream()
                        .filter(pod -> !isManagedByDaemonSet(pod))
                        .toList();

                for (V1Pod pod : activePods) {
                    V1ObjectMeta meta = Objects.requireNonNull(pod.getMetadata());
                    String podName = Objects.requireNonNull(meta.getName());
                    String namespace = Objects.requireNonNull(meta.getNamespace());

                    if (isManagedByDaemonSet(pod)) {
                        System.out.println("Skipping DaemonSet pod: " + podName);
                        continue;
                    }

                    try {
                        V1Eviction eviction = new V1Eviction().metadata(new V1ObjectMeta().name(podName).namespace(namespace));

                        api.createNamespacedPodEviction(podName, namespace, eviction).execute();
                        System.out.println("Evicted pod: " + podName);
                    } catch (ApiException e) {
                        if (e.getCode() != 404) {
                            System.err.println("Failed to evict pod " + podName + ": " + e.getMessage());
                        }
                    }
                }

                // 3. Wait until ALL non-daemonset pods are gone (including terminating ones)
                if (allNonDaemonPods.isEmpty()) {
                    System.out.println("All pods evicted from node: " + name);
                    V1Status status = api.deleteNode(name).execute();
                    future.complete(status);
                } else {
                    System.out.println("Waiting for " + allNonDaemonPods.size() + " pod(s) to finish terminating...");
                }


            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, 0, 5, TimeUnit.SECONDS);

        // Cancel the scheduler once the future is done (success or failure)
        future.whenComplete((result, ex) -> scheduler.shutdown());

    }

    //Verify if the pod belongs to system
    private boolean isManagedByDaemonSet(V1Pod pod) {
        List<V1OwnerReference> owners = Objects.requireNonNull(pod.getMetadata()).getOwnerReferences();
        if (owners == null) return false;
        return owners.stream().anyMatch(o -> "DaemonSet".equals(o.getKind()));
    }

    public List<Result> getReadyNodes() throws Exception {
        String json = HttpSendRequest.sendRequestGet("""
                kube_node_info
                * on(node) group_left()
                kube_node_status_condition{condition="Ready", status="true"}""");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }

    public long getPodsRunOnPod(String nodeName) throws Exception {
        V1PodList podList = client.getPodService().listPodsByNode(nodeName);
        return podList.getItems().stream()
                .filter(pod -> pod.getSpec() != null
                        && Objects.requireNonNull(nodeName).equals(pod.getSpec().getNodeName())
                        && pod.getStatus() != null
                        && "Running".equals(pod.getStatus().getPhase()))
                .count();
    }


    public List<Result> getFreeStorage() throws Exception {
        String json = HttpSendRequest.sendRequestGet("kube_node_status_condition{condition=\"Ready\",status=\"true\"}");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }


    public List<Result> getRamUsageByNode() throws Exception {
        String json = HttpSendRequest.sendRequestGet("kube_node_status_condition{condition=\"Ready\",status=\"true\"}");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }

    public List<Result> getCPUAvgByNode() throws Exception {
        String json = HttpSendRequest.sendRequestGet("sum(rate(container_cpu_usage_seconds_total[5m])) by (node)");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }

    public List<Result> getRamByNode() throws Exception {
        String json = HttpSendRequest.sendRequestGet("sum(container_memory_usage_bytes) by (node)");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }

    public List<Result> getNetworkByNode() throws Exception {
        String json = HttpSendRequest.sendRequestGet("sum(rate(container_network_receive_bytes_total[5m])) by (node)");
        ObjectMapper mapper = new ObjectMapper();
        PrometheusResponse response = mapper.readValue(json, PrometheusResponse.class);
        return response.data.getResult();

    }

}
