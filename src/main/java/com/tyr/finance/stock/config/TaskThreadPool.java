package com.tyr.finance.stock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class TaskThreadPool {
    @Bean(name ="CommonPool")
    public ThreadPoolTaskExecutor getThreadPool() {
        ThreadPoolTaskExecutor executor =new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(40);
        executor.setMaxPoolSize(40);
        executor.setKeepAliveSeconds(300);
        executor.setThreadNamePrefix("CommonPool<>");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略是又调用任务的线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
