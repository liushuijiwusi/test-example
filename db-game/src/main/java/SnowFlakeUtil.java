import lombok.extern.slf4j.Slf4j;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SnowFlakeUtil {

    private static SnowFlakeUtil snowFlakeUtil = null;

    /**
     * 开始时间截 (这个用自己业务系统上线的时间)
     */
    private final long twepoch = 1477666800000L;

    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 10L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 12L;

    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 时间截向左移22位(10+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器ID(0~1023)
     */
    private long workerId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    private static String ip = null;

    //==============================Constructors=====================================

    /**
     * 构造函数
     *
     * @param workerId 工作ID (0~1023)
     */
    public SnowFlakeUtil(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
        }
        this.workerId = workerId;
    }

    // ==============================Methods==========================================

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        /**
         * 发生时间回拨问题后会等待，最多等3秒，每10毫秒检查一次
         */
        if (timestamp < lastTimestamp) {
            log.error("雪花算法，时间回拨问题，需要关注. timestamp:{},lastTimestamp:{}", timestamp, lastTimestamp);
            while (true) {
                // 时间回拨问题，超过3秒抛异常
                if (timestamp - lastTimestamp > 3 * 1000) {
                    throw new RuntimeException(
                            String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
                } else {
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    return nextId();
                }
            }
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        long id = ((timestamp - twepoch) << timestampLeftShift) //
                | (workerId << workerIdShift) //
                | sequence;
//        log.info("snowid={},ip={}", id, ip);
        return id;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }


    public static long newNextId() {
        if (null == snowFlakeUtil) {
            synchronized (SnowFlakeUtil.class) {
                if (null == snowFlakeUtil) {
                    // 通过ip计算workerid，防止多机器重复
                    InetAddress address = getLocalAddress();

                    String ipstr = normalizeHostAddress(address);
                    ip = ipstr;
                    log.info(">>>>>>ip>>>>>>>>" + ipstr);

                    byte[] ipAddressByteArray = address.getAddress();
                    long workId = (long) (((ipAddressByteArray[ipAddressByteArray.length - 2] & 0B11) << Byte.SIZE) + (ipAddressByteArray[ipAddressByteArray.length - 1] & 0xFF));

                    log.info(">>>>>>>>>>workId>>>>>>>>>>>" + workId);

                    snowFlakeUtil = new SnowFlakeUtil(workId);
                }
            }
        }
        return snowFlakeUtil.nextId();
    }

    public static InetAddress getLocalAddress() {
        try {
            // Traversal Network interface to get the first non-loopback and non-private address
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            ArrayList<InetAddress> ipv4Result = new ArrayList<>();
            ArrayList<InetAddress> ipv6Result = new ArrayList<>();
            while (enumeration.hasMoreElements()) {
                final NetworkInterface networkInterface = enumeration.nextElement();

                /**
                 * -----修改点--固定获取eth0网卡ip--start-----
                 */
                if (!"eth0".equalsIgnoreCase(networkInterface.getName())) {
                    continue;
                }
                /**
                 * -----修改点--固定获取eth0网卡ip--end-----
                 */

                final Enumeration<InetAddress> en = networkInterface.getInetAddresses();
                while (en.hasMoreElements()) {
                    final InetAddress address = en.nextElement();
                    if (!address.isLoopbackAddress()) {
                        if (address instanceof Inet6Address) {
                            ipv6Result.add(address);
                        } else {
                            ipv4Result.add(address);
                        }
                    }
                }
            }

            // prefer ipv4
            if (!ipv4Result.isEmpty()) {
                for (InetAddress ip : ipv4Result) {
                    if (normalizeHostAddress(ip).startsWith("127.0") || normalizeHostAddress(ip).startsWith("192.168")) {
                        continue;
                    }

                    return ip;
                }

                return ipv4Result.get(ipv4Result.size() - 1);
            } else if (!ipv6Result.isEmpty()) {
                return ipv6Result.get(0);
            }
            //If failed to find,fall back to localhost
            final InetAddress localHost = InetAddress.getLocalHost();
            return localHost;
        } catch (Exception e) {
            log.error("Cannot get LocalHost InetAddress, please check your network!", e);
        }

        return null;
    }


    public static String normalizeHostAddress(final InetAddress localHost) {
        if (localHost instanceof Inet6Address) {
            return "[" + localHost.getHostAddress() + "]";
        } else {
            return localHost.getHostAddress();
        }
    }

    public static String getLocalIp() {

        return normalizeHostAddress(getLocalAddress());
    }
}
