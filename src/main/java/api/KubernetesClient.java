package api;


import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;

import service.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class KubernetesClient {

    private final ApiClient apiClient;
    private final CoreV1Api coreV1Api;
    private final AppsV1Api appsV1Api;
    private final String metricURL;

    private PodService podService;
    private DeploymentService deploymentService;
    private NamespaceService namespaceService;
    private NodeService nodeService;
    private ServiceManager serviceManager;
    private ClusterService clusterService;


    public KubernetesClient(String host, String token, String metricsURL) {
        this.apiClient = Config.fromToken(host, token, false);
        this.metricURL = metricsURL;
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

    public KubernetesClient getKubernetesClient() {
        return this;
    }


    public NodeService getNodeService() {
        if  (nodeService == null) nodeService = new NodeService(getCoreApi(),getKubernetesClient());
        return nodeService;
    }

    public PodService getPodService() {
        if (podService == null) podService = new PodService(getCoreApi(),getKubernetesClient());
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

    public ClusterService getClusterService() {
        if (clusterService == null) clusterService = new ClusterService(getKubernetesClient());
        return clusterService;
    }

    public String sendRequestGet(String query) throws Exception {
        HttpURLConnection conn = getHttpsURLConnection(query);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() != 200) {
            return conn.getResponseMessage();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();

        return sb.toString();
    }

    private HttpURLConnection getHttpsURLConnection(String query) throws IOException {

        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);

        URL urlObj = new URL(metricURL+ encoded);
        System.out.println(urlObj);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + "eyJhbGciOiJSUzI1NiIsImtpZCI6ImpZdUp1bUdkMGxGa0tPLTZvQmw1UGVXVi1LMUk0d3JJd1FmamdBREpJUTAifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRldmVsb3Blci1hY2MtdG9rZW4iLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGV2ZWxvcGVyLWFjYyIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6ImMwNjRhYTZkLTBiNTEtNDQ4Zi1iZDhlLTk4ZDkxYWQxMjRhMCIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRldmVsb3Blci1hY2MifQ.luN3rWl9DDrBzGW4UICt7R_H45pgSc7e6WnEjx4gg0KB5c2Qi_oOoiRwWf0NBKTqvlNxuG_Npv3HIGxa_hZ6np2DDw3vpyM1kDpbIWMOWX3e3nPyut3ufy3B9GTor3b-g9kkxkVacZUk3bx9ZPTwBaj5s3T1E3h44iGrh_FzfZ357Z0IgDUDQHVS7qBl500ip9l44xVyvctgGhuOxyPoH0HLx6iPPK-IqTKV6ITStiWFXqLRg-TKIto5tya09R5-SWoSakjbCiMOnb26f2qBf5yNIVxqhWFbV0fXn55tW1EFzDyoYDuHL2KiHV8uv2glo3x-HO8jBQN2PTLhIDCCjg");

        return conn;
    }



}
