package service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;

public class NamespaceService extends V1Namespace {

    private CoreV1Api nm = new CoreV1Api();

    public V1Namespace getNamespace(String namespace) throws ApiException {
        return nm.readNamespace(namespace).execute();
    }

    public V1NamespaceList getAllNamespaces() throws ApiException {
        return nm.listNamespace().execute();
    }

    public void createNamespace(V1Namespace nome) throws ApiException {
        nm.createNamespace(nome).execute();
    }

    public void deleteNamespace(String namespace) throws ApiException {
        if (namespace == null || namespace.isBlank()) {
            throw new IllegalArgumentException("Namespace must not be null or empty");
        }
        nm.deleteNamespace(namespace).execute();
    }
}
