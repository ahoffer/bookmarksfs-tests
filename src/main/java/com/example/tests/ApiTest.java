package com.example.tests;

import static org.assertj.core.api.Assertions.assertThat;
import org.codice.itest.api.IntegrationTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

class ApiTest implements IntegrationTest {
    static final String createUserUrl = "http://localhost:8080/drive/jimmy";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String getUrl = "http://localhost:8080/drive/jimmy";
    private static final String putUrl = "http://localhost:8080/drive";
    Duration timeout = Duration.ofSeconds(100000);

    private static String validRootTreeJson() {
        return """
                   {
                  "type" : "root",
                  "id" : "root",
                  "name" : "Root",
                  "contents" : [ {
                    "type" : "folder",
                    "id" : "60396fcc-a122-428d-9d59-c943a87266e2",
                    "name" : "my-folder",
                    "contents" : [ ]
                  }, {
                    "type" : "folder",
                    "id" : "f9e29f46-44d5-4557-aee4-4edf0f9c5750",
                    "name" : "inbox",
                    "contents" : [ ]
                  }, {
                    "type" : "folder",
                    "id" : "8fd01b37-bfc8-4c54-ad8e-81eecae444a3",
                    "name" : "trash",
                    "contents" : [ ]
                  } ]
                }
                """;
    }

    @Override
    public void cleanup() {
        DockerIntegrationTools.runCommand("docker", "compose", "down");
        DockerIntegrationTools.runCommand("docker", "volume", "rm", "bookmarks-data");
    }

    @Override
    public void setup() {
        DockerIntegrationTools.runCommand("docker", "compose", "up", "-d");
        DockerIntegrationTools.waitUntilHealthy("http://localhost:8080/actuator/health", 30);
    }

    @Override
    public void test() throws Exception {
        getNonexistent();
        HttpResponse<String> response = createUser();
        userAlreadyExists();
        putSimplestValid(response.body(), response.headers());
        putNoBody();
        // TODO
        putSubtlyWrongJson();
//    putInitialTree_overwritesAnyPriorState();
        //    getTree_returnsMyFolder_andSavesEtag();
        //    putTree_withBadEtag_returns409();
        //    putTree_withGoodEtag_updatesSuccessfully_andChangesEtag();
        //    putInvalidTree_returns400();
    }

    HttpResponse<String> createUser() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(createUserUrl)).timeout(timeout).POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(201);
        return response;
    }

    void putNoBody() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(putUrl)).timeout(timeout).header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(400);
    }

    private HttpResponse<String> getNonexistent() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(getUrl)).timeout(timeout).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(404);
        return response;
    }

    private void putInitialTree_overwritesAnyPriorState() throws Exception {
        String body = validRootTreeJson();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(putUrl)).timeout(timeout).header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(204);
    }

    private HttpResponse<String> putSimplestValid(String requestBody, HttpHeaders headers) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(putUrl)).timeout(timeout).header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        return response;
    }

    private void putSubtlyWrongJson() {
    }

    private HttpResponse<String> userAlreadyExists() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(createUserUrl)).timeout(timeout).POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(409);
        return response;
    }
}
