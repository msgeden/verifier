/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package verifier;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author msgeden
 */
public class FileHandler {

	private static final String configFile = "config.properties";
	public static List<File> retrieveTraceFiles(String fileType) throws IOException {
		File dir = new File(FileHandler.readConfigValue(Constants.TRACE_FOLDER__CONFIG));
		File[] matchingFiles = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().contains(new File(fileType).getName())
						&& pathname.getName().endsWith(".log");
			}
		});
		return Arrays.asList(matchingFiles);
	}
	public static String readConfigValue(String key) {
		Properties prop = new Properties();
		InputStream input;
		try {
			input = new FileInputStream(configFile);
			prop.load(input);
		} catch (IOException e) {
			System.out.println("Cannot read configuration file(s)\n" + e.getMessage());
		}
		return prop.getProperty(key);
	}

	public static void writeConfigValue(String key, String val) {
		Properties prop = new Properties();
		InputStream input;
		OutputStream output;
		try {
			input = new FileInputStream(configFile);
			prop.load(input);
		} catch (IOException e) {
			System.out.println("Cannot read configuration file(s)\n" + e.getMessage());
		}
		prop.setProperty(key, val);
		try {
			output = new FileOutputStream(configFile);
			prop.store(output, "");
		} catch (IOException e) {
			System.out.println("Cannot read configuration file(s)\n" + e.getMessage());
		}
	}

	public static String createDirectory(String path, String dir) {
		File directory = new File(path + dir);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		return directory.getAbsolutePath();
	}

	public static void moveFileToDirectory(String filePath, String destinationPath) throws IOException {
		FileUtils.moveFileToDirectory(new File(filePath), new File(destinationPath), true);
	}

	public static byte[] readFileToByteArray(String filePath) throws IOException {
		return FileUtils.readFileToByteArray(FileUtils.getFile(filePath));
	}

	public static String readFileToString(String filePath) throws IOException {
		return FileUtils.readFileToString(FileUtils.getFile(filePath), Charset.defaultCharset());
	}

	public static String readFileToHexString(String filePath) throws IOException {
		byte[] fileBytes = FileUtils.readFileToByteArray(FileUtils.getFile(filePath));
		StringBuilder sb = new StringBuilder();
		for (byte b : fileBytes) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}

}