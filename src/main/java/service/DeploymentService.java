package service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;

public class DeploymentService {

    private AppsV1Api dep = new AppsV1Api();

    // Listar todos os deployments
    public V1DeploymentList getAllDeployments() throws ApiException {
        return dep.listDeploymentForAllNamespaces().execute();
    }

    // Listar deployments de um namespace específico
    public V1DeploymentList getDeploymentsByNamespace(String namespace) throws ApiException {
        return dep.listNamespacedDeployment(namespace).execute();
    }

    // Criar deployment
    public V1Deployment createDeployment(String namespace, String name) throws ApiException {

        V1Deployment newDeployment = new V1Deployment()
                .apiVersion("apps/v1")
                .kind("Deployment")
                .metadata(
                        new V1ObjectMeta()
                                .name(name)
                )
                .spec(
                        new V1DeploymentSpec()
                                .replicas(3)
                                .selector(
                                        new V1LabelSelector()
                                                .putMatchLabelsItem("app", "nginx")
                                )
                                .template(
                                        new V1PodTemplateSpec()
                                                .metadata(
                                                        new V1ObjectMeta()
                                                                .putLabelsItem("app", "nginx")
                                                )
                                                .spec(
                                                        new V1PodSpec()
                                                                .addContainersItem(
                                                                        new V1Container()
                                                                                .name("nginx")
                                                                                .image("nginx:latest")
                                                                )
                                                )
                                )
                );

        return dep.createNamespacedDeployment(
                namespace,
                newDeployment
        ).execute();
    }

    // Eliminar deployment
    public void deleteDeployment(String name, String namespace) throws ApiException {
        dep.deleteNamespacedDeployment(name, namespace).execute();
    }
}