package utils;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author joy
 * @date 2020/2/22 11:58
 */
@Slf4j
public class ThreadPoolExecutorUtil {

    public static final String PRE_MATCH_SINGLE_THREAD = "matchSingleThread_";

    private static ConcurrentHashMap<String, ThreadPoolExecutor> executorServiceSingle = new ConcurrentHashMap<>(48);

    private static ConcurrentHashMap<String, ThreadPoolExecutor> executorServiceSingleMatch = new ConcurrentHashMap<>(BaccaratTableNameUtil.SHARD_NUM);


    public static ConcurrentHashMap<String, ThreadPoolExecutor> getSingleThreadExecutorAll() {
        return executorServiceSingle;
    }

    public static ConcurrentHashMap<String, ThreadPoolExecutor> getSingleThreadExecutorAllMatch() {
        return executorServiceSingleMatch;
    }

    /**
     * 48个表，有48个线程池队列，队列执行同一个表的多个局任务
     * @param tableName 逻辑表
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor getSingleThreadExecutor(String tableName) {
        String name = "gameSingleThread_" + tableName.split("_")[3];
        return executorServiceSingle.get(name);
    }

    public static void initSingleThreadExecutor(List<String> tableNames) {
        tableNames.forEach(tableName -> {
            String name = "gameSingleThread_" + tableName.split("_")[3];
            if (!executorServiceSingle.containsKey(name)) {
                ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, name));
                executorServiceSingle.put(name, executor);
            }
        });
    }

    public static ThreadPoolExecutor getSingleMatchThreadExecutor(String tableName) {
        String name = PRE_MATCH_SINGLE_THREAD + tableName.split("_")[4];
        return executorServiceSingleMatch.get(name);
    }

    public static void initSingleMatchThreadExecutor(List<String> tableNames) {
        tableNames.forEach(tableName -> {
            String name = PRE_MATCH_SINGLE_THREAD + tableName.split("_")[4];
            if (!executorServiceSingleMatch.containsKey(name)) {
                ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, name));
                executorServiceSingleMatch.put(name, executor);
            }
        });
    }
}
