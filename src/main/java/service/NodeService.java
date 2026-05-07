package service;

import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;

import java.util.List;

public class NodeService {
    private final CoreV1Api api;

    public NodeService(CoreV1Api client) {
        this.api = client;
    }

    public List<V1Node> listNodes() throws Exception {
        return api.listNode()
                .execute()
                .getItems();
    }

    public V1Node getNode(String name) throws Exception {
        return api.readNode(name).execute();
    }


}
