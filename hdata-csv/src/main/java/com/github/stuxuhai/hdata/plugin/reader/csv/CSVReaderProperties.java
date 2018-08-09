package com.github.stuxuhai.hdata.plugin.reader.csv;

public class CSVReaderProperties {
	public static final String DIR = "dir";
	public static final String FILEREG = "file";
	public static final String START_ROW = "start.row";
	public static final String ENCODING = "encoding";
	public static final String FORMAT = "format"; // null,excel,mysql,tdf,rfc4180
	
	public static final String FIELDS_SEPARATOR = "fields.separator";
	public static final String LINE_SEPARATOR = "line.separator"; // \n,\r\n,\r (linux,windows,unix)
	public static final String NULL_FORMAT = "null.format";
	public static final String COMPRESS = "compress";//gzip,bzip2,zip,无(默认值)
	public static final String RECURSIVE  = "recursive";//gzip,bzip2,zip,无(默认值)
	public static final String SCHEMA = "schema";
	
	
	public static final String ENCODING_DEFAULT = "UTF-8";
	public static final String FIELDS_SEPARATOR_DEFAULT = ",";
	public static final String LINE_SEPARATOR_DEFAULT = "\n";
	public static final String NULL_FORMAT_DEFAULT = "\\N"; 
	public static final String COMPRESS_DEFAULT = "";
	public static final int START_ROW_DEFAULT = 1;
	public static final String FORMAT_DEFAULT =null;
	public static final boolean RECURSIVE_DEFAULT =false;
	
}
