package service;

import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodSpec;

import io.kubernetes.client.openapi.JSON;


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

    private V1Pod createPod(V1Pod pod, String namespace) throws Exception {
        return api.createNamespacedPod(namespace, pod)
                .execute();
    }

    public void deletePod(String name, String namespace) throws Exception {
        api.deleteNamespacedPod(name, namespace)
                .execute();
    }

    public V1Pod createPod(String name, String namespace, String image, String containerName) throws Exception {
        V1Pod pod = new V1Pod()
                .metadata(new V1ObjectMeta()
                        .name(name)
                        .namespace(namespace))
                .spec(new V1PodSpec()
                        .runtimeClassName("crun")
                        .overhead(null)
                        .containers(List.of(
                                new V1Container()
                                        .name(containerName)
                                        .image(image)
                        )));

        System.out.println(JSON.serialize(pod));

        return createPod(pod, namespace);
    }

}
