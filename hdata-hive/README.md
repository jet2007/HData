# hdata  HIVE 说明


------------

## 1 快速介绍

提供了读取写远程HIVE文件系统数据存储的能力。

## 2 功能与限制

## 3 功能说明


### 3.1 配置样例

#### 3.1.2  Reader 配置样例
* **不建议使用hiveReader,而使用jdbcReader**
    * 1、hdata原生的hive reader：只能读取整表/整个单分区的数据，分区列不能读取；且不支持QUERYSQL; 
    * 2、不支持多个字段的单分区(BUG)；更不说多个分区；
    
* **jdbcReader读取hive的数据**
    * 将以下的jar拷贝到plugins/jdbc目录下(版本号自己根据CDH等修改)
```
commons-dbutils-1.6.jar           hive-exec-1.1.0-cdh5.7.0.jar       httpclient-4.2.5.jar  
commons-logging-1.1.3.jar         hive-jdbc-1.1.0-cdh5.7.0.jar       httpcore-4.2.5.jar    slf4j-api-1.7.5.jar
hadoop-common-2.6.0-cdh5.7.0.jar  hive-metastore-1.1.0-cdh5.7.0.jar  libfb303-0.9.2.jar    slf4j-log4j12.jar              hive-service-1.1.0-cdh5.7.0.jar    log4j-1.2.17.jar  
```

#### 3.1.2  WRITER 配置样例
- 准备
```
create database elp_demo;
use elp_demo;
drop table if exists  elp_demo_hdfs_writer_txt;
drop table if exists  elp_demo_hdfs_writer_orc;
drop table if exists  elp_demo_hdfs_writer_rcfile;

CREATE TABLE elp_demo_hdfs_writer_txt (
  id int ,
  it int ,
  st string,
  dt string,
  dm string,
  fl float  ,
  de decimal(18,4)  ,
  tx string
)
stored as TEXTFILE    ;



CREATE TABLE elp_demo_hdfs_writer_orc (
  id int ,
  it int ,
  st string,
  dt string,
  dm string,
  fl float  ,
  de decimal(18,4)  ,
  tx string
)
stored as orc    ;

CREATE TABLE elp_demo_hdfs_writer_rcfile (
  id int ,
  it int ,
  st string,
  dt string,
  dm string,
  fl float  ,
  de decimal(18,4)  ,
  tx string
)
stored as rcfile    ;



CREATE TABLE elp_demo_hdfs_writer_partition (
  id int ,
  it int ,
  st string,
  dt string,
  dm string,
  fl float  ,
  de decimal(18,4)  ,
  tx string
)
PARTITIONED BY ( 
  day_id STRING )
stored as orc    ;


CREATE TABLE elp_demo_hdfs_writer_partitions (
  id int ,
  it int ,
  st string,
  dt string,
  dm string,
  fl float  ,
  de decimal(18,4)  ,
  tx string
)
PARTITIONED BY ( 
  day_id STRING
,prov_id STRING  )
stored as rcfile    ;


CREATE TABLE elp_demo_hdfs_writer_etl_hash (
  id int ,
  it int ,
  st string,
  dt string,
  dm string,
  fl float  ,
  de decimal(18,4)  ,
  tx string,
  etl_time string,
  fields_hasher string
);
```

- 样例1：MYSQL读取-写入到HIVE(TEXTFILE)

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_1w" --writer hive -Wmetastore.uris="thrift://127.0.0.1:9083" -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Whadoop.user="cloudera" -Wdatabase="elp_demo" -Wparallelism="1" -Wtable="elp_demo_hdfs_writer_txt"

- 样例2：MYSQL读取-写入到HIVE(ORC)

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_1w" --writer hive -Wmetastore.uris="thrift://127.0.0.1:9083" -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Whadoop.user="cloudera" -Wdatabase="elp_demo" -Wparallelism="1" -Wtable="elp_demo_hdfs_writer_orc"

- 样例3：MYSQL读取-写入到HIVE(rcfile)

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_1w" --writer hive -Wmetastore.uris="thrift://127.0.0.1:9083" -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Whadoop.user="cloudera" -Wdatabase="elp_demo" -Wparallelism="1" -Wtable="elp_demo_hdfs_writer_rcfile" 

- 样例4：MYSQL读取-写入到HIVE(分区[一个分区列])

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_1w" --writer hive -Wmetastore.uris="thrift://127.0.0.1:9083" -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Whadoop.user="cloudera" -Wdatabase="elp_demo" -Wparallelism="1" -Wtable="elp_demo_hdfs_writer_partition" -Wpartitions="day_id=2017-01-02"

- 样例5：MYSQL读取-写入到HIVE(分区[多个分区列])

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_10w" --writer hive -Wmetastore.uris="thrift://127.0.0.1:9083" -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Whadoop.user="cloudera" -Wdatabase="elp_demo" -Wparallelism="1" -Wtable="elp_demo_hdfs_writer_partitions" -Wpartitions="day_id=2017-01-02,prov_id=SH"
    
