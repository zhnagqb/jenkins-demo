package multiple.util;

import org.apache.commons.lang3.ObjectUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description: 日志工具类
 * @Author: zhangqingbiao
 * @Date: 2021/9/11 1:14
 */
public class LogUtils {

  public static void error(String msg, Object... args) {
    print(msg, "-error-", args);
  }

  public static void info(String msg, Object... args) {
    print(msg, "-info-", args);
  }

  private static void print(String msg, String level, Object... args) {
    if (ObjectUtils.isNotEmpty(args)) {
      msg = String.format(msg.replace("{}", "%s"), args);
    }
    String name = Thread.currentThread().getName();
    System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")) + "" + name + level + msg);

  }

  public static void trace(String msg, Object... args) {
    print(msg, "-trace-", args);
  }

}
