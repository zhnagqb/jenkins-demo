package multiple.util;

import java.io.File;

/**
 * @Description: 文件工具类
 * @Author: zhangqingbiao
 * @Date: 2021/9/11 1:57
 */
public class FileUtils {

  /**
   * 获取本地文件的大小
   */
  public static long getFileContentLength(String path) {
    File file = new File(path);
    return file.exists() && file.isFile() ? file.length() : 0;
  }

}
