package com.github.stuxuhai.hdata.plugin.writer.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.stuxuhai.hdata.api.Fields;
import com.github.stuxuhai.hdata.api.JobContext;
import com.github.stuxuhai.hdata.api.PluginConfig;
import com.github.stuxuhai.hdata.api.Record;
import com.github.stuxuhai.hdata.api.Writer;
import com.github.stuxuhai.hdata.common.Constants;
import com.github.stuxuhai.hdata.exception.HDataException;
import com.github.stuxuhai.hdata.plugin.jdbc.JdbcUtils;
import com.github.stuxuhai.hdata.utils.EtlTimeAndFieldsHasher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public class JDBCWriter extends Writer {


    private static final Logger LOG = LogManager.getLogger(JDBCWriter.class);

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Constants.DATE_FORMAT_STRING);

    private Connection connection;
    //private Connection conn_init_destroy;
    private PreparedStatement statement;
    private int count;
    private int batchInsertSize;
    private Fields columns;
    private String[] schema;
    private List<String> upsertColumns;
    private String table;
    private String keywordEscaper;
    private String presql;
    private String postsql;
    private int parallelism;
    private Map<String, Integer> columnTypes;
    
    private String url;
    private String driver;
    private String username;
    private String password ;
    
    private String etlTime = null;
    private String fieldsHasher = null;
    
	//标记并发执行时，初始化与destroy的用途；例：写入FTP前的删除原文件；（这一个动作只执行一次）
	private static AtomicInteger init_seq = new AtomicInteger(0);
	private static AtomicInteger destroy_seq = new AtomicInteger(0);

    @Override
    public void prepare(JobContext context, PluginConfig writerConfig) {
        this.keywordEscaper = writerConfig.getProperty(JDBCWriterProperties.KEYWORD_ESCAPER, JDBCWriterProperties.KEYWORD_ESCAPER_DEFAULT);
        this.columns = context.getFields();

        this.etlTime = writerConfig.getString(JDBCWriterProperties.ETL_TIME);
        this.fieldsHasher = writerConfig.getString(JDBCWriterProperties.FIELDS_HASHER);
        
        Fields newColumns = EtlTimeAndFieldsHasher.getColomnsByEtlTimeAndFieldsHasher(etlTime, fieldsHasher, columns);
        System.out.print("######## newColumns=[");
        for (int i = 0; i < newColumns.size(); i++) {
			System.out.print(newColumns.get(i)+",");
		}
        System.out.println("]");
        
        this.table = writerConfig.getString(JDBCWriterProperties.TABLE);
        Preconditions.checkNotNull(table, "JDBC writer required property: table");

        String schemaStr = writerConfig.getString("schema");
        if (StringUtils.isNotBlank(schemaStr)) {
            this.schema = schemaStr.split(",");
        }

        String upsertColumnsStr = writerConfig.getString(JDBCWriterProperties.UPSERT_COLUMNS);
        if (StringUtils.isNotBlank(upsertColumnsStr)) {
            this.upsertColumns = Arrays.asList(upsertColumnsStr.trim().split(","));
        }

        this.batchInsertSize = writerConfig.getInt(JDBCWriterProperties.BATCH_INSERT_SIZE, JDBCWriterProperties.BATCH_INSERT_SIZE_DEFAULT);
        if (batchInsertSize < 1) {
            batchInsertSize = JDBCWriterProperties.BATCH_INSERT_SIZE_DEFAULT;
        }

        prepareConnection(writerConfig);
        
		//并行处理前，执行一次INIT操作（只1次）；
		parallelism = writerConfig.getParallelism();
		
		this.presql = writerConfig.getString(JDBCWriterProperties.PRE_SQL);
		this.postsql = writerConfig.getString(JDBCWriterProperties.POST_SQL);
		//初始化业务
		if(presql!=null && presql.trim().length()>0){
			initNonBiz(parallelism,JDBCWriter.init_seq.getAndIncrement());
		}
        
        try {
            columnTypes = JdbcUtils.getColumnTypes(connection, table, keywordEscaper);
        } catch (Exception e) {
            throw new HDataException(e);
        }

        List<String> insertColumns;
        if (this.schema != null) {
            insertColumns = Arrays.asList(this.schema);
        } else if (this.columns != null) {
            insertColumns = this.columns;
        } else {
            insertColumns = null;
        }
        if (insertColumns != null) {
            prepareStatement(buildInsertSql(table, insertColumns, this.upsertColumns));
        }
    }

    private void prepareConnection(PluginConfig writerConfig) {
        this.url = writerConfig.getString(JDBCWriterProperties.URL);
        Preconditions.checkNotNull(url, "JDBC writer required property: url");
        this.driver = writerConfig.getString(JDBCWriterProperties.DRIVER);
        Preconditions.checkNotNull(driver, "JDBC writer required property: driver");

        this.username = writerConfig.getString(JDBCWriterProperties.USERNAME);
        this.password = writerConfig.getString(JDBCWriterProperties.PASSWORD);
        try {
            connection = JdbcUtils.getConnection(driver, url, username, password);
            connection.setAutoCommit(false);
        } catch (Exception e) {
            throw new HDataException("Failed to init JDBC connection.", e);
        }
    }

    private void prepareStatement(String sql) {
        LOG.debug(sql);
        try {
            statement = connection.prepareStatement(sql);
        } catch (Exception e) {
            throw new HDataException("Failed to prepare statement.", e);
        }
    }

    private String buildInsertSql(String table, List<String> columns, List<String> upsertColumns) {
        String[] placeholder = new String[columns.size()];
        Arrays.fill(placeholder, "?");
        String sql = String.format("INSERT INTO %s(%s) VALUES(%s)",
                table,
                keywordEscaper + Joiner.on(keywordEscaper + ", " + keywordEscaper).join(columns) + keywordEscaper,
                Joiner.on(", ").join(placeholder));
        // TODO: Upsert only support mysql for now
        return appendMysqlUpsertTail(sql, upsertColumns);
    }

    private String appendMysqlUpsertTail(String sql, List<String> upsertColumns) {
        if (upsertColumns == null || upsertColumns.isEmpty()) {
            return sql;
        }
        StringBuilder buf = new StringBuilder(sql);
        buf.append(" ON DUPLICATE KEY UPDATE ");
        for (int i = 0; i < upsertColumns.size(); i++) {
            if (i != 0) {
                buf.append(", ");
            }
            String col = upsertColumns.get(i);
            buf.append(keywordEscaper).append(col).append(keywordEscaper)
                    .append(" = VALUES(")
                    .append(keywordEscaper).append(col).append(keywordEscaper)
                    .append(")");
        }
        return buf.toString();
    }

    private String buildInsertSql(String table, int columnSize, List<String> upsertColumns) {
        String[] placeholder = new String[columnSize];
        Arrays.fill(placeholder, "?");
        String sql = String.format("INSERT INTO %s VALUES(%s)", table, Joiner.on(", ").join(placeholder));
        // TODO: Upsert only support mysql for now
        //LOG.info("----------buildInsertSql:["+ appendMysqlUpsertTail(sql, upsertColumns) +"]");
        return appendMysqlUpsertTail(sql, upsertColumns);
    }

    @Override
    public void execute(Record record) {
    	Object[] objs= new Object[record.size()] ;  
    	for (int i = 0; i < record.size(); i++) {
    		objs[i]=record.get(i);
		}
    	
    	Object[] objsRecord = EtlTimeAndFieldsHasher.getRecordByEtlTimeAndFieldsHasher(etlTime, fieldsHasher, objs);
    	System.out.print("######## objsRecord=[");
    	for (int i = 0; i < objsRecord.length; i++) {
			System.out.print(objsRecord[i]+",");
		}
    	System.out.println("]");
    	
        try {
            if (statement == null) {
                // TODO: statement must be prepared before execution
                prepareStatement(buildInsertSql(table, record.size(), this.upsertColumns));
            }

            for (int i = 0, len = record.size(); i < len; i++) {
                if (record.get(i) instanceof Timestamp && !Integer.valueOf(Types.TIMESTAMP).equals(columnTypes.get(columns.get(i).toLowerCase()))) {
                    statement.setObject(i + 1, DATE_FORMAT.format(record.get(i)));
                } else {
                    statement.setObject(i + 1, record.get(i));
                }
            }

            count++;
            statement.addBatch();

            if (count % batchInsertSize == 0) {
                count = 0;
                statement.executeBatch();
                connection.commit();
            }
        } catch (SQLException e) {
            throw new HDataException(e);
        }
    }

    @Override
    public void close() {
    	
    	//int i=JDBCWriter.destroy_seq.get();
    	//LOG.info("----------close seq=["+i+"] ---------start");
        try {
            if (connection != null && statement != null && count > 0) {
                statement.executeBatch();
                connection.commit();
            }

            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            throw new HDataException(e);
        } finally {
	            DbUtils.closeQuietly(connection);
        	}
        //执行完成之后，进行销毁操作
        if(postsql!=null && postsql.trim().length()>0){
        	destroyNonBiz(parallelism,JDBCWriter.destroy_seq.getAndIncrement());
        }
        //LOG.info("----------close seq=["+i+"] ---------end");
    }
    
    
	/*
	 * 在众多的parallelism线程中；
	 * 1、只能让线程0进行执行biz工作;
	 * 2、非0线程需等线程0执行biz完成之后；
	 */
	public void initNonBiz(int parallelism,int seq){
		int i=init_seq.get();
		//当所有并发数都启动之后，init_seq=parallelism时候
		while(i<=parallelism){
			//只让第n个线程，启动业务
			if(seq==0 && i==parallelism){
					init();  // 业务处理点
					init_seq.getAndIncrement(); // 再加1，让其他线程退出
			}else{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			i=init_seq.get();
		}
	}
	
	/*
	 * INIT-业务，只执行一次 
	 */
	public void init(){
		
		if(presql!=null && presql.trim().length()>0){
			try {
				LOG.info("执行PRESQL=["+presql+"]");
				JdbcUtils.executeSqls(connection, presql);
			} catch (SQLException e) {
				throw new HDataException("HDATA JDBC presql 执行错误", e);
			}	
		}
	}
	
	/*
	 * 销毁操作：前N-1个线程，不执行任何操作；
	 *       第N个线程，等待所有线程执行完成之后；执行销毁操作
	 */
	public void destroyNonBiz(int parall,int seq){
		if(seq==parall-1){
	        Connection conn_destroy = null;
			try {
				conn_destroy = JdbcUtils.getConnection(driver, url, username, password);
	            conn_destroy.setAutoCommit(false);
	            destroy(conn_destroy);
				conn_destroy.commit();
				conn_destroy.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			// do nothing
		}
		
	
	}
	
	/*
	 * 销毁-业务，只执行一次 
	 */
	public void destroy(Connection conn_destroy){
		
		if(postsql!=null && postsql.trim().length()>0){
			try {
				LOG.info("执行POSTSQL=["+postsql+"]");
				JdbcUtils.executeSqls(conn_destroy, postsql);
				conn_destroy.commit();
			} catch (SQLException e) {
				throw new HDataException("HDATA JDBC postsql 执行错误", e);
			}
		}
	}
	
}
