package api;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import service.*;

public class KubernetesClient {

    private final ApiClient apiClient;
    private final CoreV1Api coreV1Api;
    private final AppsV1Api appsV1Api;

    private PodService podService;
    private DeploymentService deploymentService;
    private NamespaceService namespaceService;
    private NodeService nodeService;
    private ServiceManager serviceManager;

    public KubernetesClient(String host, String token) {
        this.apiClient = Config.fromToken(host, token, false);

        //this.apiClient.setLenientOnJson(true); //Se aparecer campos novos ativar isto que faz o ignore
        Configuration.setDefaultApiClient(apiClient);
        this.coreV1Api = new CoreV1Api();
        this.appsV1Api = new AppsV1Api();
    }


    public CoreV1Api getCoreApi() {
        return coreV1Api;
    }

    public AppsV1Api getAppsApi() {
        return appsV1Api;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }


    public NodeService getNodeService() {
        if  (nodeService == null) nodeService = new NodeService(getCoreApi());
        return nodeService;
    }

    public PodService getPodService() {
        if (podService == null) podService = new PodService(getCoreApi());
        return podService;
    }

    public DeploymentService getDeploymentService() {
        if (deploymentService == null) deploymentService = new DeploymentService();
        return deploymentService;
    }

    public NamespaceService getNamespaceService() {
        if (namespaceService == null) namespaceService = new NamespaceService();
        return namespaceService;
    }

}
