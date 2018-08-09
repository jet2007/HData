# hdata  HBASE 说明


------------

## 1 快速介绍

提供了读取写远程HBASE文件系统数据存储的能力。

## 2 功能与限制

## 3 功能说明


### 3.1 配置样例

#### 3.1.1  Reader 配置样例

- 样例1：HBASE读取(读取info族的3列存在的+最后1列不存在的) 

    /app/hdata-0.2.8/bin/hdata --reader hbase -Rzookeeper.quorum="127.0.0.1" -Rzookeeper.client.port="2181" -Rtable="elp_demo_target" -Rschema="st,it,id,fl,xx" -Rcolumns=":rowkey,info:tx,info:de,info:dt,info:xx" --writer console 
```
{1, max_rows=43690, 3916.5058, 2018-06-04, null}
{10, max_rows=5133, 5266.8617, 2018-06-04, null}
{100, , 29.8734, null, null}
{11, max_rows=5133, 145.1509, 2018-06-04, null}
{12, max_rows=9625, 5356.1140, null, null}
```


- 样例2：HBASE读取(把空字符视为空值) 

    /app/hdata-0.2.8/bin/hdata --reader hbase -Rzookeeper.quorum="127.0.0.1" -Rzookeeper.client.port="2181" -Rtable="elp_demo_target" -Rschema="st,it,id,fl,xx" -Rcolumns=":rowkey,info:tx,info:de,info:dt,info:xx" -Rnull.format="" --writer console

```
# 第3行的第2列值
{1, max_rows=43690, 3916.5058, 2018-06-04, null}
{10, max_rows=5133, 5266.8617, 2018-06-04, null}
{100, null, 29.8734, null, null}
{11, max_rows=5133, 145.1509, 2018-06-04, null}
{12, max_rows=9625, 5356.1140, null, null}
```

#### 3.1.2  Writer 配置样例

- 样例1：hbase写入

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rsql="SELECT id, it, st, dt, dm, fl, de, tx FROM elp_demo.elp_demo_1w LIMIT 100" --writer hbase -Wzookeeper.quorum="127.0.0.1" -Wzookeeper.client.port="2181" -Wtable="elp_demo_target" -Wcolumns=":rowkey,info:it,info:st,info:dt,info:dm,info:fl,info:de,info:tx" -Wparallelism="1"

- 样例2：hbase写入(空值写入方式一:不写入)

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rsql="SELECT id, it, st, dt, dm, fl, de, tx FROM elp_demo.elp_demo_1w LIMIT 100" --writer hbase -Wzookeeper.quorum="127.0.0.1" -Wzookeeper.client.port="2181" -Wtable="elp_demo_target" -Wcolumns=":rowkey,info:it,info:st,info:dt,info:dm,info:fl,info:de,info:tx" -Wparallelism="1" -Wnull.format="\null"
    
- 样例3：hbase写入(空值写入方式二：自定义)

    /app/hdata-0.2.8/bin/hdata --reader jdbc -Rurl="jdbc:mysql://db.mysql.hotel.reader.001:3306/elp_demo?useUnicode=true&amp;characterEncoding=utf8" -Rdriver="com.mysql.jdbc.Driver" -Rusername="root" -Rpassword="123456" -Rkeyword.escaper="" -Rparallelism="1" -Rsql="SELECT id, it, st, dt, dm, fl, de, tx FROM elp_demo.elp_demo_1w LIMIT 100" --writer hbase -Wzookeeper.quorum="127.0.0.1" -Wzookeeper.client.port="2181" -Wtable="elp_demo_target" -Wcolumns=":rowkey,info:it,info:st,info:dt,info:dm,info:fl,info:de,info:tx" -Wparallelism="1" -Wnull.format="\N"

### 3.2 Reader参数

* hbase

参数        | 是否必选   | 描述                    |
-----------| ----- | ---------------------------------------- |
zookeeper.quorum|是|Zookeeper连接地址，如：192.168.1.16,192.168.1.17|
zookeeper.client.port|否|Zookeeper客户端端口，默认：2181|
table|是|表名|
start.rowkey|否|Rowkey起始值|
end.rowkey|否|Rowkey结束值|
columns|是|读取的列，如：:rowkey,cf:start_ip,cf:end_ip|
schema|是|输出的字段定义，如：id,start_ip,end_ip|
zookeeper.znode.parent|否|hbase使用的Zookeeper根节点|
null.format|否|文本文件中无法使用标准字符串定义null(空指针)，提供nullFormat定义哪些字符串可以表示为null; 默认值为无，即是不处理; 如果用户配置: nullFormat="\N"，那么如果源头数据为"\N"，则视为是null字段。


 

    

### 3.2 Writer参数

* hbase

参数        | 是否必选   | 描述                    |
-----------| ----- | ---------------------------------------- |
zookeeper.quorum|是|Zookeeper连接地址，如：192.168.1.16,192.168.1.17|
zookeeper.client.port|否|Zookeeper客户端端口，默认：2181|
table|是|表名|
columns|是|列名，如：:rowkey,cf:start_ip|
batch.insert.size|否|批量插入的记录数，默认值：10000|
zookeeper.znode.parent|否|hbase使用的Zookeeper根节点|
null.format|否|若源字段为null值，写入hbase的方式；1、若值为\null时，则不写入hbase；2、若为\none（默认值）时，则写入空字符到hbase;3、其他自定义的值，如\N,则写\N到HBASE字段上。|

- null.format
```
源MYSQL的取值
id	it	st	dt	dm	fl	de	tx
51	1183	INNODB_SYS_TABLES	(null)	(null)	(null)	6535.8918	max_rows=14181

1.null.format取值[\null]，默认值,则HBASE结果(以下面2个结果少了3条结果)
 51                                  column=info:de, timestamp=1533538439738, value=6535.8918                                                
 51                                  column=info:it, timestamp=1533538439738, value=1183                                                     
 51                                  column=info:st, timestamp=1533538439738, value=INNODB_SYS_TABLES                                        
 51                                  column=info:tx, timestamp=1533538439738, value=max_rows=14181        

2.null.format取值[\none]，则HBASE结果
 51                                  column=info:de, timestamp=1533538397665, value=6535.8918                                                
 51                                  column=info:dm, timestamp=1533538397665, value=                                                         
 51                                  column=info:dt, timestamp=1533538397665, value=                                                         
 51                                  column=info:fl, timestamp=1533538397665, value=                                                         
 51                                  column=info:it, timestamp=1533538397665, value=1183                                                     
 51                                  column=info:st, timestamp=1533538397665, value=INNODB_SYS_TABLES                                        
 51                                  column=info:tx, timestamp=1533538397665, value=max_rows=14181           

3.null.format取值自定义[\N]，则HBASE结果(\x5CN为值\N)
 51                                  column=info:de, timestamp=1533537371074, value=6535.8918                                                
 51                                  column=info:dm, timestamp=1533537371074, value=\x5CN                                                    
 51                                  column=info:dt, timestamp=1533537371074, value=\x5CN                                                    
 51                                  column=info:fl, timestamp=1533537371074, value=\x5CN                                                    
 51                                  column=info:it, timestamp=1533537371074, value=1183                                                     
 51                                  column=info:st, timestamp=1533537371074, value=INNODB_SYS_TABLES                                        
 51                                  column=info:tx, timestamp=1533537371074, value=max_rows=14181                                           

```

## 4 FAQ

略

