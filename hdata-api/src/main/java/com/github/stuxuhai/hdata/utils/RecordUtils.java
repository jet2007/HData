package com.github.stuxuhai.hdata.utils;

import org.apache.commons.csv.CSVRecord;

import com.github.stuxuhai.hdata.exception.HDataException;

public class RecordUtils {
	
	/*
	 * 根据原输入与columns，返回选取的字段后的输入
	 * columns: 1,2,3,#123,#abc
	 * tokens
	 */
	public static String[]  getRecordByColumns(String columns,String[] tokens) {
		if( columns == null ||  columns.isEmpty())
			return tokens;
		else {
			String[] arr =  columns.split("\\s*,\\s*");
			String[] rec=new String[arr.length];
			for (int i = 0; i < arr.length; i++) {
				if(arr[i].startsWith("#"))
					rec[i]=arr[i].substring(1);
				else {
					try {
						rec[i]=tokens[Integer.parseInt(arr[i])-1];
					} catch (Exception e) {
						throw new HDataException(e);
					}		
				}
			}
			return rec;
		}
	}
	
 
	/**
	 * 根据原输入与columns，返回选取的字段后的输入
	 * @param columns
	 * @param csvRecord
	 * @return
	 */
	public static String[]  getRecordByColumns(String columns,CSVRecord csvRecord) {
		if(columns == null || columns.isEmpty()){
			String[] rec=new String[csvRecord.size()];
			for (int i = 0; i < csvRecord.size(); i++) {
				rec[i]=csvRecord.get(i);
			}
			return rec;
		}
			
		else {
			String[] arr = columns.split("\\s*,\\s*");
			String[] rec=new String[arr.length];
			for (int i = 0; i < arr.length; i++) {
				if(!arr[i].startsWith("#"))
					try {
						rec[i]=csvRecord.get(Integer.parseInt(arr[i])-1);
					} catch (Exception e) {
						throw new HDataException(e);
					}	
				else {
					rec[i]=arr[i].substring(1);
				}
			}
			return rec;
		}
	}
	
	

	
}
