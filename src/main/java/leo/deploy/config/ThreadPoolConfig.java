package leo.deploy.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Leo Li
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {

	@Bean
	public Executor asyncExecutor() {
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("kubectl-%d").build();
		return new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), threadFactory);
	}

	@Bean
	public Executor queueExecutor() {
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("queue-%d").build();
		return new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
	}
}
