package leo.deploy;

import java.io.File;

import leo.deploy.service.GitService;
import leo.deploy.utils.CommandUtil;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class K8sGitAutodeployApplication implements InitializingBean {

	private static final String LOCAL_PATH = StringUtils.removeEnd(System.getenv("LOCAL_PATH"), "/");

	@Autowired
	private GitService gitService;

	public static void main(String[] args) {
		SpringApplication.run(K8sGitAutodeployApplication.class, args);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		File file = new File(LOCAL_PATH);
		deleteDir(file);
		if (!file.exists()) {
			file.mkdir();
		}
		gitService.gitClone();
	}

	private static void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String child : children) {
				deleteDir(new File(dir, child));
			}
		}
		dir.delete();
	}
}
