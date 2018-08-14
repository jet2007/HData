package com.github.stuxuhai.hdata.plugin.writer.jdbc;

public class JDBCWriterProperties {

    public static final String DRIVER = "driver";
    public static final String URL = "url";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String TABLE = "table";
    public static final String BATCH_INSERT_SIZE = "batch.insert.size";
    public static final String PARALLELISM = "parallelism";
    public static final String SCHEMA = "schema";
    public static final String KEYWORD_ESCAPER = "keyword.escaper";
    public static final String UPSERT_COLUMNS = "upsert.columns";
    public static final String PRE_SQL = "presql";
    public static final String POST_SQL = "postsql";
    
    public static final String ETL_TIME = "etl.time"; //etl_time:1
    public static final String FIELDS_HASHER = "fields.hasher";//hasher:2
    
    
    
    public static final String KEYWORD_ESCAPER_DEFAULT = "";
    public static final int BATCH_INSERT_SIZE_DEFAULT = 10000;

}
