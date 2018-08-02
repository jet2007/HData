package com.github.stuxuhai.hdata.plugin.reader.hdfs;

public class HDFSReaderProperties {
	public static final String DIR = "dir";
	public static final String FILENAME_REGEXP = "filename";
	public static final String SCHEMA = "schema";
	public static final String FIELDS_SEPARATOR = "fields.separator";
	public static final String ENCODING = "encoding";
	public static final String HADOOP_USER = "hadoop.user";
	public static final String FILES = "reader.files";
	public static final String HDFS_CONF_PATH = "hdfs.conf.path";
	
	//文本文件中无法使用标准字符串定义null(空指针)，提供nullFormat定义哪些字符串可以表示为null。
	//如果用户配置: nullFormat="\N"，那么如果源头数据是"\N"，视作null字段。
    public static final String NULL_FORMAT = "null.format";
    
    //INCLUDE_COLUMNS只有一个生效
    //优先级：INCLUDE_COLUMNS>EXCLUDE_COLUMNS
    //包含的字段，多个字段用逗号“,”分隔，第1列从数字1开始;如1,2,3；代表只取第1,2,3列；其他列不取
    public static final String INCLUDE_COLUMNS = "include.columns";
    //排除的字段，多个字段用逗号“,”分隔,第1列从数字1开始;如1,2,3；代表不取第1,2,3列；其他列取
    public static final String EXCLUDE_COLUMNS = "exclude.columns";
    
    
    
    public static final String NULL_FORMAT_DEFAULT = "\\N";
    public static final String FIELDS_SEPARATOR_DEFAULT = "\t";
    public static final String ENCODING_DEFAULT = "UTF-8";
	
}
