package com.function;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.microsoft.azure.eventgrid.EventGridClient;
import com.microsoft.azure.eventgrid.TopicCredentials;
import com.microsoft.azure.eventgrid.implementation.EventGridClientImpl;
import com.microsoft.azure.eventgrid.models.EventGridEvent;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.model.EventData;

import org.joda.time.DateTime;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("EventGridPublisher")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        
        TopicCredentials topicCredentials = new TopicCredentials("<EVENT GRID TOPIC SAS KEY>");
        EventGridClient client = new EventGridClientImpl(topicCredentials);
        context.getLogger().info("Publishing events to Event Grid Topic");
        List<EventGridEvent> eventsList = new ArrayList<>();
        eventsList.add(new EventGridEvent(UUID.randomUUID().toString(), "Event", new EventData("Hello World"),
        "Hello World Event",
        DateTime.now(),
        "2.0"));
        String eventGridEndpoint;
        try {
            eventGridEndpoint = String.format("https://%s/", new URI("<EVENT GRID TOPIC URL>").getHost());
            client.publishEvents(eventGridEndpoint, eventsList);
            context.getLogger().info("Published to Topic Successfully");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return request.createResponseBuilder(HttpStatus.OK).body("Event Published successfully to Event Grid Topic").build();

    }
}
