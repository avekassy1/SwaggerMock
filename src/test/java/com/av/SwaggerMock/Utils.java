package com.av.SwaggerMock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;

public class Utils {
  public static String readFileToString(String path) throws IOException {
    URL resource = ClassLoader.getSystemClassLoader().getResource(path);
    if (resource == null) {
      throw new FileNotFoundException("Could not find resource " + path);
    }
    File file = new File(resource.getFile());
    return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
  }
}
