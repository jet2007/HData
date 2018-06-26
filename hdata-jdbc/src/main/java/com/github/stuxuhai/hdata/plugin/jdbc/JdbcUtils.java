package com.github.stuxuhai.hdata.plugin.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

public class JdbcUtils {

    /**
     * 获取表的字段类型
     *
     * @param connection
     * @param table
     * @return
     * @throws SQLException
     */
    public static Map<String, Integer> getColumnTypes(Connection connection, String table, String keywordEscaper) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        sql.append(keywordEscaper);
        sql.append(table);
        sql.append(keywordEscaper);
        sql.append(" WHERE 1=2");
        // sql.append(" Limit 1");

        ResultSetHandler<Map<String, Integer>> handler = new ResultSetHandler<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> handle(ResultSet rs) throws SQLException {
                Map<String, Integer> map = new HashMap<String, Integer>();
                ResultSetMetaData rsd = rs.getMetaData();
                for (int i = 0; i < rsd.getColumnCount(); i++) {
                    map.put(rsd.getColumnName(i + 1).toLowerCase(), rsd.getColumnType(i + 1));
                }
                return map;
            }
        };

        QueryRunner runner = new QueryRunner();
        return runner.query(connection, sql.toString(), handler);
    }

    /**
     * 获取表的字段名称
     *
     * @param conn
     * @param table
     * @return
     * @throws SQLException
     */
    public static List<String> getColumnNames(Connection conn, String table, String keywordEscaper) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        sql.append(keywordEscaper);
        sql.append(table);
        sql.append(keywordEscaper);
        sql.append(" WHERE ( 1=2 )");
        // sql.append(" Limit 1");

        ResultSetHandler<List<String>> handler = new ResultSetHandler<List<String>>() {

            @Override
            public List<String> handle(ResultSet rs) throws SQLException {
                List<String> columnNames = new ArrayList<String>();
                ResultSetMetaData rsd = rs.getMetaData();

                for (int i = 0, len = rsd.getColumnCount(); i < len; i++) {
                    columnNames.add(rsd.getColumnName(i + 1));
                }
                return columnNames;
            }
        };

        QueryRunner runner = new QueryRunner();
        return runner.query(conn, sql.toString(), handler);
    }

    /**
     * 查询表中分割字段值的区域（最大值、最小值）
     *
     * @param conn
     * @param sql
     * @param splitColumn
     * @return
     * @throws SQLException
     */
    public static double[] querySplitColumnRange(Connection conn, final String sql, final String splitColumn) throws SQLException {
        double[] minAndMax = new double[2];
        Pattern p = Pattern.compile("\\s+FROM\\s+.*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);

        if (m.find() && splitColumn != null && !splitColumn.trim().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT MIN(");
            sb.append(splitColumn);
            sb.append("), MAX(");
            sb.append(splitColumn);
            sb.append(")");
            sb.append(m.group(0));

            ResultSetHandler<double[]> handler = new ResultSetHandler<double[]>() {

                @Override
                public double[] handle(ResultSet rs) throws SQLException {
                    double[] minAndMax = new double[2];
                    while (rs.next()) {
                        minAndMax[0] = rs.getDouble(1);
                        minAndMax[1] = rs.getDouble(2);
                    }

                    return minAndMax;
                }
            };

            QueryRunner runner = new QueryRunner();
            return runner.query(conn, sb.toString(), handler);
        }

        return minAndMax;
    }

    /**
     * 查询表数值类型的主键
     *
     * @param conn
     * @param catalog
     * @param schema
     * @param table
     * @return
     * @throws SQLException
     */
    public static String getDigitalPrimaryKey(Connection conn, String catalog, String schema, String table, String keywordEscaper)
            throws SQLException {
        List<String> primaryKeys = new ArrayList<String>();
        ResultSet rs = conn.getMetaData().getPrimaryKeys(catalog, schema, table);
        while (rs.next()) {
            primaryKeys.add(rs.getString("COLUMN_NAME"));
        }
        rs.close();

        if (primaryKeys.size() > 0) {
            Map<String, Integer> map = getColumnTypes(conn, table, keywordEscaper);
            for (String pk : primaryKeys) {
                if (isDigitalType(map.get(pk.toLowerCase()))) {
                    return pk;
                }
            }
        }

        return null;
    }

    /**
     * 判断字段类型是否为数值类型
     *
     * @param sqlType
     * @return
     */
    public static boolean isDigitalType(int sqlType) {
        switch (sqlType) {
        case Types.NUMERIC:
        case Types.DECIMAL:
        case Types.SMALLINT:
        case Types.INTEGER:
        case Types.BIGINT:
        case Types.REAL:
        case Types.FLOAT:
        case Types.DOUBLE:
            return true;

        default:
            return false;
        }
    }

    public static boolean isStringType(int sqlType) {
        switch (sqlType) {
        case Types.CHAR:
        case Types.VARCHAR:
            return true;

        default:
            return false;
        }
    }

    /*
     * 执行sql语句，不返回结果
     */
    public static void executeSqls(Connection connection, String sql) throws SQLException{
    	Statement sm = connection.createStatement();
    	String[] sqls = sql.split(";");
		for (int i = 0; i < sqls.length; i++) {
			if(sqls[i].trim().length()>0)
				sm.addBatch(sqls[i]);
		}
		sm.executeBatch();
		sm.close();
		connection.commit();
		//connection.close();
    	
    }
    
    
    public static Connection getConnection(String driverClassName, String url, String username, String password) throws Exception {
        Class.forName(driverClassName);
        return DriverManager.getConnection(url, username, password);
    }
    
//    
//    public static void main(String[] args) {
//    	try {
//			Connection conn = getConnection("com.mysql.jdbc.Driver","jdbc:mysql://192.168.101.200:3306/test","root","123456");
//			Statement sm = conn.createStatement();
//			String sql1="  UPDATE   tables2 SET  DATA_LENGTH=1+DATA_LENGTH  WHERE AVG_ROW_LENGTH=2179 ;";
//			String sql2="    UPDATE   tables2   SET  INDEX_LENGTH=2+INDEX_LENGTH    WHERE AVG_ROW_LENGTH=2179 ;";
//			
//			String sql=sql1+sql2+sql1+sql2;
//			String[] sqls = sql.split(";");
//			for (int i = 0; i < sqls.length; i++) {
//				if(sqls[i].trim().length()>0)
//					sm.addBatch(sqls[i]);
//			} {
//				
//			}
//			
//			  int[] a = sm.executeBatch();
// 
//			System.out.println(a);
//			sm.close();
//			conn.close();
//    	
//    	} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
    
}
