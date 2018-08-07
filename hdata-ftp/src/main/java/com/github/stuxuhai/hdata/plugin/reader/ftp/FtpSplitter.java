package com.github.stuxuhai.hdata.plugin.reader.ftp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.stuxuhai.hdata.api.JobConfig;
import com.github.stuxuhai.hdata.api.PluginConfig;
import com.github.stuxuhai.hdata.api.Splitter;
import com.github.stuxuhai.hdata.exception.HDataException;
import com.github.stuxuhai.hdata.plugin.utils.ExceptionProperties;
import com.github.stuxuhai.hdata.plugin.utils.FtpUtils;
import com.github.stuxuhai.hdata.plugin.utils.impl.FtpUtilsImpl;
import com.github.stuxuhai.hdata.plugin.utils.impl.SFtpUtilsImpl;
import com.google.common.base.Preconditions;

public class FtpSplitter extends Splitter {
	private static final Logger LOGGER = LogManager.getLogger(FtpSplitter.class);

	@Override
	public List<PluginConfig> split(JobConfig jobConfig) {
		List<PluginConfig> list = new ArrayList<PluginConfig>();
		PluginConfig readerConfig = jobConfig.getReaderConfig();
		String host = readerConfig.getString(FtpReaderProperties.HOST);
		Preconditions.checkNotNull(host, "FTP reader required property: host");

		int port = readerConfig.getInt(FtpReaderProperties.PORT, FtpReaderProperties.PORT_DEFAULT);
		String username = readerConfig.getString(FtpReaderProperties.USERNAME, FtpReaderProperties.USERNAME_DEFAULT);
		String password = readerConfig.getString(FtpReaderProperties.PASSWORD, FtpReaderProperties.PASSWORD_DEFAULT);
		String dir = readerConfig.getString(FtpReaderProperties.DIR);
		Preconditions.checkNotNull(dir, "FTP reader required property: dir");

		String filenameRegexp = readerConfig.getString(FtpReaderProperties.FILENAME);
		String protocol = readerConfig.getString(FtpReaderProperties.PROTOCOL,FtpReaderProperties.PROTOCOL_DEFAULT);
		Preconditions.checkNotNull(filenameRegexp, "FTP reader required property: filename");

		boolean recursive = readerConfig.getBoolean(FtpReaderProperties.RECURSIVE, FtpReaderProperties.RECURSIVE_DEFAULT);
		int parallelism = readerConfig.getParallelism();

		FtpUtils ftp = null;
		try {
			
			if(protocol.equals("ftp")){
				ftp=new FtpUtilsImpl();
				ftp.login(host, username, password, port);	
			}else{
				ftp=new SFtpUtilsImpl();
				ftp.login(host, username, password, port);	
			}
			
			List<String> files = ftp.listFile(dir, filenameRegexp, recursive);
			for (String string : files) {
				System.out.println("############split:"+string);
			}
			
			
			if (files.size() > 0) {
				if (parallelism == 1) {
					readerConfig.put(FtpReaderProperties.FILES, files);
					list.add(readerConfig);
				} else {
					double step = (double) files.size() / parallelism;
					for (int i = 0; i < parallelism; i++) {
						List<String> splitedFiles = new ArrayList<String>();
						for (int start = (int) Math.ceil(step * i), end = (int) Math
								.ceil(step * (i + 1)); start < end; start++) {
							splitedFiles.add(files.get(start));
						}
						PluginConfig pluginConfig = (PluginConfig) readerConfig.clone();
						pluginConfig.put(FtpReaderProperties.FILES, splitedFiles);
						list.add(pluginConfig);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(ExceptionProperties.HDATA_FTP_1101 );
			throw new HDataException(e);
		} finally {
			//FTPUtils.closeFtpClient(ftpClient);
			ftp.close();
		}

		return list;
	}

}
