# hdata  JDBC 说明


------------

## 1 快速介绍

提供了读写jdbc数据存储的能力。

## 2 功能与限制

## 3 功能说明


### 3.1 配置样例


- 样例1：JDBC读取-表

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_10w"
    

- 样例2：JDBC读取-SQL语句

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rsql="SELECT id, it, st, dt, dm, fl, de, tx FROM elp_demo.elp_demo_1w LIMIT 100" 
    
- 样例2：JDBC写入-表+presql

	--writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target" -Wpresql="truncate table elp_demo.elp_demo_target;"

- 样例4：JDBC写入-etl_time+fields_hasher+schema

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://192.168.101.200:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rsql="select * from elp_demo_10w union all select * from elp_demo_10w order by 1 " --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target_etl_hash" -Wfields.hasher="" -Wetl.time="etl" -Wschema="id,it,st,dt,dm,fl,de,tx,etl_time,fields_hasher" -Wpresql="truncate table elp_demo.elp_demo_target_etl_hash"


### 3.2 FtpReader参数

* jdbc

参数        | 是否必选   | 描述                    |
-----------| ----- | ---------------------------------------- |
driver|是|JDBC驱动类名，如：com.mysql.jdbc.Driver|
url|是|JDBC连接地址，如: jdbc:mysql://localhost:3306/db|
username|是|数据库用户名|
password|是|数据库密码|
table|是|表名（包含数据库名或schema名），如：db.table，也支持分表，例如:table[001-100]|
columns|否|字段名，多个字段用逗号“,”分隔。不填则选取所有字段。|
exclude.columns|否|排除的字段名，多个字段用逗号“,”分隔|
where|否|查询条件，如：day=’20140418’|
sql|否|自定义查询SQL|
split.by|否|并行读取切分的字段|
max.size.per.fetch|否|单次执行SQL获取的最多记录数|
null.string|否|替换当字符串类型的字段值为NULL时的值|
null.non.string|否|替换当非字符串类型的字段值为NULL时的值|
field.wrap.replace.string|否|若字符串字段中存在换行符时需要替换的值|
number.format|否|小数类型字段的输出格式|
keyword.escaper|否|关键字转义字符，默认为\`|

* parallelism：多并发目录只支持mysql,且需要配合split.by共同使用；暂不推荐使用



### 3.3 FtpWriter参数

参数        | 是否必选   | 描述                    |
-----------| ----- | ---------------------------------------- |
driver|是|JDBC驱动类名，如：com.mysql.jdbc.Driver|
url|是|JDBC连接地址，如: jdbc:mysql://localhost:3306/db|
username|是|数据库用户名|
password|是|数据库密码|
table|是|表名（包含数据库名或schema名），如：db.table|
batch.insert.size|否|批量插入的记录数，默认值：10000|
schema|否|字段名配置，一般用于writer和reader的字段名不一致时|
keyword.escaper|否|关键字转义字符，默认为\`|
upsert.columns|否|指定 Upsert 的字段列表，逗号分隔，目前仅支持 Mysql，默认为空(即不启用 upsert)|
presql|否|presql|
postsql|否|postsql|
etl.time|否|含义为增加一列值为系统当前时间；格式为"字段名称:字段位置"；其中字段位置有2种格式（设reader有id,name,val三个字段）；第1种"+N",视为最后一列后添加新列，即是id,name,val,etl_time(N+1); 第2种"N",视为在第N列之前添加 新列，即id,etl_time,name,val(N=2); 若schema有值，应本包含本字段；  1、默认值为空，视为无此列；2、当设为""时，等价于"etl_time:+1"；3、当设值为"etl"时，等价于"etl:+1";|4、示例"etl:2","etl:+2"
fields.hasher|否|含义为增加一列值为reader的行记录的hash值；格式为"字段名称:字段位置"；其中字段位置有2种格式（设reader有id,name,val三个字段）；第1种"+N",视为最后一列后添加新列，即是id,name,val,fields_hasher(N+1); 第2种"N",视为在第N列之前添加 新列，即id,fields_hasher,name,val(N=2); 若schema有值，应本包含本字段； 1、默认值为空，视为无此列；2、当设为""时，等价于"fields_hasher:+1"；3、当设值为"hasher"时，等价于"hasher:+1";|4、示例"hasher:2","hasher:+2"|
 
* parallelism：适当增加并发值，可提高写入速度；


## 4 FAQ

略

