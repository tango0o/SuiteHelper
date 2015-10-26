package com.tango.auto.utils;

import java.io.File;
import java.io.IOException;

public class TestMainArgs {

    public static void main(String[] args) throws Exception {

        new BaseHelper();
        // String x = AnalysisHelper.getFullPackageClassName("ModelingTabDataPersistenceTests");
        
        AssitantHelper.loadCsvFileContent(new File("C:\\index.xls"));
        System.out.println(1);
    }
}
