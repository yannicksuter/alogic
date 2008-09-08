package ch.archilogic.misc;

public class FileSystem {
	public static String getApplicationPath(String programName) {
		programName += ".jar";

		String pathSep = System.getProperties().getProperty("path.separator");

		String[] classPath = System.getProperties().getProperty("java.class.path").split(pathSep);
		for (String cp : classPath) {
			if (cp.endsWith(programName))
				return (cp.substring(0, (cp.length() - programName.length())));
		}

		String pwd = (System.getProperties().getProperty("user.dir") + System.getProperties().getProperty("file.separator"));
		return (pwd);
	}
}
