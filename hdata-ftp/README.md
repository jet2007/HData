# hdata  Ftp 说明


------------

## 1 快速介绍

提供了读取写远程FTP文件系统数据存储的能力。

## 2 功能与限制

## 3 功能说明


### 3.1 配置样例

- 样例1：FTP读取-单个文件+分隔符+指定NULL字符

    /app/hdata-0.2.8/bin/hdata --reader ftp -Rhost="192.168.101.201" -Rport="2121" -Rusername="a" -Rpassword="a" -Rdir="/reader" -Rfilename="elp_demo_1a.csv" -Rprotocol="ftp" -Rfields.separator="," -Rrecursive="false" -Rnull.format="null" --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target" -Wpresql="truncate table elp_demo.elp_demo_target;"
	附： 指定NULL字符 null.format="null":代表源ftp文件中，若出现null字，则视为空值(默认值为\\N)

- 样例2：FTP读取-多个文件+分隔符+指定NULL字符

    /app/hdata-0.2.8/bin/hdata --reader ftp -Rhost="192.168.101.201" -Rport="2121" -Rusername="a" -Rpassword="a" -Rdir="/reader" -Rfilename="elp_demo_1[\w\d]*.csv" -Rprotocol="ftp" -Rfields.separator="," -Rrecursive="false" -Rnull.format="null" --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target" -Wpresql="truncate table elp_demo.elp_demo_target;"

- 样例3：FTP读取-递归子目录

    /app/hdata-0.2.8/bin/hdata --reader ftp -Rhost="192.168.101.201" -Rport="2121" -Rusername="a" -Rpassword="a" -Rdir="/reader" -Rfilename="elp_demo_1[\w\d]*.csv" -Rprotocol="ftp" -Rfields.separator="," -Rrecursive="true" -Rnull.format="null" --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target" -Wpresql="truncate table elp_demo.elp_demo_target;"
	附：预准备/reader目录的包含有子目录及文件

- 样例4：FTP读取-ZIP压缩

    /app/hdata-0.2.8/bin/hdata --reader ftp -Rhost="192.168.101.201" -Rport="2121" -Rusername="a" -Rpassword="a" -Rdir="/reader" -Rfilename="elp_demo_1[\w\d]*.zip" -Rprotocol="ftp" -Rfields.separator="," -Rcompress="zip" -Rrecursive="true" -Rnull.format="null" --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target"


- 样例4：FTP读取-ZIP压缩

	/app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_10w" --writer ftp -Whost="192.168.101.201" -Wport="2121" -Wusername="a" -Wpassword="a" -Wpath="/writer/elp_demo_target.txt" -Wparallelism="1" -Wprotocol="ftp"
	
- 样例4.2：FTP读取-选取指定列与固定值+定义列名

	/app/hdata-0.2.8/bin/hdata --reader ftp -Rhost="192.168.101.201" -Rport="2121" -Rusername="a" -Rpassword="a" -Rdir="/reader" -Rfilename="elp_demo_1a.csv" -Rprotocol="ftp" -Rfields.separator="," -Rrecursive="false" -Rnull.format="null" -Rcolumns="1,2,3,#abc,#1" -Rschema="a1,a2,a3,a4,a5" --writer csv -Wpath="/tmp/hdata/b/22223.csv" -Wshow.columns="true"


- 样例5：FTP写入-txt文件+分隔符+指定NULL字符

	/app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_10w" --writer ftp -Whost="192.168.101.201" -Wport="2121" -Wusername="a" -Wpassword="a" -Wpath="/writer/elp_demo_target.gz" -Wparallelism="1" -Wprotocol="ftp" -Wwritemode="overwrite" -Wfields.separator="\t" -Wcompress="gzip" 
	
- 样例6：FTP写入-gzip压缩+overwrite写入模式

	/app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_10w" --writer ftp -Whost="192.168.101.201" -Wport="2121" -Wusername="a" -Wpassword="a" -Wpath="/writer/elp_demo_target.gz" -Wparallelism="1" -Wprotocol="ftp" -Wwritemode="overwrite" -Wfields.separator="\t" -Wcompress="gzip"
	
- 样例7：FTP写入-gzip压缩+truncate写入模式+并发数3
	
	/app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_10w" --writer ftp -Whost="192.168.101.201" -Wport="2121" -Wusername="a" -Wpassword="a" -Wpath="/writer/elp_demo_target.gz" -Wparallelism="3" -Wprotocol="ftp" -Wwritemode="truncate" -Wfields.separator="\t" -Wcompress="gzip"	
	- 生成elp_demo_target_0000(0001,0003).gzip 3个文件
	
 
