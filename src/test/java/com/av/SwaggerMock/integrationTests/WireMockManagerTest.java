package com.av.SwaggerMock.integrationTests;

import com.av.SwaggerMock.wiremock.WireMockManager;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WireMockManagerTest {
    private WireMockManager wireMockManager;

    @BeforeEach
    void setUp() {
        wireMockManager = new WireMockManager();
        wireMockManager.startWireMock();
    }

    @AfterEach
    void tearDown() {
        wireMockManager.stopServer();
    }

//    @Test
//    void testGetServerReturnsRunningWireMockServer() {
//        WireMockServer server = wireMockManager.getServer();
//        assertNotNull(server);
//        assertTrue(server.isRunning());
//        assertEquals(9999, server.port());
//    }
//
//    @Test
//    void testStopServerStopsWireMock() {
//        wireMockManager.stopServer();
//        assertFalse(wireMockManager.getServer().isRunning());
//    }
}
