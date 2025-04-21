package com.example.tests;

import static java.lang.ProcessBuilder.Redirect.INHERIT;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class DockerIntegrationTools {

  private static final HttpClient client = HttpClient.newHttpClient();

  public static void runCommand(String... command) {
    try {
      ProcessBuilder pb = new ProcessBuilder(command);
      pb.redirectError(INHERIT).redirectOutput(INHERIT);
      Process process = pb.start();
      if (process.waitFor() != 0) {
        throw new RuntimeException("Command failed: " + String.join(" ", command));
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("Failed to run command", e);
    }
  }

  public static void waitUntilHealthy(String url, int timeoutSeconds) {
    long end = System.currentTimeMillis() + timeoutSeconds * 1000L;
    while (System.currentTimeMillis() < end) {
      try {
        HttpRequest request =
            HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int resopnseCode = response.statusCode();
        String responseBody = response.body();
        if (resopnseCode == 200 && responseBody.contains("UP")) {
          return;
        }
      } catch (Exception ignored) {
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ignored) {
      }
    }
    throw new RuntimeException("Timed out waiting for service to become healthy: " + url);
  }
}
