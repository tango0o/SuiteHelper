package com.tango.auto.utils;

import java.io.File;
import java.io.FileNotFoundException;

public class GenerateSuite {

	public static void main(String[] args) throws Exception {

		if (initHelpDescription(args) == 0) {
			String extractFilePath = BaseHelper.getConfigKeyValueMap().get(Constants.Extract_File_Path);
			File file = new File(extractFilePath).getAbsoluteFile();
			if (!file.exists()) throw new FileNotFoundException(String.format(Constants.Warning_No_File_Format, extractFilePath));
			boolean isSheet = file.getName().endsWith(Constants.File_Extension_CSV);
			AssitantHelper.executeActions(isSheet);
		}
	}

	private static int initHelpDescription(String[] args) {
		int arg_count = args.length;
		switch (arg_count) {
		case 0:
			new BaseHelper();
			break;
		case 1:
			if (args[0].toLowerCase().trim().equals("-h") | 
			        args[0].toLowerCase().trim().equals("/h")) System.out.println(Constants.Usage_Description);
			break;
		default:
			System.out.println("Default End.");
			break;
		}
		return arg_count;
	}
}
