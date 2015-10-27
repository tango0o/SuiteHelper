package com.tango.auto.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BaseHelper {

    private static Map<String, String>       configKeyValueMap     = null;
    private static Map<String, List<String>> suiteNodeClassNameMap = null;

    public BaseHelper() {
        configKeyValueMap = loadConfiguration("config.properties", new String[] { Constants.Base_Dir_Path, Constants.Beigin_Folder_Name,
                Constants.Max_Folder_Levels, Constants.Extract_File_Path, Constants.Output_Xml_Path, Constants.Filte_Result_Status, 
                Constants.TestNG_Listener, Constants.Is_Parallel, Constants.Is_Single_Instance });
        suiteNodeClassNameMap = new HashMap<String, List<String>>();
    }

/*    public static Map<String, String> getConfigKeyValueMap() {
        return configKeyValueMap;
    }*/
    
    public static String getConfigKeyValue(String key) {
    	return configKeyValueMap.get(key).trim();
    }
    
    public static void setSuiteNodeClassNameMap(Map<String, List<String>> map) {
        suiteNodeClassNameMap = map;
    }

    public static Map<String, List<String>> getSuiteNodeClassNameMap() {
        return suiteNodeClassNameMap;
    }

    private static Map<String, String> loadConfiguration(String configName, String... configKeys) {
        Properties properties = new Properties();
        Map<String, String> configKeyValueMap = new HashMap<String, String>();
        InputStream inputStream = BaseHelper.class.getResourceAsStream("/" + configName);
        try {
            properties.load(inputStream);
            configKeyValueMap.clear();
            for (String key : configKeys) {
                String value = properties.getProperty(key).trim();
                configKeyValueMap.put(key, value);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return configKeyValueMap;
    }
    
    public static void addNodeClassName2Map(String fullClassName, String methodName) {
        List<String> ownMethodNames = new ArrayList<String>();
        if (!suiteNodeClassNameMap.containsKey(fullClassName)) {
            ownMethodNames.add(methodName);
            suiteNodeClassNameMap.put(fullClassName, ownMethodNames);
        } else {
            boolean isDuplicated = false;
            ownMethodNames = suiteNodeClassNameMap.get(fullClassName);
            for (String tmpName : ownMethodNames) {
                if (tmpName.equals(methodName)) {
                    isDuplicated = true;
                    break;
                }
            }
            if (!isDuplicated) ownMethodNames.add(methodName);
        }
    }
}