- 样例8：FTP写入-etltime+hash

	/app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://192.168.101.200:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rsql="select * from elp_demo_10w union all select * from elp_demo_10w order by 1 "  --writer ftp -Whost="192.168.101.201" -Wport="2121" -Wusername="a" -Wpassword="a" -Wpath="/qqqqdssq1122.txt" -Wfields.separator=","   -Wprotocol="ftp"  -Wwritemode="overwrite" -Wfields.hasher="" -Wetl.time="etl"   



### 3.2 FtpReader参数

参数        | 是否必选   | 描述                    |
-----------| ----- | ---------------------------------------- |
protocol|否|ftp服务器协议，目前支持传输协议有ftp和sftp; 默认值：ftp|
host|是|FTP连接地址，如：192.168.1.1|
port|否|FTP端口，默认：21|
username|是|用户名|
password|是|密码|
dir|是|远程FTP文件系统的目录路径信息，不含文件信息；示例"/","/upload"。|
filename|是|dir参数下的文件；支持正则表达式；示例："cust_([0-9]*).txt","cust_20180102.zip"等|
recursive|否|是否查找dir参数的目录下的所有子目录，满足filename文件；值false,true，默认：false|
encoding|否|文件编码，默认：UTF-8|
fields.separator|否|字段分隔符，默认：\t|
schema|否|输出的字段定义；示例"id,name,product,val,remark"|
fields.count.filter|否|符合的字段数，不符合则过滤记录,默认值0|
compress|否|文本压缩类型，默认不填写意味着没有压缩。支持压缩类型为zip、gzip、bzip2|
start.row|否|数据起始行数，默认：1|
columns|否|包含的字段，不填视为全部字段；多个字段用逗号分隔，第1列从数字1开始;固定值前加#号;【例如1,3,5,#2018,#abc  代表共取5列值，依次值为第1,3,5列的值和固定值2018,abc】|
parallelism|否|parallelism为读/写并行度，一般根据文件的个数，默认：1|
null.format|否|将ftp文件中的指定值视为null值，默认值：\\\\N|




### 3.3 FtpWriter参数


* ftp

参数        | 是否必选   | 描述                    |
-----------| ----- | ---------------------------------------- |
protocol|否|ftp服务器协议，目前支持传输协议有ftp和sftp; 默认值：ftp|
host|是|FTP连接地址，如：192.168.1.1|
port|否|FTP端口，默认：21|
username|是|用户名|
password|是|密码|
path|是|描述：FTP文件系统的文件路径信息，FtpWriter会写入Path单个/多个文件,必须包含有后缀名。示例：/upload/cust.txt; 1.当parallelism=1时，会生成/upload/cust.txt; 2.当parallelism=N(N>=2)时，会生成/upload/cust_0000.txt,/upload/cust_0001.txt,...,/upload/cust_000N.txt等N个文件；|
encoding|否|文件编码，默认：UTF-8|
fields.separator|否|字段分隔符，默认：\t|
line.separator|否|行分隔符，默认\n|
compress|否|文本压缩类型，默认不填写意味着没有压缩。支持压缩类型为gzip、bzip2，不支持zip。，默认：无|
writemode|否|ftp文件写入方式（示例，当path=/upload/cust.txt时）；1.insert(默认值)，写入前不做任何处理,如果upload目录下有cust.txt（并发=1）或cust_NNNN.txt（并发>1）的文件，则报错； 2.overwrite，以覆盖方式写入前清理upload目录下目标cust.txt或cust_NNNN.txt的文件； 3.truncate，写入前清理upload目录下cust.txt或cust_NNNN.txt的所有文件。|
null.format|否|将null值，写入到ftp文件中的指定值，默认值：\\\\N|
parallelism|否|parallelism为读/写并行度，默认值1|
etl.time|否|含义为增加一列值为系统当前时间；格式为"字段名称:字段位置"；其中字段位置有2种格式（设reader有id,name,val三个字段）；第1种"+N",视为最后一列后添加新列，即是id,name,val,etl_time(N+1); 第2种"N",视为在第N列之前添加 新列，即id,etl_time,name,val(N=2)；  1、默认值为空，视为无此列；2、当设为""时，等价于"etl_time:+1"；3、当设值为"etl"时，等价于"etl:+1";|4、示例"etl:2","etl:+2"
fields.hasher|否|含义为增加一列值为reader的行记录的hash值；格式为"字段名称:字段位置"；其中字段位置有2种格式（设reader有id,name,val三个字段）；第1种"+N",视为最后一列后添加新列，即是id,name,val,fields_hasher(N+1); 第2种"N",视为在第N列之前添加 新列，即id,fields_hasher,name,val(N=2) ； 1、默认值为空，视为无此列；2、当设为""时，等价于"fields_hasher:+1"；3、当设值为"hasher"时，等价于"hasher:+1";|4、示例"hasher:2","hasher:+2"|



## 4 FAQ

略

