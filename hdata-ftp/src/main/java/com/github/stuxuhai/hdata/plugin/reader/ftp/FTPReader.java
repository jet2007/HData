package com.github.stuxuhai.hdata.plugin.reader.ftp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream; 

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.stuxuhai.hdata.api.DefaultRecord;
import com.github.stuxuhai.hdata.api.Fields;
import com.github.stuxuhai.hdata.api.JobContext;
import com.github.stuxuhai.hdata.api.OutputFieldsDeclarer;
import com.github.stuxuhai.hdata.api.PluginConfig;
import com.github.stuxuhai.hdata.api.Reader;
import com.github.stuxuhai.hdata.api.Record;
import com.github.stuxuhai.hdata.api.RecordCollector;
import com.github.stuxuhai.hdata.api.Splitter;
import com.github.stuxuhai.hdata.exception.HDataException;
import com.github.stuxuhai.hdata.plugin.utils.ExceptionProperties;
import com.github.stuxuhai.hdata.plugin.utils.FtpUtils;
import com.github.stuxuhai.hdata.plugin.utils.impl.FtpUtilsImpl;
import com.github.stuxuhai.hdata.plugin.utils.impl.SFtpUtilsImpl;

public class FtpReader extends Reader {
	private static final Logger LOGGER = LogManager.getLogger(FtpReader.class);
	
	private Fields fields;
	private String host;
	private int port;
	private String username;
	private String password;
	private String fieldsSeparator;
	private String encoding;
	private int fieldsCount;
	private int startRow;
	private String compress;
	private String protocol;
	
	private List<String> files = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	@Override
	public void prepare(JobContext context, PluginConfig readerConfig) {
		host = readerConfig.getString(FtpReaderProperties.HOST);
		port = readerConfig.getInt(FtpReaderProperties.PORT, 21);
		username = readerConfig.getString(FtpReaderProperties.USERNAME, "anonymous");
		password = readerConfig.getString(FtpReaderProperties.PASSWORD, "");
		fieldsSeparator = StringEscapeUtils
				.unescapeJava(readerConfig.getString(FtpReaderProperties.FIELDS_SEPARATOR, "\t"));
		encoding = readerConfig.getString(FtpReaderProperties.ENCODING, "UTF-8");
		files = (List<String>) readerConfig.get(FtpReaderProperties.FILES);
		fieldsCount = readerConfig.getInt(FtpReaderProperties.FIELDS_COUNT, 0);
		startRow = readerConfig.getInt(FtpReaderProperties.START_ROW, 1);
		compress = readerConfig.getString(FtpReaderProperties.COMPRESS, "");
		protocol = readerConfig.getString(FtpReaderProperties.PROTOCOL, "ftp");

		if (readerConfig.containsKey(FtpReaderProperties.SCHEMA)) {
			fields = new Fields();
			String[] tokens = readerConfig.getString(FtpReaderProperties.SCHEMA).split("\\s*,\\s*");
			for (String field : tokens) {
				fields.add(field);
			}
		}
	}

	@Override
	public void execute(RecordCollector recordCollector) {
		FtpUtils ftp = null;
		try {
			//ftpClient = FTPUtils.getFtpClient(host, port, username, password);
			if(protocol.equals("ftp")){
				ftp=new FtpUtilsImpl();
				ftp.login(host, username, password, port);	
			}else{
				ftp=new SFtpUtilsImpl();
				ftp.login(host, username, password, port);	
			}
			
			
			for (String file : files) {
				InputStream is = ftp.getFileStream(file);
				BufferedReader br = null;
				if (compress.equals("gzip")) {
					GZIPInputStream gzin = new GZIPInputStream(is);
					br = new BufferedReader(new InputStreamReader(gzin, encoding));
				}
				else if (compress.equals("bzip2")) {
					BZip2CompressorInputStream gzin = new BZip2CompressorInputStream(is);
					br = new BufferedReader(new InputStreamReader(gzin, encoding));
				}
				else if (compress.equals("zip")) {
					ZipCycleInputStream zis = new ZipCycleInputStream(is);
					br = new BufferedReader(new InputStreamReader(zis, encoding));
				}
				else {
					br = new BufferedReader(new InputStreamReader(is, encoding));
				}

				String line = null;
				long currentRow = 0;
				while ((line = br.readLine()) != null) {
					currentRow++;
					if (currentRow >= startRow) {
						String[] tokens = StringUtils.splitPreserveAllTokens(line, fieldsSeparator);
						if (tokens.length >= fieldsCount) {
							Record record = new DefaultRecord(tokens.length);
							for (String field : tokens) {
								record.add(field);
							}
							recordCollector.send(record);
						}
					}
				}
				if(protocol.equals("ftp")){
					ftp.completePendingCommand();
				}
				br.close();
				is.close();
			}
		} catch (Exception e) {
			LOGGER.error(ExceptionProperties.HDATA_FTP_1106 );
			throw new HDataException(e);
		} finally {
			//FTPUtils.closeFtpClient(ftpClient);
			ftp.close();
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(fields);
	}

	@Override
	public Splitter newSplitter() {
		return new FtpSplitter();
	}

}