- 样例6：MYSQL读取-写入到HIVE(etltime+fields_hasher)

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://192.168.101.200:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rsql="select * from elp_demo_10w union all select * from elp_demo_10w order by 1 " --writer hive -Wmetastore.uris="thrift://127.0.0.1:9083" -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Whadoop.user="cloudera" -Wdatabase="elp_demo" -Wparallelism="1" -Wtable="elp_demo_hdfs_writer_etl_hash"  -Wfields.hasher="" -Wetl.time="etl" 

### 3.2 Reader参数

* hive

参数        | 是否必选   | 描述                    |
-----------| ----- | ---------------------------------------- |
metastore.uris|否|Hive Metastore连接地址，如：thrift://localhost:9083, 默认: HiveConf.getVar(ConfVars.METASTOREURIS) 即 `hive-site.xml` 中的 `hive.metastore.uris` |
database|否|数据库名，默认：default|
table|是|表名|
partitions|否|分区，例如: visit_date='2016-07-07'|
hadoop.user|否|具有HDFS读权限的用户名|
hdfs.conf.path|否|hdfs-site.xml配置文件路径|
select.columns|否|选择读取的字段|
convert.null|否|设置值为NULL时对应的字符串，默认："NULL"|


* **不建议使用hiveReader,而使用jdbcReader**
    * 1、hdata原生的hive reader：只能读取整表/整个单分区的数据，分区列不能读取；且不支持QUERYSQL; 
    * 2、不支持多个字段的单分区(BUG)；更不说多个分区；
 
```
操作：需要将hive所依赖的jar拷贝到plugins/jdbc目录下； 
所依赖的jar:
commons-dbutils-1.6.jar           hive-exec-1.1.0-cdh5.7.0.jar       httpclient-4.2.5.jar  
commons-logging-1.1.3.jar         hive-jdbc-1.1.0-cdh5.7.0.jar       httpcore-4.2.5.jar    slf4j-api-1.7.5.jar
hadoop-common-2.6.0-cdh5.7.0.jar  hive-metastore-1.1.0-cdh5.7.0.jar  libfb303-0.9.2.jar    slf4j-log4j12.jar              hive-service-1.1.0-cdh5.7.0.jar    log4j-1.2.17.jar      
```    
    

### 3.2 Writer参数


参数        | 是否必选   | 描述                    |
-----------| ----- | ---------------------------------------- |
metastore.uris|否|Hive Metastore连接地址，如：thrift://localhost:9083(填写域名好像报错，可使用IP), 默认: HiveConf.getVar(ConfVars.METASTOREURIS) 即 `hive-site.xml` 中的 `hive.metastore.uris` |
database|否|数据库名，默认：default|
table|是|表名|
partitions|否|分区条件，如：day='20140418'或 day='20180102',aa='01'|
hadoop.user|否|具有HDFS写权限的用户名|
hdfs.conf.path|否|hdfs-site.xml配置文件路径|
hive.settings|否|hive set语句，目前作用不明显，可无视；分号隔开、如：【set a=1;set b=true;】|
etl.time|否|含义为增加一列值为系统当前时间；格式为"字段名称:字段位置"；其中字段位置有2种格式（设reader有id,name,val三个字段）；第1种"+N",视为最后一列后添加新列，即是id,name,val,etl_time(N+1); 第2种"N",视为在第N列之前添加 新列，即id,etl_time,name,val(N=2)；  1、默认值为空，视为无此列；2、当设为""时，等价于"etl_time:+1"；3、当设值为"etl"时，等价于"etl:+1";|4、示例"etl:2","etl:+2"
fields.hasher|否|含义为增加一列值为reader的行记录的hash值；格式为"字段名称:字段位置"；其中字段位置有2种格式（设reader有id,name,val三个字段）；第1种"+N",视为最后一列后添加新列，即是id,name,val,fields_hasher(N+1); 第2种"N",视为在第N列之前添加 新列，即id,fields_hasher,name,val(N=2)； 1、默认值为空，视为无此列；2、当设为""时，等价于"fields_hasher:+1"；3、当设值为"hasher"时，等价于"hasher:+1";|4、示例"hasher:2","hasher:+2"|

* **已修复 bug**

    * 描述：HiveDecimal转换错误
    * 报错：【ERROR com.github.stuxuhai.hdata.core.RecordEventExceptionHandler - java.lang.ClassCastException: java.math.BigDecimal cannot be cast to org.apache.hadoop.hive.common.type.HiveDecimal】

* **未修复 bug**

    * 描述：float,double转换报错
    * 报错：【ERROR com.github.stuxuhai.hdata.core.RecordEventExceptionHandler - java.lang.ClassCastException: java.lang.Float cannot be cast to java.lang.Double】(注：源mysql.float字段-->hive.double)】
    * 处理方法：将目标字段改成float类型; 
 


## 4 FAQ

略

