package com.example.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import org.codice.itest.api.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;


public class BookmarkApiIntegrationTest implements IntegrationTest {

  private final HttpClient client = HttpClient.newHttpClient();
  private final String composeFile = "docker-compose.yml";
  private final String serviceUrl = "http://localhost:8080";

  @Override
  public String getName() {
    return "BookmarkTreeApiIntegrationTest";
  }

  @Override
  public void setup() {
    DockerIntegrationTools.runCommand("docker", "compose", "-f", composeFile, "up", "-d");
    DockerIntegrationTools.waitUntilHealthy(serviceUrl + "/actuator/health", 30);
  }

  @Override
  public void test() throws IOException, InterruptedException {
    String userId = "testuser";

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(serviceUrl + "/users/" + userId + "/drive"))
            .timeout(Duration.ofSeconds(10))
            .header("Accept", "application/json")
            .GET()
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("\"uid\":\"my-folder\"");
  }

  @Override
  public void cleanup() {
    DockerIntegrationTools.runCommand("docker", "compose", "-f", composeFile, "down");
  }
}
