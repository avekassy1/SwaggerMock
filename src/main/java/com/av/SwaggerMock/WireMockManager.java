package com.av.SwaggerMock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class WireMockManager {
    private WireMockServer wireMockServer;

    @PostConstruct
    public void startWireMock() {
        if (wireMockServer == null || !wireMockServer.isRunning()) {
            System.out.println("Starting WireMock server on port 9456");

            WireMockConfiguration config = WireMockConfiguration.wireMockConfig()
                .port(9456);
            wireMockServer = new WireMockServer(config);
            wireMockServer.start();

        }
    }

    public WireMockServer getServer() {
        return wireMockServer;
    }

    public void addMapping(StubMapping mapping) {
        //startServerIfNotRunning();
        wireMockServer.addStubMapping(mapping);
    }

    @PreDestroy
    public void stopServer() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }
}
