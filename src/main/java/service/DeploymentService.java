package service;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;

public class DeploymentService {

    private final AppsV1Api dep = new AppsV1Api();

    // Listar todos os deployments
    public V1DeploymentList getAllDeployments() throws ApiException {
        return dep.listDeploymentForAllNamespaces().execute();
    }

    // Listar deployments de um namespace específico
    public V1DeploymentList getDeploymentsByNamespace(String namespace) throws ApiException {
        return dep.listNamespacedDeployment(namespace).execute();
    }

    //Devolve o deployment
    public V1Deployment getDeploymentsByName(String namespace,String deploymentName) throws ApiException {
        return dep.readNamespacedDeployment(deploymentName,namespace).execute();
    }

    public void createDeployment(String namespace, String deploymentName, String labelApp, String containerName, String image) throws ApiException {

        V1Deployment newDeployment = new V1Deployment()
                .apiVersion("apps/v1")
                .kind("Deployment")
                .metadata(
                        new V1ObjectMeta()
                                .name(deploymentName)
                )
                .spec(
                        new V1DeploymentSpec()
                                .selector(
                                        new V1LabelSelector()
                                                .putMatchLabelsItem("app", labelApp)
                                )
                                .template(
                                        new V1PodTemplateSpec()
                                                .metadata(
                                                        new V1ObjectMeta()
                                                                .putLabelsItem("app", labelApp)
                                                )
                                                .spec(
                                                        new V1PodSpec()
                                                                .addContainersItem(
                                                                        new V1Container()
                                                                                .name(containerName)
                                                                                .image(image)
                                                                )
                                                )
                                )
                );

        dep.createNamespacedDeployment(namespace, newDeployment).execute();
    }


    // Eliminar deployment
    public void deleteDeployment(String name, String namespace) throws ApiException {
        dep.deleteNamespacedDeployment(name, namespace).execute();
    }
}