package multiple.learn;

import com.multiple.constant.Constant;
import com.multiple.core.DownloadInfoThread;
import com.multiple.util.FileUtils;
import com.multiple.util.HttpUtils;
import com.multiple.util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 下载器
 * @Author: zhangqingbiao
 * @Date: 2021/9/11 0:49
 */
public class DownloaderTest {

  public ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);


  public void download(String url) {

    // 获取文件名
    String httpFileName = HttpUtils.getHttpFileName(url);

    // 文件下载路径
    httpFileName = Constant.PATH + httpFileName;

    // 获取本地文件的大小

    long localFIleLength = FileUtils.getFileContentLength(httpFileName);


    // 获取连接对象
    HttpURLConnection httpUrlConnection = null;
    DownloadInfoThread downloadInfoThread = null;
    try {
      httpUrlConnection = HttpUtils.getHttpUrlConnection(url);

      // 获取下载文件的总大小
      int contentLength = httpUrlConnection.getContentLength();
      // 判断文件是否已经下载过
      if (localFIleLength >= contentLength) {
        LogUtils.info("{}已下载完毕，无需重新下载", httpFileName);
        return;
      }


      // 创建获取下载信息的任务对象
      downloadInfoThread = new DownloadInfoThread(contentLength);
      // 将任务交给线程执行，每隔一秒执行一次
      scheduledExecutorService.scheduleAtFixedRate(downloadInfoThread, 1, 1, TimeUnit.SECONDS);


    } catch (IOException e) {
      e.printStackTrace();
    }


    try (
        // 把下载的文件读取到内存里了
        InputStream input = httpUrlConnection.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(input);
        // 将流进行存储
        FileOutputStream fos = new FileOutputStream(httpFileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos)
    ) {
      int len = -1;

      byte[] buffer = new byte[Constant.BYTE_SIZE];


      while ((len = bis.read(buffer)) != -1) {


        // 1秒内，下载了多少文件
//        (int)downloadInfoThread.downSize.doubleValue() += len;

        bos.write(buffer, 0,  len);
      }
    } catch (FileNotFoundException notfound) {
      LogUtils.error("下载文件不存在.{}", url);
    } catch (IOException e) {
      LogUtils.error("下载文件失败。");
      e.printStackTrace();
    } finally {

      System.out.print("\r");
      System.out.print("下载完成");

      // 关闭连接对象
      if (httpUrlConnection != null) {
        httpUrlConnection.disconnect();
      }
      // 关闭
      scheduledExecutorService.shutdownNow();
    }

  }

}
