package com.example.demo.util;

import org.springframework.http.*;
import org.springframework.web.multipart.*;

import java.io.*;
import java.util.*;

public class ProfileUtil {
  private static final String PROFLILE_FOLDER = System.getProperty("user.dir") + File.separator + "upload" + File.separator + "profile" + File.separator;
  private static final String PROFILE_NAME = "default.jpg";

  public static MediaType getMediaType(String imageName) {
    String mimeType = "image/jpeg";
    imageName = imageName.toLowerCase();
    if(imageName.endsWith(".png"))
      mimeType = "image/png";
    else if(imageName.endsWith(".gif"))
      mimeType = "image/gif";
    else if(imageName.endsWith(".webp"))
      mimeType = "image/webp";
    return MediaType.parseMediaType(mimeType);
  }
}
