package multiple.core;

import com.multiple.constant.Constant;
import com.multiple.util.FileUtils;
import com.multiple.util.HttpUtils;
import com.multiple.util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * @Description: 下载器
 * @Author: zhangqingbiao
 * @Date: 2021/9/11 0:49
 */
@SuppressWarnings({"ALL", "AlibabaCommentsMustBeJavadocFormat"})
public class Downloader {

  // 线程池对象
  @SuppressWarnings("AlibabaCommentsMustBeJavadocFormat")
  public ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(Constant.THREAD_NUM, Constant.THREAD_NUM, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(Constant.THREAD_NUM));
  public ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

  // 标记线程个数
  @SuppressWarnings("AlibabaCommentsMustBeJavadocFormat")
  private CountDownLatch countDownLatch = new CountDownLatch(Constant.THREAD_NUM);


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

      //切分任务
      ArrayList<Future> list = new ArrayList<>();
      split(url, list);

      /*list.forEach(future -> {
        try {
          future.get();
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        }
      });*/

      // 文件下完成
    // 当所有线程都执行完成后，才会执行计数器，当线程计数器为5 的时候才会执行后面的程序
      countDownLatch.await();
      // 进行合并文件
      if (merge(httpFileName)) {
        // 清除临时文件
        clearTemp(httpFileName);
      }


    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
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
      // 关闭线程池
      poolExecutor.shutdown();
    }

  }

  /**
   * 文件切分
   *
   * @param url
   */
  public void split(String url, ArrayList<Future> futureList) {
    // 获取文件下载大小
    try {
      long contentLength = HttpUtils.getHttpFileContentLength(url);
      // 固定文件切分成5块，
//      Constant.THREAD_NUM

      // 计算切分后的文件大小
      long size = contentLength / Constant.THREAD_NUM;

      // 根据分块个数
      for (int i = 0; i < Constant.THREAD_NUM; i++) {
        // 计算下载起始位置
        long startPos = i * size;
        // 计算下载的结束位置

        long endPos;
//         i * size
        // 下载最后一块
        if (i == Constant.THREAD_NUM - 1) {
          // 下载最后一块，下载剩余部分
          endPos = 0;
        } else {
          endPos = startPos + size;
        }

        // 如果不是第一块，起始位置+1
        if (startPos != 0) {
          startPos++;
        }

        // 创建任务
        DownloaderTask downloaderTask = new DownloaderTask(url, startPos, endPos, i,countDownLatch);

        // 将任务提交到线程池中。
        // submit 是提交callable ，本质上还是调的execute
        Future<Boolean> submit = poolExecutor.submit(downloaderTask);
        futureList.add(submit);
      }


    } catch (IOException e) {
      e.printStackTrace();
    }

  }
//注意： 分块下载文件的时候，如果不是第一课，那么起始位置需要+1，

  public boolean merge(String fileName) {
    LogUtils.info("开始合并文件{}", fileName);
    // new一个数组去读取文件

    byte[] buffer = new byte[Constant.BYTE_SIZE];
    int len = -1;
    try (
        RandomAccessFile accessFile = new RandomAccessFile(fileName, "rw")) {
      for (int i = 0; i < Constant.THREAD_NUM; i++) {

        try (
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName + ".temp" + i))
        ) {
          while ((len = bis.read(buffer)) != -1) {
            // 读取buffer, 从0 开始读，读len长度
            accessFile.write(buffer, 0, len);
          }

        }
      }
      LogUtils.info("文件合并完毕{}", fileName);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      LogUtils.error("文件未找到：{}", fileName);
      return false;
    } catch (IOException e) {
      LogUtils.error("文件合并失败，文件是：{}", fileName);
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * 清除文件
   *
   * @param fileName 文件名称
   * @return
   */
  public boolean clearTemp(String fileName) {
    for (int i = 0; i < Constant.THREAD_NUM; i++) {
      File file = new File(fileName + ".temp" + i);
      file.delete();

    }

    return true;
  }

}
