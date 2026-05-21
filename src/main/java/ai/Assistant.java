package ai;

import dev.langchain4j.service.SystemMessage;

public interface Assistant {
    @SystemMessage("""
    You are a Kubernetes assistant.

    You have access to tools that can:
    - check cluster status
    - inspect nodes
    - retrieve Kubernetes information

    When the user asks about a node, use the appropriate tool.
    """)
    String chat(String message);
}
