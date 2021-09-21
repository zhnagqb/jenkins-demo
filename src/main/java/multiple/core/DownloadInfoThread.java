package multiple.core;

import com.multiple.constant.Constant;

import java.util.concurrent.atomic.LongAdder;

/**
 * @Description: 展示下载信息
 * @Author: zhangqingbiao
 * @Date: 2021/9/11 1:38
 */
public class DownloadInfoThread implements Runnable {

  //本次累积下载的大小
  // 这里的static  只能适合一个用户进行下载，如果是多个用户，就需要吧static给删掉，否则会出现问题
  public static volatile LongAdder downSize = new LongAdder();

  //本地已下载文件的大小  原子性
  public static LongAdder finishSize = new LongAdder();

  // 前一次下载的大小
  public double preSize;

  // 下载文件总大小
  private long httpContentLength;

  public DownloadInfoThread(long httpContentLength) {
    this.httpContentLength = httpContentLength;
  }

  @Override
  public void run() {


    // 计算文件总大小 单位是Mb
    String httpFileSize = String.format("%.2f", httpContentLength / Constant.MB);

    // 计算每秒下载速度  kb

    int speed = (int) ((downSize.doubleValue() - preSize) / 1024d);
    preSize = downSize.doubleValue();

    // 计算剩余大小
    double remainSize = httpContentLength - finishSize.doubleValue() - downSize.doubleValue();
    // 计算剩余时间 计算的是kb
    String remainTime = String.format("%.1f", remainSize / 1024d / speed);

    if ("Infinity".equalsIgnoreCase(remainTime)) {
      remainTime = "——";
      return;
    }
    // 下载大小
    String currentFileSize = String.format("%.2f", (downSize.doubleValue() - finishSize.doubleValue()) / Constant.MB);

    String downInfo = String.format("已下载 %smb/%smb,速度 %skb/s,剩余时间 %ss", currentFileSize, httpFileSize, speed, remainTime);

    System.out.print("\r");
    System.out.print(downInfo);


  }
}
