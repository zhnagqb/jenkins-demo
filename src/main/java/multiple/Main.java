package multiple;

import com.multiple.core.Downloader;
import com.multiple.util.LogUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

/**
 * @Description: 文件下载器的主类
 * @Author: zhangqingbiao
 * @Date: 2021/9/11 0:30
 */
public class Main {
  public static void main(String[] args) {
    // 下载地址
    String url = null;
    if (ArrayUtils.isEmpty(args)) {
      for (; ; ) {
        LogUtils.info("请输入下载链接");
//        System.out.println("请输入下载链接");
        Scanner scanner = new Scanner(System.in);
        url = scanner.next();
        if (StringUtils.isNotBlank(url)) {
          break;
        }
      }
    } else {
      url = args[0];
    }
    System.out.println(url);

//    url="https://down.qq.com/qqweb/PCQQ/PCQQ_EXE/PCQQ2021.exe";
    Downloader downloader = new Downloader();
    downloader.download(url);
    System.out.println("下载成功");
  }
}
//https://download.jetbrains.com.cn/idea/ideaIU-2021.2.1.exe
//https://1ff901e751096c191bf9e36d937e0568.dlied1.cdntips.net/dlied1.qq.com/qqweb/PCQQ/PCQQ_EXE/PCQQ2021.exe?mkey=613bb684705f161e&f=8917&cip=112.95.48.235&proto=https&access_type=