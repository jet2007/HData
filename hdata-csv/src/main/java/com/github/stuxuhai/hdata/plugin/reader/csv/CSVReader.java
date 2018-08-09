package com.github.stuxuhai.hdata.plugin.reader.csv;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

import com.github.stuxuhai.hdata.plugin.FormatConf;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.stuxuhai.hdata.api.DefaultRecord;
import com.github.stuxuhai.hdata.api.JobContext;
import com.github.stuxuhai.hdata.api.PluginConfig;
import com.github.stuxuhai.hdata.api.Reader;
import com.github.stuxuhai.hdata.api.Record;
import com.github.stuxuhai.hdata.api.RecordCollector;
import com.github.stuxuhai.hdata.api.Splitter;
import com.github.stuxuhai.hdata.exception.HDataException;

public class CSVReader extends Reader {
	private static final Logger LOGGER = LogManager.getLogger(CSVReader.class);
	private String path = null;
	private int startRow = 1;
	private String encoding = null;
	private String format;
	private CSVFormat csvFormat = CSVFormat.DEFAULT;
	
	private String compress;
	private String nullFormat;
	private String lineSeparator;
	private String fieldsSeparator;

	@Override
	public void prepare(JobContext context, PluginConfig readerConfig) {
		path = readerConfig.getString(CSVReaderProperties.DIR);
		startRow = readerConfig.getInt(CSVReaderProperties.START_ROW, CSVReaderProperties.START_ROW_DEFAULT);
		encoding = readerConfig.getString(CSVReaderProperties.ENCODING, CSVReaderProperties.ENCODING_DEFAULT);
		format = readerConfig.getString(CSVReaderProperties.FORMAT);
		FormatConf.confCsvFormat(format,csvFormat);
		
		this.compress = readerConfig.getString(CSVReaderProperties.COMPRESS, CSVReaderProperties.COMPRESS_DEFAULT);
		this.nullFormat = readerConfig.getString(CSVReaderProperties.NULL_FORMAT, CSVReaderProperties.NULL_FORMAT_DEFAULT);
		this.lineSeparator = readerConfig.getString(CSVReaderProperties.LINE_SEPARATOR, CSVReaderProperties.LINE_SEPARATOR_DEFAULT);
		fieldsSeparator = StringEscapeUtils.unescapeJava(readerConfig.getString(CSVReaderProperties.FIELDS_SEPARATOR,CSVReaderProperties.FIELDS_SEPARATOR_DEFAULT));
		
		
	}

	@Override
	public void execute(RecordCollector recordCollector) {
		long currentRow = 0;
		try {
			//java.io.Reader in = new InputStreamReader(new FileInputStream(path), encoding);			
			InputStream is = new FileInputStream(path);
			LOGGER.info("开始读取file:"+"["+path+"]");
			java.io.Reader in = null;
			if (compress.equals("gzip")) {
				GZIPInputStream gzin = new GZIPInputStream(is);
				in=new InputStreamReader(gzin, encoding);
			}
			else if (compress.equals("bzip2")) {
				BZip2CompressorInputStream bzin = new BZip2CompressorInputStream(is);
				in= new InputStreamReader(bzin, encoding);
			}
			else if (compress.equals("zip")) {
				ZipCycleInputStream zis = new ZipCycleInputStream(is);
				in =  new InputStreamReader(zis, encoding) ;
			}
			else {
				in =  new InputStreamReader(is, encoding );
			}
			
			Iterable<CSVRecord> records = csvFormat.withDelimiter(this.fieldsSeparator.charAt(0)).withRecordSeparator(this.lineSeparator).parse(in);
			for (CSVRecord csvRecord : records) {
				currentRow++;
				if (currentRow >= startRow) {
					Record hdataRecord = new DefaultRecord(csvRecord.size());
					for (int i = 0, len = csvRecord.size(); i < len; i++) {
						if(!this.nullFormat.equals(csvRecord.get(i)))
							hdataRecord.add(csvRecord.get(i));
						else
							hdataRecord.add(null);
					}
					recordCollector.send(hdataRecord);
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw new HDataException(e);
		} catch (IOException e) {
			throw new HDataException(e);
		}
	}

	@Override
	public Splitter newSplitter() {
		return new CSVSplitter();
	}

}
