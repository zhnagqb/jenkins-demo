package multiple.core;

import com.multiple.constant.Constant;
import com.multiple.util.HttpUtils;
import com.multiple.util.LogUtils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @Description: 分块下载任务
 * @Author: zhangqingbiao
 * @Date: 2021/9/11 14:57
 */
public class DownloaderTask implements Callable<Boolean> {
  // 结束位置
  private long endPos;
  // 标识当前下载的是第几块
  private int part;
  // 起始位置
  private long startPos;
  // 地址
  private String url;

  private CountDownLatch countDownLatch;

  public DownloaderTask(String url, long startPos, long endPos, int part,CountDownLatch countDownLatch) {
    this.endPos = endPos;
    this.part = part;
    this.startPos = startPos;
    this.url = url;
    this.countDownLatch = countDownLatch;
  }

  @Override
  public Boolean call() throws Exception {

    // 1，获取文件名
    String httpFileName = HttpUtils.getHttpFileName(url);

    //2,分块的文件名
    httpFileName = httpFileName + ".temp" + part;
    //3,下载路径
    httpFileName = Constant.PATH + httpFileName;
    // 4,获取分块下载连接
    HttpURLConnection httpUrlConnection = HttpUtils.getHttpUrlConnection(url, startPos, endPos);
    try (
        InputStream input = httpUrlConnection.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(input);
        // 断点下载 移动指针
        RandomAccessFile accessFile = new RandomAccessFile(httpFileName, "rw")

    ) {
      byte[] buffer = new byte[Constant.BYTE_SIZE];
      int len = -1;
      // 循环读取数据
      while ((len = bis.read(buffer)) != -1) {
        // 1秒内下载数据之和,通过原子类进行操作
        DownloadInfoThread.downSize.add(len);
        accessFile.write(buffer, 0, len);
      }

    } catch (FileNotFoundException e) {

      LogUtils.error("下载文件不存在{}" + url);
      return false;
    } catch (Exception e) {
      LogUtils.error("下载出现异常");
      return false;
    } finally {
      httpUrlConnection.disconnect();
      // countDownLatch 会做减1 操作，当置0 的时候，就会执行。
      countDownLatch.countDown();
    }
    return true;
  }
}
