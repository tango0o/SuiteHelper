package com.tango.auto.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalysisHelper extends BaseHelper {

	public static void searchClasFullName(String baseDir, String simpleClassName, String testMethodName) {

		Map<String, List<String>> nodeClassMap = getSuiteNodeClassNameMap();
		String fullClassName = "";
		File tmpFile = new File(baseDir);
		File[] unknowFile = tmpFile.listFiles();
		String beginFolderName = getConfigKeyValueMap().get(Constants.Beigin_Folder_Name);
		int maxFolderLevels = Integer.parseInt(getConfigKeyValueMap().get(Constants.Max_Folder_Levels));

		for (int i = 0; i < unknowFile.length; i++) {
			if (unknowFile[i].isDirectory()) {
				String currentDir = unknowFile[i].getPath();
				searchClasFullName(currentDir, simpleClassName, testMethodName);
			} else {
				String current_file_name = unknowFile[i].getName().replace(".java", "");
				if (current_file_name.equals(simpleClassName)) {
					int index = baseDir.indexOf(beginFolderName + "\\");
					String prefixName = baseDir.substring(index + beginFolderName.length());
					fullClassName = checkReplaceAll(prefixName, maxFolderLevels) + "." + simpleClassName;

					List<String> ownMethodsList = nodeClassMap.get(fullClassName);
					if (ownMethodsList != null) {
						boolean isExist = false;
						for (String mthName : ownMethodsList) {
							if (mthName.equals(testMethodName)) {
								isExist = true;
								break;
							}
						}
						if (!isExist) {
							ownMethodsList.add(testMethodName);
							nodeClassMap.put(fullClassName, ownMethodsList);
						}
					} else {
						List<String> tmpMethodList = new ArrayList<String>();
						tmpMethodList.add(testMethodName);
						nodeClassMap.put(fullClassName, tmpMethodList);
					}
				}
			}
		}
		setSuiteNodeClassNameMap(nodeClassMap);
	}

	private static String checkReplaceAll(String prefixName, int maxChecks) {
		for (int check = 1; check <= maxChecks; check++) {
			if (prefixName.contains("\\\\") | prefixName.contains("\\")) {
				prefixName = prefixName.replace("\\\\", ".").replace("\\", ".").substring(1);
			}
		}
		return prefixName;
	}
}
