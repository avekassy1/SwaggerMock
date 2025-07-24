package com.av.SwaggerMock.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WireMockManager {
  private WireMockServer wireMockServer;

  private int PORT = 9999;

  @PostConstruct
  public void startWireMock() {
    if (wireMockServer == null || !wireMockServer.isRunning()) {
      log.info("Starting WireMock server on port {}", PORT);

      WireMockConfiguration config = WireMockConfiguration.wireMockConfig().port(PORT);
      wireMockServer = new WireMockServer(config);
      wireMockServer.start();
    }
  }

  public WireMockServer getServer() {
    return wireMockServer;
  }

  public void addMapping(StubMapping mapping) {
    wireMockServer.addStubMapping(mapping);
  }

  @PreDestroy
  public void stopServer() {
    if (wireMockServer != null && wireMockServer.isRunning()) {
      wireMockServer.stop();
    }
  }
}
