package com.tango.auto.utils;

import java.io.File;
import java.io.FileNotFoundException;

public class GenerateSuite {

    public static void main(String[] args) throws Exception {

        if (initHelpDescription(args) == 0) {
            long beginTime = System.currentTimeMillis();

            String extractFilePath = BaseHelper.getConfigKeyValue(Constants.Extract_File_Path);
            File file = new File(extractFilePath).getAbsoluteFile();
            if (!file.exists()) throw new FileNotFoundException(String.format(Constants.Warning_No_File_Format, extractFilePath));
            boolean isSheet = file.getName().endsWith(Constants.File_Extension_CSV) | file.getName().endsWith(Constants.File_Extension_XLS);
            AssitantHelper.executeActions(isSheet);

            long endTime = System.currentTimeMillis();
            System.out.println("Search Class Name Elapsed Time: " + (endTime - beginTime));
            System.out.println("End of generation-Suite.");
        }
    }

    private static int initHelpDescription(String[] args) {
        int argCount = args.length;
        switch (argCount) {
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
        return argCount;
    }
}
