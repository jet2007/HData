# hdata  csv 说明


------------

## 1 快速介绍

提供了读取写本地文件系统数据存储的能力。

## 2 功能与限制

## 3 功能说明


### 3.1 配置样例

- 样例1：TXT写入-字段分隔符+行分隔符+空值指定+编码(全部为默认值)

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_10w" --writer csv -Wpath="/tmp/hdata/elp_demo_target.txt"

- 样例2：TXT写入-字段分隔符+行分隔符+空值指定+编码(全部为默认值,与样例1等价)

    /app/hdata-0.2.8/bin/hdata --reader ftp -Rhost="192.168.101.201" -Rport="2121" -Rusername="a" -Rpassword="a" -Rdir="/reader" -Rfilename="elp_demo_1[\w\d]*.csv" -Rprotocol="ftp" -Rfields.separator="," -Rrecursive="false" -Rnull.format="null" --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target" -Wpresql="truncate table elp_demo.elp_demo_target;"

- 样例3：TXT写入-压缩gzip

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_10w" --writer csv -Wpath="/tmp/hdata/elp_demo_target.gz" -Wparallelism="1" -Wfields.separator="\t" -Wencoding="UTF-8" -Wline.separator="\n" -Wcompress="gzip"

- 样例4：TXT写入-写入方式OVERWRITE（样例1、2、3重跑会报错）

    HDATA_SCRIPTS】=【/app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_10w" --writer csv -Wpath="/tmp/hdata/elp_demo_target.gz" -Wparallelism="1" -Wfields.separator="\t" -Wencoding="UTF-8" -Wline.separator="\n" -Wcompress="gzip" -Wwritemode="overwrite"


- 样例5：TXT写入-打印字段名

	/app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rtable="elp_demo_10w" --writer csv -Wpath="/tmp/hdata/elp_demo_target.txt" -Wparallelism="1" -Wfields.separator="\t" -Wencoding="UTF-8" -Wline.separator="\n" -Wwritemode="overwrite" -Wshow.columns="true"


- 样例5：FTP写入-txt文件+分隔符+指定NULL字符

	/app/hdata-0.2.8/bin/hdata --reader csv -Rdir="/tmp/hdata" -Rfile="elp_demo_targe1.txt" -Rfields.separator="\t" -Rencoding="UTF-8" -Rstart.row="1" -Rline.separator="\n" -Rnull.format="\\N" --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target" -Wpresql="truncate table elp_demo.elp_demo_target;"
	
- 样例6：TXT读取-指定某个文件+指定字段分隔符+其他默认值

	 /app/hdata-0.2.8/bin/hdata --reader csv -Rdir="/tmp/hdata" -Rfile="elp_demo_targe1.txt" -Rfields.separator="\t" -Rencoding="UTF-8" -Rstart.row="1" -Rline.separator="\n" -Rnull.format="\\N" --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target" -Wpresql="truncate table elp_demo.elp_demo_target;"
	 
	
- 样例7：TXT读取-正则匹配多个文件+gzip压缩+选取指定列与固定值
	
	/app/hdata-0.2.8/bin/hdata --reader csv -Rdir="/tmp/hdata" -Rfile="elp_demo_target_[\d]*.gz" -Rcolumns="1,#123,3,4,5,6,7,#abc123cde" -Rfields.separator="\t" -Rencoding="UTF-8" -Rcompress="gzip" -Rstart.row="1" -Rline.separator="\n" -Rnull.format="\\N" --writer jdbc -Wurl="jdbc:mysql://db.mysql.hotel.writer.002:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Wdriver="com.mysql.jdbc.Driver" -Wusername="root" -Wpassword="123456" -Wkeyword.escaper="" -Wparallelism="1" -Wtable="elp_demo_target" -Wpresql="truncate table elp_demo.elp_demo_target;"
	
- 样例8：TXT写入-etltime+fields_hasher
	
	/app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://192.168.101.200:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rsql="select * from elp_demo_10w union all select * from elp_demo_10w order by 1 " --writer csv -Wpath="/tmp/hdata/1111.csv" -Wshow.columns="true" -Wwritemode="overwrite" -Wfields.hasher="" -Wetl.time="etl" 
	


