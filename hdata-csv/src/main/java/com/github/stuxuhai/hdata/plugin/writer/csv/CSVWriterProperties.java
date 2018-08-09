package com.github.stuxuhai.hdata.plugin.writer.csv;

public class CSVWriterProperties {
    public static final String PATH = "path";
    public static final String ENCODING = "encoding";
    public static final String FIELD_SEPARATOR = "separator";
    public static final String SHOW_COLUMNS = "show.columns";
    public static final String SHOW_TYPES_AND_COMMENTS = "show.types.and.comments";
	public static final String LINE_SEPARATOR = "line.separator"; // \n,\r\n,\r (linux,windows,unix)
	public static final String NULL_FORMAT = "null.format";
	public static final String COMPRESS = "compress";//gzip,bzip2,""(默认值)
	public static final String WRITEMODE = "writemode";//insert,overwrite,append
    public static final String FORMAT = "format"; // 默认值,excel,mysql,tdf,rfc4180  不建议修改，除非需求特殊及了解csvFormat
    
    
	public static final String ENCODING_DEFAULT = "UTF-8";
	public static final String FIELDS_SEPARATOR_DEFAULT = ",";
	public static final String LINE_SEPARATOR_DEFAULT = "\n";
	public static final String COMPRESS_DEFAULT = "";
	public static final String WRITEMODE_DEFAULT = "insert";// 
	public static final String NULL_FORMAT_DEFAULT = "\\N"; 
	public static final boolean SHOW_COLUMNS_DEFAULT = false;
	public static final boolean SHOW_TYPES_AND_COMMENTS_DEFAULT = false;
	
}
