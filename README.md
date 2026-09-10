# KubeApp — Kubernetes/K3s Cluster Management Application

A Java desktop application that provides a graphical, unified interface for managing a **Kubernetes/K3s cluster**, communicating directly with the **Kubernetes API** through the official Java client. It centralizes operations that would otherwise require the `kubectl` CLI, and includes an integrated **AI assistant** for querying cluster status in natural language.

Developed as part of the *Information Technology Laboratory* course (TL2), Computer Engineering degree — Polytechnic Institute of Leiria.

## Features

- **Cluster connection** via API server host and Bearer token authentication.
- **Dashboard** with real-time cluster metrics (CPU/RAM usage) pulled from **Prometheus**, displayed with gauges and charts.
- **Nodes**: listing, status inspection (ready/not ready), and creation.
- **Namespaces**: listing, creation, and deletion.
- **Pods**: listing, creation, and deletion.
- **Deployments**: listing, creation, and deletion.
- **Services**: listing and creation, with dynamic input fields depending on the service type (ClusterIP, NodePort, LoadBalancer, ExternalName).
- **Error handling**: clear inline validation and error messages for invalid fields or failed operations.
- **AI Assistant (chatbot)**: built with LangChain4j and a local LLM (via Ollama), able to answer questions about cluster and node status using custom tools that query the live cluster.
- **Local history**: previously used clusters are stored locally (SQLite) for quick reconnection.
- **Light/dark theme** via FlatLaf.

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Build | Maven |
| GUI | Java Swing + JavaFX (mixed) + [FlatLaf](https://www.formdev.com/flatlaf/) |
| Kubernetes access | [Official Kubernetes Java Client](https://github.com/kubernetes-client/java) (`io.kubernetes:client-java`) |
| Metrics | Prometheus (PromQL queries) |
| Charts / Gauges | JFreeChart, Medusa (hansolo) |
| AI / Chatbot | [LangChain4j](https://github.com/langchain4j/langchain4j) + Ollama (local LLM, `qwen2.5:3b`) |
| JSON serialization | Jackson |
| Local persistence | SQLite (JDBC) |
| Logging | SLF4J |

## Architecture

- **`api.KubernetesClient`** — wraps the official Kubernetes Java client, configured with the cluster host and Bearer token, exposing `CoreV1Api` and `AppsV1Api`.
- **`service`** — one service class per resource type (`NodeService`, `PodService`, `NamespaceService`, `DeploymentService`, `ServiceManager`, `ClusterService`), encapsulating the Kubernetes API calls and, for `ClusterService`, the Prometheus queries used for CPU/RAM metrics.
- **`ui`** — Swing screens: the main `Dashboard` and the creation/deletion dialogs for each resource type (`CreateNode`, `CreatePod`, `CreateDeployment`, `CreateNamespace`, `CreateService`, `DeleteDialog`, `InfoDialog`).
- **`ai`** — the chatbot layer: `AiFactory` builds the LangChain4j `Assistant` backed by a local Ollama model, and `Tools` exposes cluster-querying functions (e.g. cluster status, node status) that the model can call.
- **`Handlers`** — cross-cutting utilities: local database access (`ClusterDAO`, `Database`), HTTP requests to Prometheus (`HttpSendRequest`), form field validation (`FieldValidator`, `FieldType`), and application setup (`AppSetup`).
- **`model`** — data classes for Prometheus responses (`PrometheusResponse`, `Data`, `Metric`, `Result`) and UI-related types (`History`, `IconType`).

## Prerequisites

- Java 21+
- Maven
- A Kubernetes/K3s cluster reachable over the network, with API server access and a valid Bearer token
- A Prometheus instance scraping the cluster (for dashboard metrics)
- [Ollama](https://ollama.com/) running locally with the `qwen2.5:3b` model pulled, for the AI assistant feature

```bash
ollama pull qwen2.5:3b
ollama serve
```

## Running the project

```bash
git clone https://github.com/Vilhena22/kubeApp.git
cd kubeApp
mvn clean install
mvn exec:java -Dexec.mainClass="Main"
```

Alternatively, run the `Main` class directly from an IDE (IntelliJ IDEA). On first launch, connect to your cluster using its API host and Bearer token; the connection is stored locally in SQLite for future sessions.

## Project structure

```
kubeApp/
├── src/main/java/
│   ├── Main.java              # Application entry point
│   ├── api/                   # Kubernetes API client wrapper
│   ├── service/                # Resource-specific service classes + cluster metrics
│   ├── ui/                     # Dashboard and resource dialogs (Swing)
│   ├── ai/                     # LangChain4j-based chatbot assistant and tools
│   ├── Handlers/                # DB, HTTP, validation and setup utilities
│   └── model/                   # Data models (Prometheus, history, icons)
└── pom.xml
```

## Authors

- Francisco Vilhena — [@Vilhena22](https://github.com/Vilhena22)
- Pedro Gomes - [@PedroGomesReis](https://github.com/PedroGomesReis)

## Academic context

Laboratory Assignment No. 2 (TL2) — Information Technology Laboratory, EI 2025/26, Polytechnic Institute of Leiria.