### 3.2 csvReader参数

参数        | 是否必选   | 描述                    |
-----------| ----- | ---------------------------------------- |
dir|是|文件系统的目录路径信息，不含文件信息；示例"/","/upload"。|
file|是|dir参数下的文件；支持正则表达式；示例："cust_([0-9]*).txt","cust_20180102.zip"等|
recursive|否|是否查找dir参数的目录下的所有子目录，满足file文件；值false,true，默认：false|
fields.separator|否|字段分隔符，默认：,|
line.separator|否|行分隔符，默认：\\n|
encoding|否|文件编码，默认：UTF-8|
null.format|否|将ftp文件中的指定值视为null值，默认值：\\\\N|
start.row|否|数据起始行数，默认：1|
columns|否|默认值为无，代表选取所有字段；含义：选取指定字段，多个字段用逗号分隔，[第1列从数字1开始;固定值前加#号]; 例如1,3,5,#2018,#abc，则代表共取5列值，依次值为第1,3,5列的值和固定值2018,abc|
schema|否|输出的字段定义；示例"id,name,product,val,remark"|
compress|否|文本压缩类型，默认不填写意味着没有压缩。支持压缩类型为zip、gzip、bzip2|
format|否|解析csv的csvFormat的格式，不建议修改。无(默认值),excel,mysql,tdf,rfc4180




### 3.3 csvWriter参数

参数        | 是否必选   | 描述                    |
-----------| ----- | ---------------------------------------- |
path|是|描述：文件系统的文件路径信息，FtpWriter会写入Path单个/多个文件,必须包含有后缀名。示例：/upload/cust.txt; 1.当parallelism=1时，会生成/upload/cust.txt; 2.当parallelism=N(N>=2)时，会生成/upload/cust_0000.txt,/upload/cust_0001.txt,...,/upload/cust_000N.txt等N个文件；|
fields.separator|否|字段分隔符，默认：,|
line.separator|否|行分隔符，默认：\\n|
encoding|否|文件编码，默认：UTF-8|
null.format|否|将ftp文件中的指定值视为null值，默认值：\\\\N|
compress|否|文本压缩类型，默认不填写意味着没有压缩。支持压缩类型为gzip、bzip2，不支持zip。，默认：无|
writemode|否|文件写入方式（示例，当path=/upload/cust.txt时）；1.insert(默认值)，写入前不做任何处理,如果upload目录下有cust.txt（并发=1）或cust_NNNN.txt（并发>1）的文件，则报错； 2.overwrite，以覆盖方式写入前清理upload目录下目标cust.txt或cust_NNNN.txt的文件； 3.append，追加写入cust.txt或cust_NNNN.txt。|
format|否|解析csv的csvFormat的格式，不建议修改。无(默认值),excel,mysql,tdf,rfc4180|
show.columns|否|打出字段名|
show.types.and.comments|否|不建议|
parallelism|否|parallelism为读/写并行度，默认值1|
etl.time|否|含义为增加一列值为系统当前时间；格式为"字段名称:字段位置"；其中字段位置有2种格式（设reader有id,name,val三个字段）；第1种"+N",视为最后一列后添加新列，即是id,name,val,etl_time(N+1); 第2种"N",视为在第N列之前添加 新列，即id,etl_time,name,val(N=2);1、默认值为空，视为无此列；2、当设为""时，等价于"etl_time:+1"；3、当设值为"etl"时，等价于"etl:+1";|4、示例"etl:2","etl:+2"
fields.hasher|否|含义为增加一列值为reader的行记录的hash值；格式为"字段名称:字段位置"；其中字段位置有2种格式（设reader有id,name,val三个字段）；第1种"+N",视为最后一列后添加新列，即是id,name,val,fields_hasher(N+1); 第2种"N",视为在第N列之前添加 新列，即id,fields_hasher,name,val(N=2)； 1、默认值为空，视为无此列；2、当设为""时，等价于"fields_hasher:+1"；3、当设值为"hasher"时，等价于"hasher:+1";|4、示例"hasher:2","hasher:+2"|
   


## 4 FAQ

略

