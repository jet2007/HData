package com.github.stuxuhai.hdata.plugin.utils;

public class FilenameRegexp {

	public static final String MORE_REG = "([_]*)([0-9]*)";
	
	public static String getFilenameRegexp(String path, String filename){
		int dot_loc=filename.lastIndexOf(".");
		String filenameRegexp;
		if(dot_loc>-1){
			String prev=filename.substring(0,  dot_loc);
			String last=filename.substring(dot_loc, filename.length());
			filenameRegexp=prev+MORE_REG+last;
		}
		else{
			filenameRegexp=filename+MORE_REG;
		}
		return filenameRegexp;
	}
}
