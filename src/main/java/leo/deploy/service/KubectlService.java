package leo.deploy.service;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import leo.deploy.utils.CommandUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Leo Li
 */
@Service
public class KubectlService {
	private static final Logger logger = LoggerFactory.getLogger(KubectlService.class);
	private static final String LOCAL_PATH = StringUtils.removeEnd(System.getenv("LOCAL_PATH"), "/");;

	@Async("asyncExecutor")
	public void kubectlApply(String path, CountDownLatch countDownLatch) {
		try {
			CommandUtil.execute("kubectl apply -f " + LOCAL_PATH + "/" + path);
		} catch (Exception e) {
			logger.error("kubectl apply error", e);
		} finally {
			countDownLatch.countDown();
		}
	}

	@Async("asyncExecutor")
	public void kubectlDelete(String path, CountDownLatch countDownLatch) {
		try {
			CommandUtil.execute("kubectl delete -f " + LOCAL_PATH + "/" + path);
		} catch (Exception e) {
			logger.error("kubectl delete error", e);
		} finally {
			countDownLatch.countDown();
		}
	}
}
