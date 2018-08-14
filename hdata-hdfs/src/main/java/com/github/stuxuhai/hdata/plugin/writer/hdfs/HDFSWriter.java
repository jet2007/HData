package com.github.stuxuhai.hdata.plugin.writer.hdfs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;

import com.github.stuxuhai.hdata.api.JobContext;
import com.github.stuxuhai.hdata.api.PluginConfig;
import com.github.stuxuhai.hdata.api.Record;
import com.github.stuxuhai.hdata.api.Writer;
import com.github.stuxuhai.hdata.exception.HDataException;
import com.github.stuxuhai.hdata.utils.EtlTimeAndFieldsHasher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public class HDFSWriter extends Writer {

	private int writerParallelism;
	private String path;
	private String fieldsSeparator;
	private String lineSeparator;
	private String encoding;
	private String compressCodec;
	private String nullFormat;
    private String etlTime ;
    private String fieldsHasher ;
    
	private BufferedWriter bw;
	private String[] strArray;
	private int fileNum = 0;
	private long maxFileBytesSize;
	private long writenBytesize;
	private int currentWriterSequence;
	private String filePathWithoutExtension;
	private String fileExtension = "";
	private int dateIndex;
	private String dateFormat;
	private String lastDate;
	private SimpleDateFormat sdf;
	private final SimpleDateFormat defaultdf = new SimpleDateFormat("yyyyMMdd");
	private Configuration conf;
	private PluginConfig writerConfig;
	private static AtomicInteger sequence = new AtomicInteger(0);
	private static final Pattern REG_FILE_PATH_WITHOUT_EXTENSION = Pattern.compile(".*?(?=(\\.\\w+)?$)");
	private static final Pattern REG_FILE_EXTENSION = Pattern.compile("(\\.\\w+)$");
	


	private Path createFilePath(int fileNum) {
		String path = null;
		if (dateIndex >= 0 && dateFormat != null && writerParallelism > 1 && maxFileBytesSize > 0) {
			path = String.format("%s_%s_%05d_%05d%s", filePathWithoutExtension, lastDate, currentWriterSequence,
					fileNum, fileExtension);
		} else if (dateIndex >= 0 && dateFormat != null && writerParallelism > 1) {
			path = String.format("%s_%s_%05d%s", filePathWithoutExtension, lastDate, currentWriterSequence,
					fileExtension);
		} else if (dateIndex >= 0 && dateFormat != null && maxFileBytesSize > 0) {
			path = String.format("%s_%s_%05d%s", filePathWithoutExtension, lastDate, fileNum, fileExtension);
		} else if (dateIndex >= 0 && dateFormat != null) {
			path = String.format("%s_%s%s", filePathWithoutExtension, lastDate, fileExtension);
		} else if (writerParallelism > 1 && maxFileBytesSize > 0) {
			path = String.format("%s_%05d_%05d%s", filePathWithoutExtension, currentWriterSequence, fileNum,
					fileExtension);
		} else if (writerParallelism > 1) {
			path = String.format("%s_%05d%s", filePathWithoutExtension, currentWriterSequence, fileExtension);
		} else if (maxFileBytesSize > 0) {
			path = String.format("%s_%05d%s", filePathWithoutExtension, fileNum, fileExtension);
		} else {
			path = filePathWithoutExtension + fileExtension;
		}

		return new Path(path);
	}

	private BufferedWriter createBufferedWriter(Path hdfsPath) throws IOException {
		FileSystem fs = hdfsPath.getFileSystem(conf);
		FSDataOutputStream output = fs.create(hdfsPath);
		if (compressCodec == null) {
			return new BufferedWriter(new OutputStreamWriter(output, encoding));
		} else {
			CompressionCodecFactory factory = new CompressionCodecFactory(conf);
			CompressionCodec codec = factory.getCodecByClassName(compressCodec);
			return new BufferedWriter(new OutputStreamWriter(codec.createOutputStream(output), encoding));
		}
	}

	@Override
	public void prepare(JobContext context, PluginConfig writerConfig) {
		this.writerConfig = writerConfig;
		writerParallelism = writerConfig.getParallelism();
		path = writerConfig.getString(HDFSWriterProperties.PATH);
		Preconditions.checkNotNull(path, "HDFS writer required property: path");

		fieldsSeparator = StringEscapeUtils
				.unescapeJava(writerConfig.getString(HDFSWriterProperties.FIELDS_SEPARATOR, HDFSWriterProperties.FIELDS_SEPARATOR_DEFAULT));
		lineSeparator = StringEscapeUtils
				.unescapeJava(writerConfig.getString(HDFSWriterProperties.LINE_SEPARATOR, HDFSWriterProperties.LINE_SEPARATOR_DEFAULT));
		encoding = writerConfig.getString(HDFSWriterProperties.ENCODING, HDFSWriterProperties.ENCODING_DEFAULT);
		compressCodec = writerConfig.getProperty(HDFSWriterProperties.COMPRESS_CODEC);
		maxFileBytesSize = writerConfig.getLong(HDFSWriterProperties.MAX_FILE_SIZE_MB, HDFSWriterProperties.MAX_FILE_SIZE_MB_DEFAULT) * 1024 * 1024;
		dateIndex = writerConfig.getInt(HDFSWriterProperties.PARTITION_DATE_INDEX, HDFSWriterProperties.PARTITION_DATE_INDEX_DEFAULT);
		dateFormat = writerConfig.getString(HDFSWriterProperties.PARTITIONED_DATE_FORMAT);
		
		this.nullFormat=writerConfig.getString(HDFSWriterProperties.NULL_FORMAT,HDFSWriterProperties.NULL_FORMAT_DEFAULT);
		
        this.etlTime = writerConfig.getString(HDFSWriterProperties.ETL_TIME);
        this.fieldsHasher = writerConfig.getString(HDFSWriterProperties.FIELDS_HASHER);

		String hadoopUser = writerConfig.getString(HDFSWriterProperties.HADOOP_USER);
		if (hadoopUser != null) {
			System.setProperty("HADOOP_USER_NAME", hadoopUser);
		}

		Matcher m1 = REG_FILE_PATH_WITHOUT_EXTENSION.matcher(path.trim());
		if (m1.find()) {
			filePathWithoutExtension = m1.group();
		}

		Matcher m2 = REG_FILE_EXTENSION.matcher(path.trim());
		if (m2.find()) {
			fileExtension = m2.group();
		}

		currentWriterSequence = sequence.getAndIncrement();
	}

	@Override
	public void execute(Record record) {
		Object[] objsRecord = EtlTimeAndFieldsHasher.getRecordByEtlTimeAndFieldsHasher(etlTime, fieldsHasher, record);
		
		if (dateIndex >= 0 && dateFormat != null) {
			if (sdf == null) {
				sdf = new SimpleDateFormat(dateFormat);
			}

			try {
				String currentDate = defaultdf.format(sdf.parse(objsRecord[dateIndex].toString()));
				if (lastDate == null) {
					lastDate = currentDate;
				} else if (!currentDate.equals(lastDate)) {
					bw.close();
					fileNum = 0;
					writenBytesize = 0;
					lastDate = currentDate;
					bw = createBufferedWriter(createFilePath(fileNum++));
				}
			} catch (ParseException e) {
				throw new HDataException(e);
			} catch (IOException e) {
				throw new HDataException(e);
			}
		}

		if (bw == null) {
			Path hdfsPath = createFilePath(fileNum++);
			conf = new Configuration();
			if (writerConfig.containsKey(HDFSWriterProperties.HDFS_CONF_PATH)) {
				for (String path: writerConfig.getString(HDFSWriterProperties.HDFS_CONF_PATH).split(",")) {
					conf.addResource(new Path("file://" + path));
				}
			}

			try {
				bw = createBufferedWriter(hdfsPath);
			} catch (IOException e) {
				throw new HDataException(e);
			}
		}

		if (strArray == null) {
			strArray = new String[objsRecord.length];
		}

		for (int i = 0, len = objsRecord.length; i < len; i++) {
			Object o = objsRecord[i];
			if (o == null) {
				//strArray[i] = "NULL";
				strArray[i] = this.nullFormat; //空值处理的地方
			} else {
				strArray[i] = o.toString();
			}
		}

		try {
			String line = Joiner.on(fieldsSeparator).join(strArray) + lineSeparator;
			if (maxFileBytesSize > 0) {
				writenBytesize += line.getBytes(encoding).length;
				if (writenBytesize >= maxFileBytesSize) {
					bw.close();
					bw = createBufferedWriter(createFilePath(fileNum++));
					writenBytesize = 0;
				}
			}
			bw.write(line);
		} catch (IOException e) {
			throw new HDataException(e);
		}
	}

	@Override
	public void close() {
		if (bw != null) {
			try {
				bw.close();
			} catch (IOException e) {
				throw new HDataException(e);
			}
		}
	}
}
