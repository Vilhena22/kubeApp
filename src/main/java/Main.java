import api.KubernetesClient;
import io.kubernetes.client.openapi.models.V1Pod;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        try {
            KubernetesClient client = new KubernetesClient(
                    AppSetup.getK3sHost(),
                    AppSetup.getK3sToken()
            );
            System.out.println(client.getApiClient());
            System.out.println( client.getCoreApi());
            List<V1Pod> pods = client.getPodService().listPods("default");

            pods.forEach(pod ->
                    System.out.println("Pod: " + Objects.requireNonNull(pod.getMetadata()).getName())
            );

        } catch (Exception e) {
            System.out.println("Error Connecting: " + e.getMessage());
        }

    }
}
