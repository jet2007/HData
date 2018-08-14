package com.github.stuxuhai.hdata.plugin.writer.hdfs;

public class HDFSWriterProperties {
	public static final String PATH = "path";
	public static final String FIELDS_SEPARATOR = "fields.separator";
	public static final String LINE_SEPARATOR = "line.separator";
	public static final String ENCODING = "encoding";
	public static final String COMPRESS_CODEC = "compress.codec";
	public static final String HADOOP_USER = "hadoop.user";
	public static final String MAX_FILE_SIZE_MB = "max.file.size.mb";
	public static final String HDFS_CONF_PATH = "hdfs.conf.path";
	public static final String PARTITION_DATE_INDEX = "partition.date.index";
	public static final String PARTITIONED_DATE_FORMAT = "partition.date.format";
	
	//文本文件中无法使用标准字符串定义null(空指针)，提供nullFormat定义哪些字符串可以表示为null。
	//如果用户配置: nullFormat="\N"，那么如果源头数据是null字段,则写出数据为"\N"。
    public static final String NULL_FORMAT = "null.format"; 
    
    public static final String ETL_TIME = "etl.time"; //etl_time:1
    public static final String FIELDS_HASHER = "fields.hasher";//fields_hasher:2
    
    public static final String NULL_FORMAT_DEFAULT = "\\N";
    public static final String LINE_SEPARATOR_DEFAULT = "\n";
    public static final String FIELDS_SEPARATOR_DEFAULT = "\t";
    public static final String ENCODING_DEFAULT = "UTF-8";
    public static final int MAX_FILE_SIZE_MB_DEFAULT = 0;
    public static final int PARTITION_DATE_INDEX_DEFAULT = -1;
    
	
}
