package multiple.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;


/**
 * @Description: zookeeper 的Api
 * @Author: zhangqingbiao
 * @Date: 2021/9/21 15:17
 * @Version: 1.0
 */
public class ZkApi {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        // 1,创建zookeeper连接

        ZooKeeper zooKeeper = new ZooKeeper("47.119.171.11:2181", 2000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("触发了：" + watchedEvent.getType() + "事件");
            }
        });
        // 2，创建父节点
//        String path = zooKeeper.create("/items", "initvalue".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        System.out.println(path);

        // Exception in thread "main" org.apache.zookeeper.KeeperException$ConnectionLossException: KeeperErrorCode = ConnectionLoss for /item

        // 3，创建子节点

//        String childPath = zooKeeper.create("/items/child", "childInitvalue".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        System.out.println(childPath);


        // 4，获取节点的值（父节点和子节点）

        byte[] data = zooKeeper.getData("/items", false, null);
        System.out.println(new String(data));

        List<String> children = zooKeeper.getChildren("/items", false);
        for (String str : children) {
            System.out.println(str);
        }
        // 5，修改节点的值
        Stat stat = zooKeeper.setData("/items", "itemsUpdate".getBytes(), -1);
        System.out.println(stat);
        // 6,判断某个节点是否存在
        Stat exists = zooKeeper.exists("/items/child", false);
        System.out.println(exists);
        // 7，删除节点

        //
    }
}
