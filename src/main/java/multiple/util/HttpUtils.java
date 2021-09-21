package multiple.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Description: 下载工具类
 * @Author: zhangqingbiao
 * @Date: 2021/9/11 0:40
 */
public class HttpUtils {


  public static long getHttpFileContentLength(String url) throws IOException {
    int contentLength;
    HttpURLConnection httpUrlConnection = null;
    try {
      // 获取下载链接
      httpUrlConnection = getHttpUrlConnection(url);
      // 获取下载大小
      contentLength = httpUrlConnection.getContentLength();
    } finally {
      //   关闭对象
      if (httpUrlConnection != null) {
        httpUrlConnection.disconnect();
      }
    }


    return contentLength;
  }

  /**
   * 获取HttpURLConnection
   *
   * @param url 文件路径
   * @return
   */
  public static HttpURLConnection getHttpUrlConnection(String url) throws IOException {
    URL httpUrl = new URL(url);
    HttpURLConnection httpUrlConnection = (HttpURLConnection) httpUrl.openConnection();
    // 向网站服务器发生标识信息
    httpUrlConnection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
    return httpUrlConnection;

  }

  /**
   * 获取文件下载名称
   *
   * @param url
   * @return
   */
  public static String getHttpFileName(String url) {
    int i = url.lastIndexOf("/");
    return url.substring(i + 1);
  }

  /**
   * 分块下载
   *
   * @param url      下载地址
   * @param startPos 下载起始位置
   * @param endPos   下载结束问题在
   * @return
   */
  public static HttpURLConnection getHttpUrlConnection(String url, long startPos, long endPos) throws IOException {

    // 获取链接对象
    HttpURLConnection httpUrlConnection = getHttpUrlConnection(url);
    LogUtils.info("下载的区间是：{}-{}", startPos, endPos);

    if (endPos != 0) {
      // 100 - 200
      httpUrlConnection.setRequestProperty("RANGE", "bytes=" + startPos + "-" + endPos);
    } else {
      // 说明下载剩余所有数据，也就是最后一块区域内存。
      httpUrlConnection.setRequestProperty("RANGE", "bytes=" + startPos + "-");
    }
    return httpUrlConnection;
  }
}
