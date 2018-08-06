# hdata  Ftp 说明


------------

## 1 快速介绍

提供了读取写远程FTP文件系统数据存储的能力。

## 2 功能与限制

## 3 功能说明


### 3.1 配置样例

- 样例1：ftp读取写入到ftp(bzip2)

    bin/hdata --reader ftp -Rhost="192.168.101.201" -Rport="2121" -Rusername="a" -Rpassword="a" -Rdir="/" -Rfields.separator="," -Rfilename="aaaa.txt" -Rcompress="" -Rprotocol="ftp" --writer ftp -Whost="192.168.101.201" -Wport="2121" -Wusername="a" -Wpassword="a" -Wpath="/qqqqdssq.bz2" -Wfields.separator="," -Wcompress="bzip2" -Wprotocol="ftp" 

- 样例2：ftp读取写入到ftp(truncate写入模式+并发=6)

    bin/hdata --reader ftp -Rhost="192.168.101.201" -Rport="2121" -Rusername="a" -Rpassword="a" -Rdir="/" -Rfields.separator="," -Rfilename="aaaa.txt" -Rcompress="" -Rprotocol="ftp" --writer ftp -Whost="192.168.101.201" -Wport="2121" -Wusername="a" -Wpassword="a" -Wpath="/a12.csv" -Wfields.separator="," -Wprotocol="ftp" -Wwritemode="truncate"  -Wparallelism="6"

- 样例3：sftp读取写入到sftp

    bin/hdata --reader sftp -Rhost="192.168.101.200" -Rport="2222" -Rusername="foo" -Rpassword="pass" -Rdir="/upload/aa" -Rfields.separator="," -Rfilename="test3.csv"     --writer sftp -Whost="192.168.101.200" -Wport="2222" -Wusername="foo" -Wpassword="pass"  -Wfields.separator="," -Wpath="/upload/aa/g22.txt"  

- 样例4：sftp读取写入到sftp(gzip+并发=2)

    bin/hdata --reader sftp -Rhost="192.168.101.200" -Rport="2222" -Rusername="foo" -Rpassword="pass" -Rdir="/upload/aa" -Rfields.separator="," -Rfilename="a.csv"     --writer sftp -Whost="192.168.101.200" -Wport="2222" -Wusername="foo" -Wpassword="pass"  -Wfields.separator="," -Wpath="/upload/aa/a.gz"    -Wcompress="gzip"  -Wparallelism="2"



### 3.2 FtpReader参数

* **protocol**

	* 描述：ftp服务器协议，目前支持传输协议有ftp和sftp。
	* 必选：否
	* 默认值：ftp

* **host**

	* 描述：ftp服务器地址。示例：192.168.1.100
	* 必选：是 
	* 默认值：无 

* **port**
	* 描述：ftp服务器端口。示例：21
	* 必选：否
	* 默认值：21 

* **username**

	* 描述：ftp服务器访问用户名。示例：user_etl
	* 必选：是 
	* 默认值：无 

* **password**

	* 描述：ftp服务器访问密码。 示例：pass_etl
	* 必选：是 
	* 默认值：无 

* **dir**

	* 描述：远程FTP文件系统的目录路径信息，不含文件信息；示例"/","/upload"。
	* 必选：是
	* 默认值：无

* **filename**

	* 描述：dir参数下的文件；支持正则表达式；示例："cust_([0-9]*).dat","cust_20180102.zip"等
	* 必选：是 
	* 默认值：无

* **recursive**

    * 描述：是否查找dir参数的目录下的所有子目录，满足filename文件；示例：false,true
    * 必选：否
    * 默认值：false

* **fields.separator**

	* 描述：读取的字段分隔符
	* 必选：否
	* 默认值：\t

* **compress**

	* 描述：文本压缩类型，默认不填写意味着没有压缩。支持压缩类型为zip、gzip、bzip2
	* 必选：否 
	* 默认值：没有压缩

* **encoding**

	* 描述：读取文件的编码配置。
 	* 必选：否
 	* 默认值：utf-8

* **start.row**

	* 描述：数据起始行数，默认：1
 	* 必选：否
 	* 默认值：1

* **schema**

	* 描述：输出的字段定义；示例"id,name,product,val,remark"
 	* 必选：否 
 	* 默认值：无

* **parallelism**

    * 描述：parallelism为读/写并行度，所有插件均有该参数；切分依据为文件数量；如若在性能速度无大要求，建议设置为1；
    * 必选：否 
    * 默认值：1

* **null.format**

    * 描述：空值写入ftp文件的填充值。
    * 必选：否
    * 默认值：\N

### 3.2 FtpWriter参数

* **protocol**

    * 描述：ftp服务器协议，目前支持传输协议有ftp和sftp。
    * 必选：否
    * 默认值：ftp

* **host**

    * 描述：ftp服务器地址。示例：192.168.1.100
    * 必选：是 
    * 默认值：无 

* **port**
    * 描述：ftp服务器端口。示例：21
    * 必选：否
    * 默认值：21 

* **username**

    * 描述：ftp服务器访问用户名。示例：user_etl
    * 必选：是 
    * 默认值：无 

* **password**

    * 描述：ftp服务器访问密码。 示例：pass_etl
    * 必选：是 
    * 默认值：无 

* **path**

    * 描述：FTP文件系统的文件路径信息，FtpWriter会写入Path单个/多个文件,必须包含有后缀名。示例：/upload/cust.txt
        * 当parallelism=1时，会生成/upload/cust.txt
        * 当parallelism=N(N>=2)时，会生成/upload/cust_0000.txt,/upload/cust_0001.txt,...,/upload/cust_000N.txt等N个文件；
    * 必选：是
    * 默认值：无
 
* **fields.separator**

    * 描述：读取的字段分隔符
    * 必选：否
    * 默认值：\t

* **encoding**

    * 描述：读取文件的编码配置。
    * 必选：否
    * 默认值：utf-8

* **line.separator**

    * 描述：行分隔符
    * 必选：否
    * 默认值：\n

* **compress**

    * 描述：文本压缩类型，默认不填写意味着没有压缩。支持压缩类型为gzip、bzip2，不支持zip。
    * 必选：否 
    * 默认值：没有压缩

* **writemode**
 
    * 描述：FtpWriter写入前数据清理处理模式：示例，当path=/upload/cust.txt时
        * truncate，写入前清理upload目录下cust.txt或cust_NNNN.txt的所有文件。
        * append，写入前不做任何处理,FtpWriter直接使用cust.tx写入,若两次运行，则生成2份数据；慎用。
        * nonConflict，如果upload目录下有cust.txt或cust_NNNN.txt的文件，直接报错。
    * 必选：否
    * 默认值：nonConflict


* **compress**

    * 描述：文本压缩类型，默认不填写意味着没有压缩。支持压缩类型为gzip、bzip2
    * 必选：否 
    * 默认值：无压缩 
    
* **encoding**

    * 描述：读取文件的编码配置。
    * 必选：否 
    * 默认值：utf-8 <br />
 

* **null.format**

    * 描述：空值写入ftp文件的填充值。
    * 必选：否
    * 默认值：\N

* **parallelism**

    * 描述：parallelism为读/写并行度，所有插件均有该参数；切分依据为文件数量；如若在性能速度无大要求，建议设置为1；
    * 必选：否 
    * 默认值：1


## 4 FAQ

略

