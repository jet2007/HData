# hdata  hdfs 说明


------------

## 1 快速介绍

提供了读取写远程hdfs文件系统数据存储的能力。

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

#### 3.1.1  Reader 配置样例

- 样例1：HDFS读取(textfile+无压缩+列分隔符+NULL,使用HIVE的表)-写入到MYSQL

    /app/hdata-0.2.8/bin/hdata --reader hdfs -Rdir="hdfs://127.0.0.1:8020/user/hive/warehouse/elp_demo.db/elp_demo_hdfs__txt" -Rfilename="^[\w\d\-_.]*" -Rschema="id,it,st,dt,dm,fl,de,tx" -Rhdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Rhadoop.user="cloudera" -Rfields.separator="\001" -Rnull.format="\\N" --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target" -Wpresql="truncate table elp_demo.elp_demo_target;"

- 样例2：MYSQL读取-写入到HIVE(ORC)

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_1w" --writer hive -Wmetastore.uris="thrift://127.0.0.1:9083" -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Whadoop.user="cloudera" -Wdatabase="elp_demo" -Wparallelism="1" -Wtable="elp_demo_hdfs_writer_orc"

- 样例3：MYSQL读取-写入到HIVE(rcfile)

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_1w" --writer hive -Wmetastore.uris="thrift://127.0.0.1:9083" -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Whadoop.user="cloudera" -Wdatabase="elp_demo" -Wparallelism="1" -Wtable="elp_demo_hdfs_writer_rcfile" 

- 样例4：MYSQL读取-写入到HIVE(分区[一个分区列])

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_1w" --writer hive -Wmetastore.uris="thrift://127.0.0.1:9083" -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Whadoop.user="cloudera" -Wdatabase="elp_demo" -Wparallelism="1" -Wtable="elp_demo_hdfs_writer_partition" -Wpartitions="day_id=2017-01-02"

- 样例5：MYSQL读取-写入到HIVE(分区[多个分区列])

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_10w" --writer hive -Wmetastore.uris="thrift://127.0.0.1:9083" -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Whadoop.user="cloudera" -Wdatabase="elp_demo" -Wparallelism="1" -Wtable="elp_demo_hdfs_writer_partitions" -Wpartitions="day_id=2017-01-02,prov_id=SH"

### 3.2 Reader参数

参数        | 是否必选   | 描述                    |
-----------| ----- | ---------------------------------------- |
dir|是|HDFS目录路径，如：hdfs://192.168.1.1:8020/user/dir1|
filename|是|文件名，支持正则表达式|
schema|否|输出的字段定义;如id,it,st,dt,dm,fl,de,tx|
fields.separator|否|字段分隔符，默认：\t;例如hive常用值为\001|
encoding|否|文件编码，默认：UTF-8|
hadoop.user|否|具有HDFS读权限的用户名|
hdfs.conf.path|否|hdfs-site.xml配置文件路径|
null.format|否|提供nullFormat定义哪些字符串可以表示为null,如果用户配置: 默认值=\\N，那么如果源头数据是"\N"，视作null字段|


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
path|是|HDFS文件路径，如：hdfs://192.168.1.1:8020/user/1.txt|
fields.separator|否|字段分隔符，默认：\t|
line.separator|否|行分隔符，默认：\n|
encoding|否|文件编码，默认：UTF-8|
compress.codec|否|压缩编码，如：org.apache.hadoop.io.compress.GzipCodec|
hadoop.user|否|具有HDFS写权限的用户名|
max.file.size.mb|否|单个文件最大大小限制（单位：MB）|
partition.date.index|否|日期字段索引值，起始值为0|
partition.date.format|否|日期格式，如：yyyy-MM-dd|
hdfs.conf.path|否|hdfs-site.xml配置文件路径|

* **已修复 bug**

    * 描述：HiveDecimal转换错误
    * 报错：【ERROR com.github.stuxuhai.hdata.core.RecordEventExceptionHandler - java.lang.ClassCastException: java.math.BigDecimal cannot be cast to org.apache.hadoop.hive.common.type.HiveDecimal】

* **未修复 bug**

    * 描述：float,double转换报错
    * 报错：【ERROR com.github.stuxuhai.hdata.core.RecordEventExceptionHandler - java.lang.ClassCastException: java.lang.Float cannot be cast to java.lang.Double】(注：源mysql.float字段-->hive.double)】
    * 处理方法：将目标字段改成float类型; 
 


## 4 FAQ

略

