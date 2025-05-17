package com.av.SwaggerMock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Component
public class WireMockManager {
    private WireMockServer wireMockServer;

    public synchronized void startServerIfNotRunning() {
        if (wireMockServer == null || !wireMockServer.isRunning()) {
            System.out.println("Starting WireMock server on port 9456");
            wireMockServer = new WireMockServer(options().port(9456));
            wireMockServer.start();
            WireMock.configureFor("localhost", 9456);
        }
    }

    public void addMapping(StubMapping mapping) {
        startServerIfNotRunning();
        wireMockServer.addStubMapping(mapping);
    }

    @PreDestroy
    public void stopServer() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }
}
