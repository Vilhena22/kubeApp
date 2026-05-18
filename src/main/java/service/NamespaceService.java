package service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;

public class NamespaceService extends V1Namespace {

    private final CoreV1Api nm = new CoreV1Api();

    public void getNamespace(String namespace) throws ApiException {
        nm.readNamespace(namespace).execute();
    }

    public V1NamespaceList getAllNamespaces() throws ApiException {
        return nm.listNamespace().execute();
    }

    public void createNamespace(String name) throws ApiException {
        nm.createNamespace(new V1Namespace().apiVersion("v1")
                .kind("Namespace").metadata(new V1ObjectMeta()
                        .name(name)
                        .putLabelsItem("name", name))).execute();
    }

    public void deleteNamespace(String namespace) throws ApiException {
        nm.deleteNamespace(namespace).execute();
    }
}
