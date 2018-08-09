package com.github.stuxuhai.hdata.plugin.reader.ftp;

public class FtpReaderProperties {
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String DIR = "dir";
	public static final String FILENAME = "filename";
	public static final String RECURSIVE = "recursive";
	public static final String ENCODING = "encoding";
	public static final String FIELDS_SEPARATOR = "fields.separator";
	public static final String SCHEMA = "schema";
	public static final String FIELDS_COUNT = "fields.count";
	public static final String FILES = "reader.files";
	public static final String START_ROW = "start.row";
	public static final String COMPRESS = "compress"; //gzip,zip,bzip2,""
	public static final String PROTOCOL = "protocol"; //ftp,sftp
	public static final String NULL_FORMAT = "null.format"; //ftp,sftp
	public static final String COLUMNS = "columns";
	
	
	public static final String USERNAME_DEFAULT = "anonymous";
	public static final String PASSWORD_DEFAULT = "";
	public static final String COMPRESS_DEFAULT = ""; //gzip,zip,bzip2,""
	public static final String PROTOCOL_DEFAULT = "ftp"; //ftp,sftp
	public static final String ENCODING_DEFAULT = "UTF-8";
	public static final String FIELDS_SEPARATOR_DEFAULT = "\t";
	public static final boolean RECURSIVE_DEFAULT = false;
	public static final int PORT_DEFAULT = 21;
	public static final int START_ROW_DEFAULT = 1;
	public static final int FIELDS_COUNT_DEFAULT = 0;
	public static final String NULL_FORMAT_DEFAULT = "\\N";
}
