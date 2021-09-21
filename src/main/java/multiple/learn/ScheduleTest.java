package multiple.learn;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: zhangqingbiao
 * @Date: 2021/9/11 1:26
 */
public class ScheduleTest {
  public static void main(String[] args) {

    ScheduledExecutorService sc = Executors.newScheduledThreadPool(1);
    // 延时2秒后开始执行任务，每间隔3s秒再执行任务。
    sc.scheduleAtFixedRate(() ->{ System.out.println(System.currentTimeMillis());

      try {
        TimeUnit.SECONDS.sleep(6);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }


    }, 2, 3, TimeUnit.SECONDS);
  }

  public static void schedule() {
    // 获取对象
    ScheduledExecutorService sc = Executors.newScheduledThreadPool(1);
    //Runnable 可以看做是任务
    // 延时2s后再执行
    sc.schedule(() -> System.out.println(Thread.currentThread().getName()), 2, TimeUnit.SECONDS);

    // 关闭
    sc.shutdown();

  }

}
