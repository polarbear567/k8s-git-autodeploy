package leo.deploy.config;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import leo.deploy.service.AutoDeployService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author Leo Li
 */
@Component
public class DeployQueue {
	private static Logger logger = LoggerFactory.getLogger(DeployQueue.class);
	private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();

	@Autowired
	private AutoDeployService autoDeployService;
	@Autowired
	@Qualifier("queueExecutor")
	private Executor queueExecutor;

	@PostConstruct
	public void init() {
		queueExecutor.execute(() -> {
			while (true) {
				try {
					//just take from queue
					String body = queue.take();
					optQueue();
				} catch (Exception e) {
					logger.error("queue take error", e);
				}
			}
		});
	}

	public void put(String body) {
		try {
			queue.put(body);
		} catch (Exception e) {
			logger.error("put to queue error", e);
		}
	}

	public void optQueue() {
		autoDeployService.autoDeploy();
	}
}
