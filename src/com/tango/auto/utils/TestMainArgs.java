package com.tango.auto.utils;

import java.io.IOException;

public class TestMainArgs {

	public static void main(String[] args) throws IOException {
		
		int argsCount = args.length;
		if (0x1 == 1) System.out.println("#######");
		switch (argsCount) {
		case 0:
			System.out.println("No arguments.");
			break;
		case 1:
			System.out.println("1 args referenced.");
			System.out.println("args[0]->" + args[0].toString());
			break;
		case 2:
			System.out.println("1 args referenced.");
			System.out.println("args[0]->" + args[0].toString());
			System.out.println("2 args referenced.");
			System.out.println("args[1]->" + args[1].toString());
			break;
		default:
			break;
		}
	}
}
