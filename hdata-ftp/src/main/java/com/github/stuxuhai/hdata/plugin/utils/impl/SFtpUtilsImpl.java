package com.github.stuxuhai.hdata.plugin.utils.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.stuxuhai.hdata.exception.HDataException;
import com.github.stuxuhai.hdata.plugin.utils.ExceptionProperties;
import com.github.stuxuhai.hdata.plugin.utils.FtpUtils;
import com.github.stuxuhai.hdata.plugin.writer.ftp.FtpWriterProperties;
import com.google.common.base.Throwables;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class SFtpUtilsImpl implements FtpUtils {
	private static final Logger LOGGER = LogManager.getLogger(SFtpUtilsImpl.class);
	private ChannelSftp sftp = null;
	private Session session = null;

	@Override
	public void login(String host, String username, String password, int port) {
        JSch jsch = new JSch();  
        try {
	        this.session = jsch.getSession(username, host, port);  
	        this.session.setPassword(password);  
	        Properties sshConfig = new Properties();  
	        sshConfig.put("StrictHostKeyChecking", "no");  
	        this.session.setConfig(sshConfig); 
	        //this.session.setTimeout(100);
	        this.session.connect();  
	        this.sftp = (ChannelSftp)this.session.openChannel("sftp");  
	        this.sftp.connect();  
		} catch (JSchException e) {
			LOGGER.error(ExceptionProperties.HDATA_FTP_1001 );
			throw new HDataException(e);
		}  


	}

	@Override
	public void close() {
		if (this.sftp != null  && this.sftp.isConnected()) {
			try {
				this.sftp.disconnect();
				this.sftp.exit();
			}   catch (Exception e) {
				LOGGER.error(ExceptionProperties.HDATA_FTP_1002 );
				throw new HDataException(e);
			}
		}
		
        if (this.session != null) {
            this.session.disconnect();
            this.session = null;
        }

	}

	@Override
	public List<String> listFile(String path, String filenameRegexp, boolean recursive) {
		List<String> files = new ArrayList<String>();
		try {
			@SuppressWarnings("rawtypes")
			Vector allFiles = this.sftp.ls(path);
			
			if(path.equals("/")){path="";}
			else if(path.length()>1&& path.substring(path.length()-1, path.length()).equals("/")){
				path=path.substring(0,path.length()-1);
			}
			
			for (int i = 0; i < allFiles.size(); i++) {
	        	LsEntry ftpFile = (LsEntry) allFiles.get(i);
	        	String strName = ftpFile.getFilename();
	        	//System.out.println("-------"+path+"/"+strName );
	        	if( !ftpFile.getAttrs().isDir() ) { // 文件且满足正则表达式
	        		if (Pattern.matches(filenameRegexp, strName )) {
						files.add(path + "/" + strName );
	        		}
	        	}
	        	// 递归目录
	        	else if(recursive && ftpFile.getAttrs().isDir() && (!strName.equals("."))&& (!strName.equals("..")) ){
	        		files.addAll(listFile( path + "/" + strName , filenameRegexp, recursive));
	        		}
	        	}
		} catch (SftpException e) {
			LOGGER.error(ExceptionProperties.HDATA_FTP_1003 );
			throw new HDataException(e);
		}
		return files;
	}


	
	
	@Override
	public boolean isFileExists(String fullname,int parallelism) {
		int dot_loc=fullname.lastIndexOf("/");
		if(dot_loc>-1){
			String path=fullname.substring(0,  dot_loc);
			String filename=fullname.substring(dot_loc+1, fullname.length());
			return isFileExists(path,filename,  parallelism);
		}
		else{
			return isFileExists("/",fullname,  parallelism);
		}
	}

	@Override
	public boolean isFileExists(String path, String filename,int parallelism) {
		String filenameRegexp=filename;
		if(parallelism==1){
			try {
				@SuppressWarnings("rawtypes")
				Vector allFiles = this.sftp.ls(path);
				for (int i = 0; i < allFiles.size(); i++){
					LsEntry ftpFile = (LsEntry) allFiles.get(i);
					String strName = ftpFile.getFilename();
					if( !ftpFile.getAttrs().isDir() && filenameRegexp.equals(strName) ){
						return  true;
					}
				}
			} catch (SftpException e) {
				LOGGER.error(ExceptionProperties.HDATA_FTP_1004 );
				throw new HDataException(e);
			}
			return false;
		}
		else {
			filenameRegexp=FtpWriterProperties.getFilenameRegexp(filename);
			try {
				@SuppressWarnings("rawtypes")
				Vector allFiles = this.sftp.ls(path);
				for (int i = 0; i < allFiles.size(); i++){
					LsEntry ftpFile = (LsEntry) allFiles.get(i);
					String strName = ftpFile.getFilename();
					if( !ftpFile.getAttrs().isDir() && Pattern.matches(filenameRegexp, strName ) ){
						return  true;
					}
				}
			} catch (SftpException e) {
				LOGGER.error(ExceptionProperties.HDATA_FTP_1004 );
				throw new HDataException(e);
			}
			return false;
		}
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
		String filenameRegexp=FtpWriterProperties.getFilenameRegexp(filename);
		boolean ret=true;
		
		try {
			@SuppressWarnings("rawtypes")
			Vector allFiles = this.sftp.ls(path);
			for (int i = 0; i < allFiles.size(); i++){
				LsEntry ftpFile = (LsEntry) allFiles.get(i);
				String strName = ftpFile.getFilename();
				if( !ftpFile.getAttrs().isDir() && Pattern.matches(filenameRegexp, strName ) ){
					LOGGER.info("删除文件：[ "+path+"/"+strName+"]");
					//System.out.println("delete "+path+"/"+strName);	
					this.sftp.rm(path+"/"+strName);					
				}
			}
		
		} catch (SftpException e) {
			LOGGER.error(ExceptionProperties.HDATA_FTP_1005 );
			throw new HDataException(e);
		}
		
		return ret;
	}

	@Override
	public InputStream getFileStream(String fullname) {
		try {
			return this.sftp.get(fullname);
		} catch (SftpException e) {
			LOGGER.error(ExceptionProperties.HDATA_FTP_1006 );
			throw new HDataException(e);
		}
	}

	@Override
	public void completePendingCommand() {
		// do nothing
	}

	@Override
	public OutputStream getoutputStream(String fullname) {
		return getoutputStream(fullname,"insert");
		
	}
	
	@Override
	public OutputStream getoutputStream(String fullname, String writeMode) {
		try {
			OutputStream outputStream;
			if(writeMode.toLowerCase().equals("overwrite")){
				outputStream = this.sftp.put(fullname,ChannelSftp.OVERWRITE);
				}
			else{
				outputStream = this.sftp.put(fullname,ChannelSftp.APPEND);
			}
			return outputStream;
		} catch (SftpException e) {
			LOGGER.error(ExceptionProperties.HDATA_FTP_1008 );
			throw new HDataException(e);
		} 
	}

}
