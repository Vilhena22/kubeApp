package ai;

import api.KubernetesClient;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

public class AiFactory {
    public static Assistant createAssistant(Tools tools) {

        var model =
                OllamaChatModel.builder()
                        .baseUrl("http://localhost:11434")
                        .modelName("qwen2.5:7b")
                        .build();

        return AiServices.builder(Assistant.class)
                .chatModel(model)
                .tools(tools)
                .chatMemory(
                        MessageWindowChatMemory.withMaxMessages(20)
                )
                .build();



    }
}
