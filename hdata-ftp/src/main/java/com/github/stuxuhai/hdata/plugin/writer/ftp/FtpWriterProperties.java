package com.github.stuxuhai.hdata.plugin.writer.ftp;

public class FtpWriterProperties {
	public static int pre=0;
	public static int post=0;
	
	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String PATH = "path";
	public static final String ENCODING = "encoding";
	public static final String FIELDS_SEPARATOR = "fields.separator";
	public static final String LINE_SEPARATOR = "line.separator";
	public static final String COMPRESS = "compress";//gzip,bzip2,""
	public static final String PROTOCOL = "protocol";//ftp,sftp
	public static final String WRITEMODE = "writemode";//
//	FtpWriter写入前数据清理处理模式：示例，当path=/upload/cust.txt时
//			truncate，写入前清理upload目录下cust.txt或cust_NNNN.txt的所有文件。
//			overwrite，以覆盖方式写入前清理upload目录下目标cust.txt或cust_NNNN.txt的文件。
//			insert，写入前不做任何处理,FtpWriter直接使用cust.tx写入,若两次运行，则生成2份数据；。
//			nonConflict，如果upload目录下有cust.txt或cust_NNNN.txt的文件，直接报错。默认值
	public static final String NULL_FORMAT = "null.format";//NULL
	
	//删除文件匹配的正则    cust.txt与cust_0001.txt

	
	
	public static final int PORT_DEFAULT = 21;
	public static final String USERNAME_DEFAULT = "anonymous";
	public static final String PASSWORD_DEFAULT = "";
	public static final String ENCODING_DEFAULT = "UTF-8";
	public static final String FIELDS_SEPARATOR_DEFAULT = "\t";
	public static final String LINE_SEPARATOR_DEFAULT = "\n";
	public static final String COMPRESS_DEFAULT = "";//gzip,bzip2,""
	public static final String PROTOCOL_DEFAULT = "ftp";//ftp,sftp
	public static final String WRITEMODE_DEFAULT = "insert";// 
	public static final String NULL_FORMAT_DEFAULT = "\\N";//NULL
	
	
	
	public static final String MORE_REG = "([_]*)([0-9]*)";
	public static String getFilenameRegexp(String filename){
		int dot_loc=filename.lastIndexOf(".");
		String filenameRegexp;
		if(dot_loc>-1){
			String prev=filename.substring(0,  dot_loc);
			String last=filename.substring(dot_loc, filename.length());
			filenameRegexp=prev+FtpWriterProperties.MORE_REG+last;
		}
		else{
			filenameRegexp=filename+FtpWriterProperties.MORE_REG;
		}
		return filenameRegexp;
	}
	
}
