package com.example.demo.util;

import java.io.File;

public interface TBoardConstant {
  public static final String UPLOAD_FOLDER = System.getProperty("user.dir") + File.separator + "upload";
  public static final String TEMP_FOLDER = System.getProperty("user.dir") + File.separator + "upload" + File.separator + "temp" + File.separator;
  public static final String PROFILE_FOLDER = System.getProperty("user.dir") + File.separator + "upload" + File.separator + "profile" + File.separator;

}
