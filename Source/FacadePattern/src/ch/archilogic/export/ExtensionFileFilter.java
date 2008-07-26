package ch.archilogic.export;

import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class ExtensionFileFilter extends FileFilter {
	public static final int LOAD = 0;
	public static final int SAVE = 1;
	private String description;
	private boolean allowDirectories;
	private Hashtable extensionsTable = new Hashtable();
	private boolean allowAll = false;

	public ExtensionFileFilter(boolean allowDirectories) {
		this.allowDirectories = allowDirectories;
	}

	public ExtensionFileFilter() {
		this(true);
	}

	public static String getFileName(String initialDirectory,
			String description, String extension) {
		String[] extensions = new String[] { extension };
		return (getFileName(initialDirectory, description, extensions, LOAD));
	}

	public static String getFileName(String initialDirectory, String description, String extension, int mode) {
		String[] extensions = new String[] { extension };
		String filename = getFileName(initialDirectory, description,
				extensions, mode);
		if (!filename.endsWith(extension)) {
			filename += ("." + extension);
		}
		return filename;
	}

	public static String getFileName(String initialDirectory,
			String description, String[] extensions) {
		return (getFileName(initialDirectory, description, extensions, LOAD));
	}

	public static String getFileName(String initialDirectory, String description, String[] extensions, int mode) {
		ExtensionFileFilter filter = new ExtensionFileFilter();
		filter.setDescription(description);
		for (int i = 0; i < extensions.length; i++) {
			String extension = extensions[i];
			filter.addExtension(extension, true);
		}
		JFileChooser chooser = new JFileChooser(initialDirectory);
		chooser.setFileFilter(filter);
		int selectVal = (mode == SAVE) ? chooser.showSaveDialog(null) : chooser
				.showOpenDialog(null);
		if (selectVal == JFileChooser.APPROVE_OPTION) {
			String path = chooser.getSelectedFile().getAbsolutePath();
			return (path);
		} else {
			JOptionPane.showMessageDialog(null, "No file selected.");
			return (null);
		}
	}

	public void addExtension(String extension, boolean caseInsensitive) {
		if (caseInsensitive) {
			extension = extension.toLowerCase();
		}
		if (!extensionsTable.containsKey(extension)) {
			extensionsTable.put(extension, new Boolean(caseInsensitive));
			if (extension.equals("*") || extension.equals("*.*")
					|| extension.equals(".*")) {
				allowAll = true;
			}
		}
	}

	public boolean accept(File file) {
		if (file.isDirectory()) {
			return (allowDirectories);
		}
		if (allowAll) {
			return (true);
		}
		String name = file.getName();
		int dotIndex = name.lastIndexOf('.');
		if ((dotIndex == -1) || (dotIndex == name.length() - 1)) {
			return (false);
		}
		String extension = name.substring(dotIndex + 1);
		if (extensionsTable.containsKey(extension)) {
			return (true);
		}
		Enumeration keys = extensionsTable.keys();
		while (keys.hasMoreElements()) {
			String possibleExtension = (String) keys.nextElement();
			Boolean caseFlag = (Boolean) extensionsTable.get(possibleExtension);
			if ((caseFlag != null) && (caseFlag.equals(Boolean.FALSE))
					&& (possibleExtension.equalsIgnoreCase(extension))) {
				return (true);
			}
		}
		return (false);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return (description);
	}
}