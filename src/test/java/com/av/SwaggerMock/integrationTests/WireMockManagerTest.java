package com.av.SwaggerMock.integrationTests;

import com.av.SwaggerMock.wiremock.WireMockManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

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
