package com.onestep.os.messagebusservice.util;

import com.onestep.os.error.Error;
import com.onestep.os.utils.LoggerUtils;

public class LoggerHelper {

  public static void info(String msg, Boolean logOn) {
    if (!logOn) {
      return;
    }
    LoggerUtils.info(msg);
  }

  public static void error(Error err, String msg) {
    LoggerUtils.error(err, msg);
  }

  public static void exception(Error err, Exception ex) {
    LoggerUtils.exception(err, ex);
  }
}
