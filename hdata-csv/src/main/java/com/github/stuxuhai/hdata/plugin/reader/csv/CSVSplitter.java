package com.github.stuxuhai.hdata.plugin.reader.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.stuxuhai.hdata.api.JobConfig;
import com.github.stuxuhai.hdata.api.PluginConfig;
import com.github.stuxuhai.hdata.api.Splitter;
import com.google.common.base.Preconditions;

public class CSVSplitter extends Splitter {

	@Override
	public List<PluginConfig> split(JobConfig jobConfig) {
		List<PluginConfig> list = new ArrayList<PluginConfig>();
		PluginConfig readerConfig = jobConfig.getReaderConfig();

		String dir = readerConfig.getString(CSVReaderProperties.DIR);
		String filenameRegexp = readerConfig.getString(CSVReaderProperties.FILEREG);
		boolean recursive = readerConfig.getBoolean(CSVReaderProperties.RECURSIVE,CSVReaderProperties.RECURSIVE_DEFAULT);
		
		Preconditions.checkNotNull(dir, "CSV reader required property: dir");
		Preconditions.checkNotNull(filenameRegexp, "CSV reader required property: file");

		if (dir != null) {
			//String[] pathArray = paths.split(",");
			List<String> pathArray = listFiles(dir,filenameRegexp,recursive);
			for (String path : pathArray) {System.out.println("#############=["+path+"]");}
			for (String path : pathArray) {
				if (!path.trim().isEmpty()) {
					PluginConfig pluginConfig = (PluginConfig) readerConfig.clone();
					pluginConfig.put(CSVReaderProperties.DIR, path);
					list.add(pluginConfig);
				}
			}
		}
		return list;
	}
	
	public List<String> listFiles(String dir,String filenameRegexp, boolean recursive){
		List<String> list=new ArrayList<String>();
		File file = new File(dir);
        if (!file.exists()) {
            return null;
        }
        for (File f : file.listFiles()){
        	if(f.isFile() && Pattern.matches(filenameRegexp,f.getName()) )
        		list.add(f.getAbsolutePath());	
        	else if( recursive && f.isDirectory() ) {
        		list.addAll(listFiles(f.getAbsolutePath(), filenameRegexp, recursive) );
        	}
        }
		
		return list;
		
	}
	
	
}
