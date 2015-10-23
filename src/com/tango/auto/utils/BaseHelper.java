package com.tango.auto.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class BaseHelper {

	private static Map<String, String>			configKeyValueMap		= null;
	private static Map<String, List<String>>	suiteNodeClassNameMap	= null;

	public BaseHelper() {
		configKeyValueMap = loadConfiguration("config.properties",
				new String[] { 
						Constants.Base_Dir_Path, Constants.Beigin_Folder_Name, 
						Constants.Max_Folder_Levels, Constants.Extract_File_Path, Constants.Output_Xml_Path });
		suiteNodeClassNameMap = new HashMap<String, List<String>>();
	}

	public static Map<String, String> getConfigKeyValueMap() {
		return configKeyValueMap;
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
		InputStream inputStream = AssitantHelper.class.getResourceAsStream("/" + configName);
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
}
