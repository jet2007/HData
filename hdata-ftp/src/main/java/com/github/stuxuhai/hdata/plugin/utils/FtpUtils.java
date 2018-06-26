package com.github.stuxuhai.hdata.plugin.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FtpUtils {
	/**
	 * ftp login
	 * @param host
	 * @param username
	 * @param password
	 * @param port
	 */
	public void login(String host, String username, String password, int port);
	
	/**
	 * ftp close
	 */
	public void close();
	
	/**
	 * 根据路径返回所有符合正则的文件名
	 * @param path
	 * @param filenameRegexp
	 * @param recursive 是否递归查找目录
	 * @return
	 */
	public List<String> listFile(String path, String filenameRegexp,boolean recursive);
	
	/**
	 * 判断是否存在目标文件
	 * @param fullname
	 * @return
	 */
	public  boolean isFileExists(String fullname);
	public  boolean isFileExists(String path, String filename);
	
	/**
	 * 删除目标文件
	 * @param fullname
	 * @return
	 */
	public  boolean deleteFiles(String fullname);
	public  boolean deleteFiles(String path, String filename);
	
	public InputStream getFileStream(String fullname);
	
	public OutputStream getoutputStream(String fullname);
	
	//ftp only
	public void completePendingCommand();
	

}
