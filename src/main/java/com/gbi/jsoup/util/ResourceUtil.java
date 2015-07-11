package com.gbi.jsoup.util;

import java.io.File;

public class ResourceUtil {
	public static File getSelfFile(Class<?> clazz, String filename) {
		String absFilename = System.getProperty("user.dir") + "/file/" + clazz.getName().replace('.', '/') + "/" + filename;
		return new File(absFilename);
	}
}
