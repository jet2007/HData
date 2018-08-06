package com.github.stuxuhai.hdata.plugin.writer.ftp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.stuxuhai.hdata.api.JobContext;
import com.github.stuxuhai.hdata.api.PluginConfig;
import com.github.stuxuhai.hdata.api.Record;
import com.github.stuxuhai.hdata.api.Writer;
import com.github.stuxuhai.hdata.exception.HDataException;
import com.github.stuxuhai.hdata.plugin.utils.ExceptionProperties;
import com.github.stuxuhai.hdata.plugin.utils.FtpUtils;
import com.github.stuxuhai.hdata.plugin.utils.impl.FtpUtilsImpl;
import com.github.stuxuhai.hdata.plugin.utils.impl.SFtpUtilsImpl;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

public class FtpWriter extends Writer {
	
	private static final Logger LOGGER = LogManager.getLogger(FtpWriter.class);
	
	
	private String host;
	private int port;
	private String username;
	private String password;
	private String fieldsSeparator;
	private String lineSeparator;
	private String encoding;
	private String path;
	private String compress;
	private String protocol;
	private String writemode;
	private String nullvalue;
	private int parallelism;
	//private FTPClient ftpClient;
	private FtpUtils ftp;
	private BufferedWriter bw;
	private String[] strArray;
	

	
	private static AtomicInteger sequence = new AtomicInteger(0);
	
	//标记并发执行时，初始化与destroy的用途；例：写入FTP前的删除原文件；（这一个动作只执行一次）
	private static AtomicInteger init_seq = new AtomicInteger(0);
	//private static AtomicInteger destroy_seq = new AtomicInteger(0);
	
	private static final Pattern REG_FILE_PATH_WITHOUT_EXTENSION = Pattern.compile(".*?(?=\\.\\w+$)");
	private static final Pattern REG_FILE_EXTENSION = Pattern.compile("(\\.\\w+)$");

	@Override
	public void prepare(JobContext context, PluginConfig writerConfig) {
		host = writerConfig.getString(FtpWriterProperties.HOST);
		Preconditions.checkNotNull(host, "FTP writer required property: host");

		port = writerConfig.getInt(FtpWriterProperties.PORT, FtpWriterProperties.PORT_DEFAULT);
		username = writerConfig.getString(FtpWriterProperties.USERNAME, FtpWriterProperties.USERNAME_DEFAULT);
		password = writerConfig.getString(FtpWriterProperties.PASSWORD, FtpWriterProperties.PASSWORD_DEFAULT);
		fieldsSeparator = StringEscapeUtils
				.unescapeJava(writerConfig.getString(FtpWriterProperties.FIELDS_SEPARATOR, FtpWriterProperties.FIELDS_SEPARATOR_DEFAULT));
		lineSeparator = StringEscapeUtils
				.unescapeJava(writerConfig.getString(FtpWriterProperties.LINE_SEPARATOR, FtpWriterProperties.LINE_SEPARATOR_DEFAULT));
		encoding = writerConfig.getString(FtpWriterProperties.ENCODING, FtpWriterProperties.ENCODING_DEFAULT);
		path = writerConfig.getString(FtpWriterProperties.PATH);
		Preconditions.checkNotNull(path, "FTP writer required property: path");

		compress = writerConfig.getString(FtpWriterProperties.COMPRESS,FtpWriterProperties.ENCODING_DEFAULT);
		protocol = writerConfig.getString(FtpWriterProperties.PROTOCOL,FtpWriterProperties.PROTOCOL_DEFAULT);
		writemode = writerConfig.getString(FtpWriterProperties.WRITEMODE,FtpWriterProperties.WRITEMODE_DEFAULT);
		nullvalue = StringEscapeUtils
				.unescapeJava(writerConfig.getString(FtpWriterProperties.NULL_FORMAT, FtpWriterProperties.NULL_FORMAT_DEFAULT));

 
		//ftpClient = FTPUtils.getFtpClient(host, port, username, password);
		if(protocol.equals("ftp")){
			ftp=new FtpUtilsImpl();
		}else{
			ftp=new SFtpUtilsImpl();
		}
		ftp.login(host, username, password, port);		
		
		//并行处理前，执行一次INIT操作（只1次）；
		parallelism = writerConfig.getParallelism();
		
		//初始化业务
		initNonBiz(parallelism,init_seq.getAndIncrement());
		
		if (parallelism > 1) {
			String filePathWithoutExtension = "";
			String fileExtension = "";
			Matcher m1 = REG_FILE_PATH_WITHOUT_EXTENSION.matcher(path.trim());
			if (m1.find()) {
				filePathWithoutExtension = m1.group();
			}

			Matcher m2 = REG_FILE_EXTENSION.matcher(path.trim());
			if (m2.find()) {
				fileExtension = m2.group();
			}
			path = String.format("%s_%04d%s", filePathWithoutExtension, sequence.getAndIncrement(), fileExtension);
		}

		try {

			//写入模式
			OutputStream outputStream = ftp.getoutputStream(path,writemode);
			
			if (compress.equals("gzip")) {
				bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(outputStream), encoding));
			}
			else if (compress.equals("bzip2")) {
				bw = new BufferedWriter(new OutputStreamWriter(new BZip2CompressorOutputStream(outputStream), encoding));
			}
			else {
				bw = new BufferedWriter(new OutputStreamWriter(outputStream, encoding));
			}
		} catch (Exception e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
			throw new HDataException(ExceptionProperties.HDATA_FTP_1103);
		}
	}

	@Override
	public void execute(Record record) {
		if (strArray == null) {
			strArray = new String[record.size()];
		}

		for (int i = 0, len = record.size(); i < len; i++) {
			Object o = record.get(i);
			if (o == null) {
				strArray[i] = nullvalue;
			} else {
				strArray[i] = o.toString();
			}
		}
		try {
			bw.write(Joiner.on(fieldsSeparator).join(strArray));
			bw.write(lineSeparator);
		} catch (IOException e) {
			LOGGER.error(Throwables.getStackTraceAsString(e));
			throw new HDataException(ExceptionProperties.HDATA_FTP_1104);
		}
	}

	@Override
	public void close() {
		if (bw != null) {
			try {
				bw.close();
			} catch (IOException e) {
				LOGGER.error(Throwables.getStackTraceAsString(e));
				throw new HDataException(ExceptionProperties.HDATA_FTP_1105);
			}
		}
		//if(FtpWriter.destroy_seq.getAndIncrement()==0)
		//	destroy();
		ftp.close();
		
		
		if(init_seq.get()==parallelism+1)
			LOGGER.info("生成文件:["+path+"]");
	}
	
	/*
	 * 初始化，只执行1次
	 */
	public void init(){
		boolean exists = ftp.isFileExists(path);
		if(writemode.toLowerCase().equals("nonconflict")){
			if(exists){
				LOGGER.error(ExceptionProperties.HDATA_FTP_1102);
				throw new HDataException(ExceptionProperties.HDATA_FTP_1102);
			}
		}
		//写入模式=清除，删除目标文件
		else if (writemode.equals("truncate")){
			if(exists){
				ftp.deleteFiles(path);
			}
		}	
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
			//只让第0个线程，启动业务
			if(seq==0 && i==parallelism){
				init();  // 业务处理点
				init_seq.getAndIncrement(); // 再加1，让其他线程退出
			}else{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			i=init_seq.get();
		}
	}
	
	/*
	 * 销毁-业务，只执行一次 
	 */
	public void destroy(){
		
	}
	
	
	
	
	
}
