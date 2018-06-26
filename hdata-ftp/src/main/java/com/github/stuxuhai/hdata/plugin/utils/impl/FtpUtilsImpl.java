package com.github.stuxuhai.hdata.plugin.utils.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.stuxuhai.hdata.exception.HDataException;
import com.github.stuxuhai.hdata.plugin.utils.ExceptionProperties;
import com.github.stuxuhai.hdata.plugin.utils.FilenameRegexp;
import com.github.stuxuhai.hdata.plugin.utils.FtpUtils;
import com.google.common.base.Throwables;


public class FtpUtilsImpl implements FtpUtils {
	private static final Logger LOGGER = LogManager.getLogger(FtpUtilsImpl.class);
	private FTPClient ftp = null;

	
	@Override
	public void login(String host, String username, String password, int port) {
		String LOCAL_CHARSET = "GB18030";
		this.ftp = new FTPClient();
		try {
			this.ftp.connect(host, port);
			// 检测服务器是否支持UTF-8编码，如果支持就用UTF-8编码，否则就使用本地编码GB18030
			if (FTPReply.isPositiveCompletion(this.ftp.sendCommand("OPTS UTF8", "ON"))) {
				LOCAL_CHARSET = "UTF-8";
			}
			this.ftp.setControlEncoding(LOCAL_CHARSET);
			this.ftp.login(username, password);
			this.ftp.setBufferSize(1024 * 1024 * 16);
			this.ftp.enterLocalPassiveMode();
			this.ftp.setFileType(FTP.BINARY_FILE_TYPE);
			this.ftp.setControlKeepAliveTimeout(60);

			int reply = this.ftp.getReplyCode();
			// login失败
            if (!FTPReply.isPositiveCompletion(reply)) {
            	this.ftp.disconnect();
    			LOGGER.error(ExceptionProperties.HDATA_FTP_2001 );
    			throw new HDataException(ExceptionProperties.HDATA_FTP_2001);
            }
		} catch (IOException e) {
			LOGGER.error(ExceptionProperties.HDATA_FTP_2001 );
			throw new HDataException(e);
		}
	}

	@Override
	public void close() {
		if (this.ftp != null) {
			try {
				this.ftp.disconnect();
			} catch (IOException e) {
				LOGGER.error(ExceptionProperties.HDATA_FTP_2002);
				throw new HDataException(e);
			}
		}
	}

	@Override
	public List<String> listFile(String path, String filenameRegexp, boolean recursive) {
		List<String> files = new ArrayList<String>();
		
		try {
			for (FTPFile ftpFile : this.ftp.listFiles(path)) {
				
				if(path.equals("/")){path="";}
				else if(path.length()>1&& path.substring(path.length()-1, path.length()).equals("/")){
					path=path.substring(0,path.length()-1);
				}
				
				//System.out.println("-------"+path+"/"+ftpFile.getName());
				if (ftpFile.isFile()) {
					if (Pattern.matches(filenameRegexp, ftpFile.getName())) {
						files.add(path + "/" + ftpFile.getName());
					}
				} else if (recursive && ftpFile.isDirectory() 
							&& !ftpFile.getName().equals(".")
							&& !ftpFile.getName().equals("..")
						) {
					listFile(path + "/" + ftpFile.getName(), filenameRegexp, recursive);
				}
			}
		} catch (IOException e) {		
			LOGGER.error(ExceptionProperties.HDATA_FTP_2003 );
			throw new HDataException(e);
		}
		
		return files;
	}

	@Override
	public boolean isFileExists(String fullname) {
		int dot_loc=fullname.lastIndexOf("/");
		if(dot_loc>-1){
			String path=fullname.substring(0,  dot_loc);
			String filename=fullname.substring(dot_loc+1, fullname.length());
			return isFileExists(path,filename);
		}
		else{
			return isFileExists("/",fullname);
		}
	}

	@Override
	public boolean isFileExists(String path, String filename) {
		String filenameRegexp=FilenameRegexp.getFilenameRegexp(path,filename);
		try {
			for (FTPFile ftpFile : this.ftp.listFiles(path)) {
				//System.out.println(ftpFile.getName());
				if (ftpFile.isFile()) {
					if (Pattern.matches(filenameRegexp, ftpFile.getName())) {
//						System.out.println("################## isFileExists" );
//						System.out.println("filenameRegexp:"+filenameRegexp );
//						System.out.println("Filename:"+path+"/"+filename );
						return true;
					}
				}  
			}
		} catch (IOException e) {
			LOGGER.error(ExceptionProperties.HDATA_FTP_2004 );
			throw new HDataException(e);
		}
		
		return false;
	}

	@Override
	public boolean deleteFiles(String fullname) {
		int dot_loc=fullname.lastIndexOf("/");
		if(dot_loc>-1){
			String path=fullname.substring(0,  dot_loc);
			String filename=fullname.substring(dot_loc+1, fullname.length());
			return deleteFiles(path,filename);
		}
		else{
			return deleteFiles("/",fullname);
		}
	}

	@Override
	public boolean deleteFiles(String path, String filename) {
		//System.out.println("############################*********:"+path+"/"+filename);
		
		String filenameRegexp=FilenameRegexp.getFilenameRegexp(path,filename);
		try {
			for (FTPFile ftpFile : this.ftp.listFiles(path)) {
				if (ftpFile.isFile()) {
					if (Pattern.matches(filenameRegexp, ftpFile.getName())) {
						//System.out.println("############################*********");
						//System.out.println("############################*********:"+path+"/"+ftpFile.getName());
						//System.out.println("############################*********");
						this.ftp.deleteFile(path+"/"+ftpFile.getName());
					}
				}  
			}
		} catch (IOException e) {
			LOGGER.error(ExceptionProperties.HDATA_FTP_2005 );
			throw new HDataException(e);
			
		}
		return true;
	}
	
	@Override
	public InputStream getFileStream(String fullname) {
		try {
			return this.ftp.retrieveFileStream(fullname);
		} catch (IOException e) {
			LOGGER.error(ExceptionProperties.HDATA_FTP_2006 );
			throw new HDataException(e);
		}
	}
	
	@Override
	public void completePendingCommand() {
		try {
			this.ftp.completePendingCommand();
		} catch (IOException e) {
			LOGGER.error(ExceptionProperties.HDATA_FTP_2007 );
			throw new HDataException(e);
		}
		
	}

	@Override
	public OutputStream getoutputStream(String fullname) {
		try {
			return this.ftp.appendFileStream(fullname);
		} catch (IOException e) {
			LOGGER.error(ExceptionProperties.HDATA_FTP_2008 );
			throw new HDataException(e);
		}
	}
	
	
//	public static void main(String[] args) {
//		FtpUtilsImpl ftp=new FtpUtilsImpl();
//		ftp.login("192.168.101.201", "a", "cc", 2121);
//		System.out.println(ftp.ftp.isAvailable());
//	}
}
