package com.example.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.codice.itest.api.IntegrationTest;

class ApiTest implements IntegrationTest {
  private static final HttpClient client = HttpClient.newHttpClient();
  private static final String getUrl = "http://localhost:8080/users/jimmy/vfs";
  private static final String putUrl = "http://localhost:8080/users/jimmy/vfs";

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
    putInitialTree_overwritesAnyPriorState();
    //    getTree_returnsMyFolder_andSavesEtag();
    //    putTree_withBadEtag_returns409();
    //    putTree_withGoodEtag_updatesSuccessfully_andChangesEtag();
    //    putInvalidTree_returns400();
  }

  private void putInitialTree_overwritesAnyPriorState() throws Exception {
    String body = validRootTreeJson();

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(putUrl))
            .timeout(Duration.ofSeconds(10))
            .header("Content-Type", "application/json")
            .header("If-Match", "\"initial\"")
            .PUT(HttpRequest.BodyPublishers.ofString(body))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(204);
  }
}
