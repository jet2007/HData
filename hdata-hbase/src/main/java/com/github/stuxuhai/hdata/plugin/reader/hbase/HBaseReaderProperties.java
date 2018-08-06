package com.github.stuxuhai.hdata.plugin.reader.hbase;

public class HBaseReaderProperties {

	public static final String ZOOKEEPER_QUORUM = "zookeeper.quorum";
	public static final String ZOOKEEPER_PROPERTY_CLIENTPORT = "zookeeper.client.port";
	public static final String TABLE = "table";
	public static final String START_ROWKWY = "start.rowkey";
	public static final String END_ROWKWY = "end.rowkey";
	public static final String COLUMNS = "columns";
	public static final String SCHEMA = "schema";
	public static final String ZOOKEEPER_ZNODE_PARENT = "zookeeper.znode.parent";
	
	//文本文件中无法使用标准字符串定义null(空指针)，提供nullFormat定义哪些字符串可以表示为null。
	//如果用户配置: nullFormat="\N"，那么如果源头数据为"\N"，则视为是null字段。
	//默认值为无，即是不处理
	public static final String NULL_FORMAT = "null.format"; 
}
