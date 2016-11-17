package com.ahew.entando.tools.system.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.ahew.entando.tools.system.exception.ApsSystemException;

public class FileUtils {
	
	public static void closeResource(Closeable res) {
		if (res != null) {
			try {
				res.close();
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, FileUtils.class, "closeResource", "Error while closing the resource");
			}
		}
	}
	
	public static void zipFiles(File[] files, File zipFile, File tmpDirectory) throws ApsSystemException {
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(zipFile));
			byte[] buf = new byte[1024];
			if(files.length>0){
				for (File file : files) {
					String fileName = file.getName();
					FileInputStream in = new FileInputStream(tmpDirectory.getAbsolutePath() + System.getProperty("file.separator") + fileName);
					// add ZIP entry to output stream
					out.putNextEntry(new ZipEntry(fileName));
					// transfer bytes from the file to the ZIP file
					int len;
					while((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					// complete the entry
					out.closeEntry();
					in.close();
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, FileUtils.class, "zipFiles", "Error saving zip file " + zipFile);
			throw new ApsSystemException("Error saving zip file " + zipFile);
		} finally {
			closeResource(out);
		}
	}
	
	public static File createDirectory(String path) {
		File folder = new File(path);
		folder.mkdirs();
		return folder;
	}
	
	public static File createDirectory(File folder) {
		folder.mkdirs();
		return folder;
	}
	
	public static List<String> readFileLines(String file) throws Throwable {
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = null;
		while ((line = reader.readLine())!=null) {
			if (!StringUtils.isBlank(line)) {
				lines.add(line);
			}
		}
		closeResource(reader);
		return lines;
	}
	
	public static void readFileFromUrl(String fileBaseUrl, String fileName, File destFile) throws Exception {
		URL url = new URL(fileBaseUrl + fileName);
		URLConnection connection = url.openConnection();
		
		InputStream input = connection.getInputStream();
		readFileFromInputStream(input, destFile);
		closeResource(input);
	}
	
	public static List<File> extractFilesFromZip(File zipFile, String allowedExt) throws Exception {
		ZipFile zFile = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> entries = zFile.entries();
		List<File> files = new ArrayList<File>();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			String fileName = entry.getName().toLowerCase();
			if (fileName.contains(allowedExt)) {
//			if (fileName.endsWith(allowedExt)) {
				InputStream input = zFile.getInputStream(entry);
				File tmpFile = File.createTempFile("tmp_", fileName);
				readFileFromInputStream(input, tmpFile);
				closeResource(input);
				files.add(tmpFile);
			}
		}
		closeResource(zFile);
		return files;
    }
	
	public static List<File> extractFilesFromZip(File zipFile, String baseFolder, String[] allowedExtensions) throws Exception {
		ZipFile zFile = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> entries = zFile.entries();
		List<File> files = new ArrayList<File>();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			String fileName = entry.getName().toLowerCase();
			String ext = getFileExtension(fileName);
			if (ext!=null && allowedExtensions!=null && allowedExtensions.length>0 && ArrayUtils.contains(allowedExtensions, ext)) {
				InputStream input = zFile.getInputStream(entry);
				File tmpFile = new File(baseFolder + File.separator + fileName);
				readFileFromInputStream(input, tmpFile);
				closeResource(input);
				files.add(tmpFile);
			}
		}
		closeResource(zFile);
		return files;
    }
	
	public static void readFileFromInputStream(InputStream input, File destFile) throws Exception {
		byte[] buffer = new byte[4096];
		int n = - 1;
		
		OutputStream output = new FileOutputStream(destFile);
		while ((n = input.read(buffer)) != -1) {
			output.write(buffer, 0, n);
		}
		closeResource(output);
	}
	
	public static String getFileExtension(String fileName) {
		String ext = null;
		if (fileName!=null) {
			int index = fileName.lastIndexOf(".");
			if (index>=0) {
				ext = fileName.substring(index+1);
			}
		}
		return ext;
	}
	
	public static void writeToFile(String filePath, Consumer<Appendable> consumer) throws Exception {
		writeToFile(new File(filePath), consumer);
	}
	
	public static void writeToFile(File file, Consumer<Appendable> consumer) throws Exception {
		createDirectory(file.getParent());
		PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)), true);
		try {
			consumer.accept(out);
		} catch (Exception e) {
			throw e;
		} finally {
			FileUtils.closeResource(out);
		}
	}
	
	public static void readFromFile(File file, Consumer<BufferedReader> consumer) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		try {
			consumer.accept(reader);
		} catch (Exception e) {
			throw e;
		} finally {
			FileUtils.closeResource(reader);
		}
	}
	
}
