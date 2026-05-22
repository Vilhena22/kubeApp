package service;


import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
public class ServiceManager {
    private final CoreV1Api client;

    public ServiceManager(CoreV1Api client) {
        this.client = client;

    }

    public V1ServiceList getAllServices() throws ApiException {
        return client.listServiceForAllNamespaces().execute();
    }

    public V1ServiceList getAllServicesOnNamespace(String namespace) throws ApiException {
        return client.listNamespacedService(namespace).execute();
    }

    public V1Service getService(String namespace, String serviceName) throws ApiException {
        return client.readNamespacedService(namespace, serviceName).execute();
    }

    public V1Service createService(String namespace, V1Service service) throws ApiException {
        return client.createNamespacedService(namespace, service
                .apiVersion("v1")
                .kind("Service")
                .metadata(service.getMetadata())
                .spec(service.getSpec())
        ).execute();
    }

    public void deleteService(String namespace, String serviceName) throws ApiException {
        client.deleteNamespacedService(serviceName,namespace).execute();
    }
}
