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
	public static final String COMPRESS = "compress";//gzip,bzip2,"blank"
	public static final String PROTOCOL = "protocol";//ftp,sftp
	public static final String WRITEMODE = "writemode";//
//	FtpWriter写入前数据清理处理模式：示例，当path=/upload/cust.txt时
//			truncate，写入前清理upload目录下cust.txt或cust_NNNN.txt的所有文件。
//			overwrite，以覆盖方式写入前清理upload目录下目标cust.txt或cust_NNNN.txt的文件。
//			insert，写入前不做任何处理,FtpWriter直接使用cust.tx写入,若两次运行，则生成2份数据；慎用。
//			nonConflict，如果upload目录下有cust.txt或cust_NNNN.txt的文件，直接报错。默认值
	public static final String NULLVALUE = "nullvalue";//NULL
}
