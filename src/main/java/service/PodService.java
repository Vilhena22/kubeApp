package service;

import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;

import java.util.List;

public class PodService {
    private final CoreV1Api api;

    public PodService(CoreV1Api client) {
        this.api = client;
    }

    public List<V1Pod> listPods(String namespace) throws Exception {
        return api.listNamespacedPod(namespace)
                .execute()
                .getItems();
    }

    public V1Pod getPod(String name, String namespace) throws Exception {
        return api.readNamespacedPod(name, namespace)
                .execute();
    }

    public V1Pod createPod(V1Pod pod, String namespace) throws Exception {
        return api.createNamespacedPod(namespace, pod)
                .execute();
    }

    public void deletePod(String name, String namespace) throws Exception {
        api.deleteNamespacedPod(name, namespace)
                .execute();
    }
}
