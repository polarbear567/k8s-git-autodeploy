package leo.deploy.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.lib.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Leo Li
 */
@Service
public class AutoDeployService {
	private static final Logger log = LoggerFactory.getLogger(AutoDeployService.class);

	@Autowired
	private GitService gitService;
	@Autowired
	private KubectlService kubectlService;

	public void autoDeploy() {
		try {
			//before pull, get the old head objectId
			ObjectId oldObjectId = gitService.getHeadObjectId();
			//pull first
			gitService.gitPull();
			//get the new head objectId now
			ObjectId newObjectId = gitService.getHeadObjectId();
			//get the diff
			Map<String, List<String>> diffMap = gitService.listDiffBetweenNewAndOldHead(oldObjectId, newObjectId);
			int totalSize = diffMap.get("apply").size() + diffMap.get("delete").size();
			if (totalSize == 0) {
				return;
			}
			log.info("start to deploy");

			CountDownLatch countDownLatch = new CountDownLatch(diffMap.get("apply").size() + diffMap.get("delete").size());
			diffMap.forEach((k, v) -> {
				if (StringUtils.equals(k, "apply")) {
					v.forEach(path -> kubectlService.kubectlApply(path, countDownLatch));
				} else if (StringUtils.equals(k, "delete")) {
					v.forEach(path -> kubectlService.kubectlDelete(path, countDownLatch));
				}
			});

			countDownLatch.await();
			log.info("this stage deploy end");
			//pause 5s
			Thread.sleep(5000);
		} catch (Exception e) {
			log.error("auto deploy error", e);
		}
	}
}
