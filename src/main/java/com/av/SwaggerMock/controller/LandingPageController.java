package com.av.SwaggerMock.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LandingPageController {

  @GetMapping("/")
  public String home() {

    ClassPathResource bannerResource = new ClassPathResource("banner.txt");
    ClassPathResource htmlResource = new ClassPathResource("landingPage.txt");
    String banner = null, landingPageHtml = null;

    try {
      banner = StreamUtils.copyToString(bannerResource.getInputStream(), StandardCharsets.UTF_8);
      landingPageHtml =
          StreamUtils.copyToString(htmlResource.getInputStream(), StandardCharsets.UTF_8);

    } catch (IOException e) {
      log.info("Couldn't load SwaggerMock banner or landing page HTML.");
    }
    return String.format(landingPageHtml, banner);
  }
}
