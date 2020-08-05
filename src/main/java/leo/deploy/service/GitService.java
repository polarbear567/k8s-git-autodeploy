package leo.deploy.service;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

/**
 * @author Leo Li
 */
@Service
public class GitService {

	private static final Logger log = LoggerFactory.getLogger(GitService.class);
	private static final String REMOTE_REPO_URL = System.getenv("REMOTE_REPO_URL");
	private static final String BRANCH = System.getenv("BRANCH");
	private static final String LOCAL_PATH = StringUtils.removeEnd(System.getenv("LOCAL_PATH"), "/");
	private static final UsernamePasswordCredentialsProvider PROVIDER = new UsernamePasswordCredentialsProvider(System.getenv("USERNAME"), System.getenv("PASSWORD"));
	private Repository repository;
	private Git localGit;

	@PostConstruct
	public void set() throws Exception {
		repository = new FileRepository(LOCAL_PATH + "/.git");
		localGit = new Git(repository);

		if (!StringUtils.equalsIgnoreCase("true", System.getenv("NEED_PROXY"))) {
			return;
		}
		ProxySelector.setDefault(new ProxySelector() {

			@Override
			public List<Proxy> select(URI uri) {
				return Arrays.asList(new Proxy(Proxy.Type.HTTP, InetSocketAddress
						.createUnresolved(System.getenv("HTTP_PROXY"), Integer.valueOf(System.getenv("HTTP_PORT")))));
			}

			@Override
			public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
				if (uri == null || sa == null || ioe == null) {
					throw new IllegalArgumentException("Arguments can't be null.");
				}
			}
		});
	}

	public void gitClone() {
		try {
			log.info("start to git clone");
			CloneCommand cloneCommand = Git.cloneRepository();
			Git git = cloneCommand.setURI(REMOTE_REPO_URL)
								  .setBranch(BRANCH)
								  .setDirectory(new File(LOCAL_PATH))
								  .setCredentialsProvider(PROVIDER)
								  .call();
			git.close();
		} catch (Exception e) {
			log.error("git clone error", e);
		}
	}

	public void gitPull() {
		try {
			log.info("start to git pull");
			PullResult pullResult = localGit.pull().setRemoteBranchName(BRANCH).setCredentialsProvider(PROVIDER).call();
			log.info("pullRes merge: " + pullResult.getMergeResult().toString());
		} catch (Exception e) {
			log.error("git pull error", e);
		}
	}

	public ObjectId getHeadObjectId() {
		try {
			return repository.resolve("HEAD^{tree}");
		} catch (Exception e) {
			log.error("get head object id error", e);
		}
		return null;
	}

	public Map<String, List<String>> listDiffBetweenNewAndOldHead(ObjectId oldObjectId, ObjectId newObjectId) {
		try {
			ObjectReader reader = repository.newObjectReader();
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			oldTreeIter.reset(reader, oldObjectId);
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, newObjectId);
			List<DiffEntry> diffs= localGit.diff()
									  .setNewTree(newTreeIter)
									  .setOldTree(oldTreeIter)
									  .call();

			Map<String, List<String>> changeMap = new HashMap<>();
			List<String> fileAddAndModifyList = new ArrayList<>();
			List<String> fileDeleteList = new ArrayList<>();

			for (DiffEntry diff : diffs) {
				mergeList(diff, fileAddAndModifyList, fileDeleteList);
			}
			changeMap.put("apply", fileAddAndModifyList);
			changeMap.put("delete", fileDeleteList);

			return changeMap;
		} catch (Exception e) {
			log.error("list diff between new and old head error", e);
		}
		return new HashMap<>();
	}

	private void mergeList(DiffEntry diff, List<String> am, List<String> d) {
		if (diff.getChangeType().equals(ChangeType.ADD) || diff.getChangeType().equals(ChangeType.MODIFY)) {
			am.add(diff.getNewPath());
		}
		if (diff.getChangeType().equals(ChangeType.DELETE)) {
			d.add(diff.getNewPath());
		}
	}
}
