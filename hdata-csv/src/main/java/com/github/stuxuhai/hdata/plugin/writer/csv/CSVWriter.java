package com.github.stuxuhai.hdata.plugin.writer.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import com.github.stuxuhai.hdata.plugin.FormatConf;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.stuxuhai.hdata.api.Fields;
import com.github.stuxuhai.hdata.api.JobContext;
import com.github.stuxuhai.hdata.api.PluginConfig;
import com.github.stuxuhai.hdata.api.Record;
import com.github.stuxuhai.hdata.api.Writer;
import com.github.stuxuhai.hdata.exception.HDataException;
import com.google.common.base.Preconditions;

public class CSVWriter extends Writer {

	private static final Logger LOGGER = LogManager.getLogger(CSVWriter.class);
    private String path = null;
    private String encoding = null;
    private String fieldSeparator = null;
    private java.io.Writer writer;
    private CSVPrinter csvPrinter;
    private Fields fields;
    private boolean showColumns;
    private boolean showTypesAndComments;
    private String[] types;
    private String[] comments;
    private List<Object> csvList = new ArrayList<Object>();
    private static AtomicInteger sequence = new AtomicInteger(0);
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Pattern REG_FILE_PATH_WITHOUT_EXTENSION = Pattern.compile(".*?(?=\\.\\w+$)");
    private static final Pattern REG_FILE_EXTENSION = Pattern.compile("(\\.\\w+)$");
    private String format;
    private CSVFormat csvFormat = CSVFormat.DEFAULT;
    
    
    private String lineSeparator;
    private String nullFormat;
    private String compress;
    private String writemode;

    @Override
    public void prepare(JobContext context, PluginConfig writerConfig) {
        path = writerConfig.getString(CSVWriterProperties.PATH);
        Preconditions.checkNotNull(path, "CSV writer required property: path");

        encoding = writerConfig.getString(CSVWriterProperties.ENCODING, CSVWriterProperties.ENCODING_DEFAULT);
        fieldSeparator = StringEscapeUtils.unescapeJava(writerConfig.getString(CSVWriterProperties.FIELDS_SEPARATOR,  CSVWriterProperties.FIELDS_SEPARATOR_DEFAULT));
        this.lineSeparator = StringEscapeUtils.unescapeJava(writerConfig.getString(CSVWriterProperties.LINE_SEPARATOR,  CSVWriterProperties.LINE_SEPARATOR_DEFAULT));

        this.nullFormat = writerConfig.getString(CSVWriterProperties.NULL_FORMAT, CSVWriterProperties.NULL_FORMAT_DEFAULT);
        this.compress = writerConfig.getString(CSVWriterProperties.COMPRESS, CSVWriterProperties.COMPRESS_DEFAULT);
        this.writemode = writerConfig.getString(CSVWriterProperties.WRITEMODE, CSVWriterProperties.WRITEMODE_DEFAULT);
        
        
        format = writerConfig.getString(CSVWriterProperties.FORMAT);
        FormatConf.confCsvFormat(format,csvFormat);
        
        fields = context.getFields();
        showColumns = writerConfig.getBoolean(CSVWriterProperties.SHOW_COLUMNS, CSVWriterProperties.SHOW_COLUMNS_DEFAULT);
        showTypesAndComments = writerConfig.getBoolean(CSVWriterProperties.SHOW_TYPES_AND_COMMENTS, CSVWriterProperties.SHOW_TYPES_AND_COMMENTS_DEFAULT);
        if (showTypesAndComments) {
            types = context.getJobConfig().getString("types").split("\001");
            comments = context.getJobConfig().getString("comments").split("\001");
        }

        int parallelism = writerConfig.getParallelism();
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
        	
        	//写入方式
        	FileOutputStream outputStream=null;
        	if(this.writemode.toLowerCase().equals("insert")){
        		File file =new File(path);
        		if(file.exists()) {
        			LOGGER.error("写入方式为insert,但文件已存在！！！" );
        			throw new HDataException();}
        		else outputStream = new FileOutputStream(path);
        			
        	}
        	else if (this.writemode.toLowerCase().equals("overwrite") ){
        		 outputStream = new FileOutputStream(path);
        	}
        	else if (this.writemode.toLowerCase().equals("append") ) 
        		outputStream = new FileOutputStream(path,true);
        	else {
        		LOGGER.error("写入方式值错误！！！" );
        		throw new HDataException();
        	}
        	
        	// 增加gzip/bzip2的压缩格式
			if (compress.equals("gzip")) {
				writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(outputStream), encoding));
			}
			else if (compress.equals("bzip2")) {
				writer = new BufferedWriter(new OutputStreamWriter(new BZip2CompressorOutputStream(outputStream), encoding));
			}
			else  {
				writer = new BufferedWriter(new OutputStreamWriter(outputStream, encoding));
			} 
        } catch (Exception e) {
            throw new HDataException(e);
        }
        
//        try {
//            writer = new OutputStreamWriter(new FileOutputStream(path), encoding);
//            
//        } catch (Exception e) {
//            throw new HDataException(e);
//        }
    }

    @Override
    public void execute(Record record) {
        if (csvPrinter == null) {
            try {
            	//修正列分隔和行分隔符
                csvPrinter = new CSVPrinter(writer, csvFormat.withDelimiter(fieldSeparator.charAt(0)).withRecordSeparator(this.lineSeparator));
                if (showTypesAndComments) {
                    for (String type : types) {
                        csvList.add(type);
                    }
                    csvPrinter.printRecord(csvList);
                    csvList.clear();

                    for (String comment : comments) {
                        csvList.add(comment);
                    }
                    csvPrinter.printRecord(csvList);
                    csvList.clear();
                }

                if (showColumns) {
                    for (Object object : fields) {
                        csvList.add(object);
                    }
                    csvPrinter.printRecord(csvList);
                    csvList.clear();
                }
            } catch (IOException e) {
                throw new HDataException(e);
            }
        }

        for (int i = 0, len = record.size(); i < len; i++) {
            Object obj = record.get(i);
            if (obj instanceof Timestamp) {
                csvList.add(dateFormat.format(obj));
            } else if (obj !=null){
                csvList.add(obj);
            } else {
            	csvList.add(this.nullFormat); //null值的写入文件的指定值
            }
        }

        try {
            csvPrinter.printRecord(csvList);
        } catch (IOException e) {
            throw new HDataException(e);
        }
        csvList.clear();
    }

    @Override
    public void close() {
        if (csvPrinter != null) {
            try {
            	//csvPrinter.flush();
                csvPrinter.close();
            } catch (IOException e) {
                throw new HDataException(e);
            }
        }

        if (writer != null) {
            try {
            	//writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new HDataException(e);
            }
        }
    }
    
}
