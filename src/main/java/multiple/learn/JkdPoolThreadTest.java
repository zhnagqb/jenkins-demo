package multiple.learn;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: jdk创建线程池的便捷方式  阿里巴巴 官方文档中，推荐 不建议使用这些线程池。 因为你对它的内部不了解，会出现一些问题，例如无界队列。
 * @Author: zhangqingbiao
 * @Date: 2021/9/11 14:41
 */
public class JkdPoolThreadTest {
  public static void main(String[] args) {

    // 无核心线程数，无限队列
    ExecutorService executorService = Executors.newFixedThreadPool(5);

    // 有1个核心线程数，无其他线程了。
    ExecutorService executorService1 = Executors.newSingleThreadExecutor();
    //keepaliveTime  是指非核心线程的存活时间。


  }

}
