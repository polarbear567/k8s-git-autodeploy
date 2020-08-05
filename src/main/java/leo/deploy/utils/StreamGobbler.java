package leo.deploy.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Leo Li
 */
public class StreamGobbler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(StreamGobbler.class);

	private InputStream is;
	private String type;

	public StreamGobbler(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if ("Error".equals(type)) {
					log.info("Error:" + line);
				} else {
					log.info("Debug:" + line);
				}
			}
		} catch (Exception e) {
			log.error("exec error", e);
		}
	}
}
