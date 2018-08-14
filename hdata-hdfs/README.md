# hdata  hdfs 说明


------------

## 1 快速介绍

提供了读取写远程hdfs文件系统数据存储的能力。

## 2 功能与限制

## 3 功能说明


### 3.1 配置样例

#### 3.1.1  Reader 配置样例

- 样例1：HDFS读取(textfile,无压缩+列分隔符+文件表达式+列映射+表示空值+列分隔)-写入到MYSQL

    /app/hdata-0.2.8/bin/hdata --reader hdfs -Rdir="hdfs://127.0.0.1:8020/user/hive/warehouse/elp_demo.db/elp_demo_hdfs__txt" -Rfilename="^[\w\d\-_.]*" -Rschema="id,it,st,dt,dm,fl,de,tx" -Rhdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Rhadoop.user="cloudera" -Rfields.separator="\001" -Rnull.format="\\N" --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target" -Wpresql="truncate table elp_demo.elp_demo_target;"

- 样例2：HDFS读取(textfile,压缩格式)-写入到MYSQL

    /app/hdata-0.2.8/bin/hdata --reader hdfs -Rdir="hdfs://127.0.0.1:8020/user/hive/warehouse/elp_demo.db/elp_demo_hdfs__txt_gzip" -Rfilename="^[\w\d\-_.]*" -Rschema="id,it,st,dt,dm,fl,de,tx" -Rhdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Rhadoop.user="cloudera" -Rfields.separator="\001" -Rnull.format="\\N" --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target" -Wpresql="truncate table elp_demo.elp_demo_target;"

- 样例3：HDFS读取(orc,压缩格式-->失败)

    /app/hdata-0.2.8/bin/hdata --reader hdfs -Rdir="hdfs://127.0.0.1:8020/user/hive/warehouse/elp_demo.db/elp_demo_hdfs__orc" -Rfilename="^[\w\d\-_.]*" -Rschema="id,it,st,dt,dm,fl,de,tx" -Rhdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Rhadoop.user="cloudera" -Rfields.separator="\001" -Rnull.format="\\N" --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target" -Wpresql="truncate table elp_demo.elp_demo_target;"

- 样例4：HDFS读取(选取指定列)：第3，6，7列使用固定值abc123,1.23,3.14代替；

    /app/hdata-0.2.8/bin/hdata --reader hdfs -Rdir="hdfs://127.0.0.1:8020/user/hive/warehouse/elp_demo.db/elp_demo_hdfs__txt_gzip" -Rfilename="^[\w\d\-_.]*" -Rcolumns="1,2,#abc123,4,5,#1.23,#3.14,8" -Rhdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Rhadoop.user="cloudera" -Rfields.separator="\001" -Rnull.format="\\N" --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target" -Wpresql="truncate table elp_demo.elp_demo_target;"


#### 3.1.2  Writer 配置样例

- 样例1：HDFS写入的目录

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_100w" --writer hdfs -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Wpath="hdfs://127.0.0.1:8020/user/hive/warehouse/elp_demo.db/elp_demo_hdfs__txt/0000_1533275725000.txt" -Wparallelism="1" -Whadoop.user="cloudera"

- 样例2：HDFS写入的目录+列分隔符

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_100w" --writer hdfs -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Wpath="hdfs://127.0.0.1:8020/user/hive/warehouse/elp_demo.db/elp_demo_hdfs__txt/0000_1533275725000.txt" -Wparallelism="1" -Whadoop.user="cloudera" -Wfields.separator="\001"
    
- 样例3：HDFS写入的目录+压缩+单文件最大值

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_100w" --writer hdfs -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Wpath="hdfs://127.0.0.1:8020/user/hive/warehouse/elp_demo.db/elp_demo_hdfs__txt_gzip/0000_1533278874000.gz" -Wparallelism="1" -Whadoop.user="cloudera" -Wfields.separator="\001" -Wcompress.codec="org.apache.hadoop.io.compress.GzipCodec" -Wmax.file.size.mb="10"
    
- 样例4：HDFS写入的目录+etltime+fields_hasher

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://192.168.101.200:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rsql="select * from elp_demo_10w union all select * from elp_demo_10w order by 1 " --writer hdfs -Whdfs.conf.path="/etc/hive/conf/hdfs-site.xml" -Wpath="hdfs://127.0.0.1:8020/user/hive/warehouse/elp_demo.db/elp_demo_hdfs__txt/0000_1533275725000.txt" -Wparallelism="1" -Whadoop.user="cloudera" -Wfields.separator="\001"  -Wfields.hasher="" -Wetl.time="etl"    

### 3.2 Reader参数

参数        | 是否必选   | 描述                    |
-----------| ----- | ---------------------------------------- |
dir|是|HDFS目录路径，如：hdfs://192.168.1.1:8020/user/dir1|
filename|是|文件名，支持正则表达式|
columns|否|包含的字段，不填视为全部字段；多个字段用逗号分隔，第1列从数字1开始;固定值前加#号;【例如1,3,5,#2018,#abc  代表共取5列值，依次值为第1,3,5列的值和固定值2018,abc】|
schema|否|输出的字段定义;如id,it,st,dt,dm,fl,de,tx|
fields.separator|否|字段分隔符，默认：\t;例如hive常用值为\001|
encoding|否|文件编码，默认：UTF-8|
hadoop.user|否|具有HDFS读权限的用户名|
hdfs.conf.path|否|hdfs-site.xml配置文件路径|
null.format|否|提供nullFormat定义哪些字符串可以表示为null,如果用户配置: 默认值=\\N，那么如果源头数据是"\N"，视作null字段|


* **增加功能**
    * 1、选取字段columns
    * 2、表示空值null.format；
 

    

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
null.format|否|提供nullFormat定义哪些字符串可以表示为null,如果用户配置: 默认值=\\N，那么如果源头数据是"\N"，视作null字段|
etl.time|否|含义为增加一列值为系统当前时间；格式为"字段名称:字段位置"；其中字段位置有2种格式（设reader有id,name,val三个字段）；第1种"+N",视为最后一列后添加新列，即是id,name,val,etl_time(N+1); 第2种"N",视为在第N列之前添加 新列，即id,etl_time,name,val(N=2)；  1、默认值为空，视为无此列；2、当设为""时，等价于"etl_time:+1"；3、当设值为"etl"时，等价于"etl:+1";|4、示例"etl:2","etl:+2"
fields.hasher|否|含义为增加一列值为reader的行记录的hash值；格式为"字段名称:字段位置"；其中字段位置有2种格式（设reader有id,name,val三个字段）；第1种"+N",视为最后一列后添加新列，即是id,name,val,fields_hasher(N+1); 第2种"N",视为在第N列之前添加 新列，即id,fields_hasher,name,val(N=2)； 1、默认值为空，视为无此列；2、当设为""时，等价于"fields_hasher:+1"；3、当设值为"hasher"时，等价于"hasher:+1";|4、示例"hasher:2","hasher:+2"|

- max.file.size.mb：参数没有变化情况下，重跑，有可能存在数据不一到情况；
```
       当tgt.max_file_size_mb=10MB时，
             第1次跑10点，错误数据有102mb；会生成11个HDFS文件；
             第2次11点重跑，参数没有变化；修复后正确数据有97mb，会生成10个HDFS文件；第1次生成的第11个HDFS文件还会存在；
```

## 4 FAQ

略

