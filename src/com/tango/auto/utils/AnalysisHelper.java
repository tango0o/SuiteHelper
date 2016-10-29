package com.tango.auto.utils;

import java.io.File;

public class AnalysisHelper extends BaseHelper {

    private static String resultPackageClassName = "";

    private static void searchFullClassName(String simpleClassName, String fromDirPath) {
        File tmpFile = new File(fromDirPath);
        File[] unknowFile = tmpFile.listFiles();
        for (int k = 0, U = unknowFile.length; k < U; k++) {
            if (unknowFile[k].isDirectory()) searchFullClassName(simpleClassName, unknowFile[k].getPath());
            else {
                String currentFileName = unknowFile[k].getName().replace(".java", "");
                if (currentFileName.equals(simpleClassName)) {
                    String currentDir = tmpFile.getPath(), beginFolderName = getConfigKeyValue(Constants.Beigin_Folder_Name);
                    int folderLevels = Integer.parseInt(getConfigKeyValue(Constants.Max_Folder_Levels));
                    int index = currentDir.indexOf(beginFolderName + File.separator);
                    String prefixName = currentDir.substring(index + beginFolderName.length());
                    resultPackageClassName = checkReplaceAll(prefixName, folderLevels) + "." + simpleClassName;
                    break;
                }
            }
        }
    }

    private static String checkReplaceAll(String prefixName, int maxChecks) {
        for (int check = 1; check <= maxChecks; check++) {
            if (prefixName.contains(File.separator)) {
                prefixName = prefixName.replace(File.separator, ".").substring(1);
            }
        }
        return prefixName;
    }

    public static String getFullPackageClassName(String simpleClassName) {
        long beginTime = System.currentTimeMillis();
        searchFullClassName(simpleClassName, getConfigKeyValue(Constants.Base_Dir_Path));
        long endTime = System.currentTimeMillis();
        System.out.println("Search Class Name Elapsed Time: " + (endTime - beginTime));
        return resultPackageClassName;
    }
}
