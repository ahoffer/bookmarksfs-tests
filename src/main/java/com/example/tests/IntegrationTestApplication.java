/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * http://www.gnu.org/licenses/lgpl.html.
 */
package com.example.tests;

import java.util.Map;
import org.codice.itest.api.IntegrationTest;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = {"org.codice", "com.example.tests"})
public class IntegrationTestApplication {

  public static void main(String... args) {

    ApplicationContext context = SpringApplication.run(IntegrationTestApplication.class, args);
    Map<String, IntegrationTest> integrationTests = context.getBeansOfType(IntegrationTest.class);
    if (integrationTests.isEmpty()) {
      System.out.println("No tests found");
    }
    ExitCodeGenerator exitCodeGenerator = context.getBean(ExitCodeGenerator.class);
    if (exitCodeGenerator != null) System.exit(exitCodeGenerator.getExitCode());
  }
}
