package com.github.stuxuhai.hdata.plugin.writer.hbase;

public class HBaseWriterProperties {
	public static final String ZOOKEEPER_QUORUM = "zookeeper.quorum";
	public static final String ZOOKEEPER_PROPERTY_CLIENTPORT = "zookeeper.client.port";
	public static final String TABLE = "table";
	public static final String COLUMNS = "columns";
	public static final String BATCH_INSERT_SIZE = "batch.insert.size";
	public static final String ZOOKEEPER_ZNODE_PARENT = "zookeeper.znode.parent";
	
	//文本文件中无法使用标准字符串定义null(空指针)，提供nullFormat定义哪些字符串可以表示为null。
	//默认值为\null:不会写入hbase列；
	//值为\none,则会写入空字符串值
	//其他传值，如nullFormat="\N"，那么如果源头数据是null字段,则写出数据为"\N"。
	public static final String NULL_FORMAT = "null.format"; 
	
	public static final String NULL_FORMAT_DEFAULT = "\none"; 
}
