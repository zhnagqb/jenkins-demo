package multiple.learn;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: zhangqingbiao
 * @Date: 2021/9/11 13:14
 */
public class PoolTest01 {
  public static void main(String[] args) throws InterruptedException {
  /*  new ThreadPoolExecutor(2, 3, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(2),
        r -> {
          Thread thread = new Thread(r);
          thread.setName("zhangsan");
          return thread;
        }
    );*/

    ThreadPoolExecutor threadPool =null;
  try{
    // 创建线程池
     threadPool = new ThreadPoolExecutor(1, 4, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(3));

    // 创建任务
    Runnable r = () -> {

      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println(Thread.currentThread().getName());


    };

    // Runnable  就是创建任务的。并不是线程池，或线程，只有把任务放到线程里，才算关联
//    Thread thread = new Thread(r);
    // 将任务提交给线程池

    //本质 将线程交给默认的线程工厂里了。
    for (int i = 0; i < 7; i++) {  // 提交相同任务，实际场景不是相同任务，就不能这么写
/**
 *
 * 最大线程数是3，核心线程数是2  因为线程名称是3  说明非核心线程数已经启动了
 * pool-1-thread-1
 * pool-1-thread-3
 * pool-1-thread-2
 * pool-1-thread-3
 * pool-1-thread-1
 */
      threadPool.execute(r);
    }

    System.out.println(threadPool);
    TimeUnit.SECONDS.sleep(10);
    System.out.println(threadPool);
  }finally {

    if(threadPool !=null){
//      threadPool.shutdown(); // 温和
//      threadPool.shutdownNow(); // 暴力

      threadPool.shutdown();

      if(!threadPool.awaitTermination(1,TimeUnit.MINUTES)){
        // 等待1分钟，如果线程池没有关闭，则会执行进来
        threadPool.shutdownNow();
      }

//      jdk 中提供的一些工作队列 workQueue
//      1,SynchronousQueue   ：  直接提交队列 ，队列里不放线程，有的话直接往核心线程里放，核心线程满了，就放到非核心线程里。

//      2,ArrayBlockingQueue  ： 有界队列，可以指定容量，

//      3,LinkedBlockingDeque :  无界队列
//      4,PriorityBlockingQueue :   优先任务队列，可以根据任务优先级顺序执行任务。
    }

  }

    // 拒绝策略  reject  当线程队列和最大线程数满了，会执行拒绝策略

    /**
     * 线程状态：
     * RUNNING ： 创建线程之后的状态
     *
     * SHUTDOWN ： 该状态下，线程池就不会接收新任务，但会处理阻塞队列剩余任务，相对温和。
     *
     * STOP : 该状态下会中断正在执行的任务，并抛弃阻塞队列任务，相对暴力
     *
     * TIDYING : 任务全部执行完毕，活动线程为0 即将进入终止。
     *
     * TERMINATED : 线程终止
     *
     *
     * 线程的关闭：
     * shutdown() ： 该方法执行后，线程池状态会变为SHUTDOWN，不会接收新任务，但是会执行完已提交的任务，此方法不会阻塞调用线程的执行。
     *
     * shutdownNow() : 该方法执行后，线程池状态变为STOP，不会接收新任务，会将队列中的任务返回。
     *并用interrupt 的方式中断正在执行的任务。
     *
     *
     *
     *
     */

  }
}
