package com.permission.utils.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * **********************
 * * Author: StillWarm  *
 * * Date: 2019-12-28   *
 * * Time: 14:56        *
 * * to: xm             *
 * **********************
 * 线程池配置类
 **/
@Configuration
@EnableAsync
public class AsyncTaskConfig {

    //上传线程配置
    @Bean
    public Executor upload() {
        return set(10, 50, 10, 60, "upload-Async-");
    }

    /**
     * 设置
     *
     * @param corePoolSize            核心线程数
     * @param maxPoolSize             最大线程数
     * @param queueCapacity           线程池所使用的缓冲队列
     * @param awaitTerminationSeconds 等待时间 （默认为0，此时立即停止），并没等待xx秒后强制停止
     * @param threadNamePrefix        线程名称前缀
     * @return Executor
     */
    private Executor set(int corePoolSize, int maxPoolSize, int queueCapacity, int awaitTerminationSeconds, String threadNamePrefix) {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        //设置核心线程数
        threadPool.setCorePoolSize(corePoolSize);
        //设置最大线程数
        threadPool.setMaxPoolSize(maxPoolSize);
        //线程池所使用的缓冲队列
        threadPool.setQueueCapacity(queueCapacity);
        //等待任务在关机时完成--表明等待所有线程执行完
        threadPool.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间 （默认为0，此时立即停止），并没等待xx秒后强制停止
        threadPool.setAwaitTerminationSeconds(awaitTerminationSeconds);
        // 线程名称前缀
        threadPool.setThreadNamePrefix(threadNamePrefix);
        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化线程
        threadPool.initialize();
        return threadPool;
    }
}
