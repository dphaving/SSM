package com.tad.util;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

	public static String save(MultipartFile file, String savePath) throws IllegalStateException, IOException {
		if (file != null && file.getSize() > 0) {
			File fileFolder = new File(savePath);
			if (!fileFolder.exists()) {
				fileFolder.mkdirs();
			}
			File saveFile = getFile(savePath, file.getOriginalFilename());
			file.transferTo(saveFile);//把内存中的图片写入磁盘
			return saveFile.getName();
		}
		return null;
	}

	private static File getFile(String savePath, String originalFilename) {
		String fileName = System.currentTimeMillis() + "_" + originalFilename;
		File file = new File(savePath + fileName);
		if (file.exists()) {
			return getFile(savePath, originalFilename);
		}
		return file;
	}

	public static boolean delete(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
			return true;
		}
		return false;
	}

}
