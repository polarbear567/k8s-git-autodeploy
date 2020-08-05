package leo.deploy.utils;

/**
 * @author Leo Li
 */
public class CommandUtil {
	public static void execute(String cmd) throws Exception {
		String[] command = { "/bin/sh", "-c", cmd};
		Process proc = Runtime.getRuntime().exec(command);
		StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "Error");
		StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "Output");
		errorGobbler.start();
		outputGobbler.start();
		proc.waitFor();
	}
}
