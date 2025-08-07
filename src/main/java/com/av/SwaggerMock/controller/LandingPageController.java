package com.av.SwaggerMock.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LandingPageController {

    private final String banner =
"""
  /$$$$$$                                                                  /$$      /$$                     /$$     \s
 /$$__  $$                                                                | $$$    /$$$                    | $$     \s
| $$  \\__/ /$$  /$$  /$$  /$$$$$$   /$$$$$$   /$$$$$$   /$$$$$$   /$$$$$$ | $$$$  /$$$$  /$$$$$$   /$$$$$$$| $$   /$$
|  $$$$$$ | $$ | $$ | $$ |____  $$ /$$__  $$ /$$__  $$ /$$__  $$ /$$__  $$| $$ $$/$$ $$ /$$__  $$ /$$_____/| $$  /$$/
 \\____  $$| $$ | $$ | $$  /$$$$$$$| $$  \\ $$| $$  \\ $$| $$$$$$$$| $$  \\__/| $$  $$$| $$| $$  \\ $$| $$      | $$$$$$/\s
 /$$  \\ $$| $$ | $$ | $$ /$$__  $$| $$  | $$| $$  | $$| $$_____/| $$      | $$\\  $ | $$| $$  | $$| $$      | $$_  $$\s
|  $$$$$$/|  $$$$$/$$$$/|  $$$$$$$|  $$$$$$$|  $$$$$$$|  $$$$$$$| $$      | $$ \\/  | $$|  $$$$$$/|  $$$$$$$| $$ \\  $$
 \\______/  \\_____/\\___/  \\_______/ \\____  $$ \\____  $$ \\_______/|__/      |__/     |__/ \\______/  \\_______/|__/  \\__/
                                   /$$  \\ $$ /$$  \\ $$                                                              \s
                                  |  $$$$$$/|  $$$$$$/                                                              \s
                                   \\______/  \\______/                                                               \s
""";

    @GetMapping("/")
    public String home() {

    return String.format(
"""
<pre>
%s
</pre>
<h2>Welcome to SwaggerMock!</h2>
<p>
Send a <b>POST</b> request to <code>/wiremock/upload-spec</code> with your Swagger spec in the request body.<br>
You will receive the generated WireMock stub back in the response.
</p>
""", banner);
    }
}
