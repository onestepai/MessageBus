//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.onestep.os.utils;

import com.onestep.os.error.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class LoggerUtils {
    private static final Logger logger = LoggerFactory.getLogger(LoggerUtils.class);

    public LoggerUtils() {
    }

    public static void infoForWebLogAspect(String log) {
        logger.info(log);
    }

    public static void info(String log) {
        StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
        StringBuilder sb = new StringBuilder("{");
        sb.append(",\"file_name\":\"").append(stackTraceElement.getFileName()).append('"');
        sb.append(",\"class_name\":\"").append(stackTraceElement.getClassName()).append('"');
        sb.append(",\"method_name\":\"").append(stackTraceElement.getMethodName()).append('"');
        sb.append(",\"line_number\":\"").append(stackTraceElement.getLineNumber()).append('"');
        sb.append(",\"log\":\"").append(log).append('"');
        sb.append('}');
        logger.info(sb.toString());
    }

    public static void warn(String log) {
        StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
        StringBuilder sb = new StringBuilder("{");
        sb.append(",\"file_name\":\"").append(stackTraceElement.getFileName()).append('"');
        sb.append(",\"class_name\":\"").append(stackTraceElement.getClassName()).append('"');
        sb.append(",\"method_name\":\"").append(stackTraceElement.getMethodName()).append('"');
        sb.append(",\"line_number\":\"").append(stackTraceElement.getLineNumber()).append('"');
        sb.append(",\"log\":\"").append(log).append('"');
        sb.append('}');
        logger.warn(sb.toString());
    }

    public static void error(Error err, String log) {
        StackTraceElement stackTraceElement = (new Throwable()).getStackTrace()[1];
        StringBuilder sb = new StringBuilder("{");
        sb.append(",\"file_name\":\"").append(stackTraceElement.getFileName()).append('"');
        sb.append(",\"class_name\":\"").append(stackTraceElement.getClassName()).append('"');
        sb.append(",\"method_name\":\"").append(stackTraceElement.getMethodName()).append('"');
        sb.append(",\"line_number\":\"").append(stackTraceElement.getLineNumber()).append('"');
        sb.append(",\"error_code\":\"").append(err.getErrorCode()).append('"');
        sb.append(",\"log\":\"").append(log).append('"');
        sb.append('}');
        logger.error(sb.toString());
    }

    public static void exception(Error err, Exception ex) {
        StringBuilder sb = new StringBuilder("{");
        sb.append(",\"exception\":\"").append(CollectionUtils.arrayToList(ex.getStackTrace()).toString()).append('"');
        sb.append(",\"error_code\":\"").append(err.getErrorCode()).append('"');
        sb.append('}');
        logger.error(sb.toString());
    }
}
