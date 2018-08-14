package com.github.stuxuhai.hdata.utils;


import com.github.stuxuhai.hdata.api.DefaultRecord;
import com.github.stuxuhai.hdata.api.Fields;
import com.github.stuxuhai.hdata.api.Record;
import com.github.stuxuhai.hdata.utils.ArrayUtils;
import org.apache.commons.codec.digest.DigestUtils;


public class EtlTimeAndFieldsHasher {
	
	public static final String ETLTIME_NAME="etl_time";
	public static final String ETLTIME_POSITION="+1";
	public static final String FIELDSHASHER_NAME="fields_hasher";
	public static final String FIELDSHASHER_POSITION="+2";
	
	/**
	 * 
	 * @param etlTime
	 * @return etltime的字段名称
	 */
	public static String parseEtlTimeColumnName(String etlTime){
		if(etlTime==null) return null;
		else{
			if(etlTime.length()==0) etlTime=ETLTIME_NAME+":"+ETLTIME_POSITION;
			return etlTime.split(":")[0].trim();
		}
	}
	
	/**
	 * 根据etlTime解析
	 * @param etlTime：etltime字段名称:etltime字段位置
	 * 				1、若直接写成空字符串,等价于etl_time:+1
	 * 				2、etltime字段：+1为len+1；指定位置
	 * @param len: 数据组长度
	 * @return
	 */
	public static Integer parseEtlTimeColumnPosition(String etlTime,int len){
		Integer re ;
		if(etlTime==null) return null;
		else{
			if(etlTime.length()==0) etlTime=ETLTIME_NAME+":"+ETLTIME_POSITION;
			else if (etlTime.split(":").length<2) etlTime=etlTime+":"+ETLTIME_POSITION;
			String loc = etlTime.split(":")[1].trim();
			if(loc.startsWith("+"))
				re=len-1+Integer.parseInt(loc);
			else
				re=Integer.parseInt(loc)-1;
			return re;
		}
	}
	
	public static String parseFieldsHasherColumnName(String fields_hasher){
		if(fields_hasher==null) return null;
		else{
			if(fields_hasher.length()==0) fields_hasher=FIELDSHASHER_NAME+":"+FIELDSHASHER_POSITION;
			return fields_hasher.split(":")[0].trim();
		}
	}
	
	public static Integer parseFieldsHasherColumnPosition(String fields_hasher,int len){
		Integer re ;
		if(fields_hasher==null) return null;
		else{
			if(fields_hasher.length()==0) fields_hasher=FIELDSHASHER_NAME+":"+FIELDSHASHER_POSITION;
			else if(fields_hasher.split(":").length<2) fields_hasher=fields_hasher+":"+FIELDSHASHER_POSITION;
			String loc = fields_hasher.split(":")[1].trim();
			if(loc.startsWith("+"))
				re=len-1+Integer.parseInt(loc);
			else
				re=Integer.parseInt(loc)-1;
			return re;
		}
	}
	
	
 
	/**
	 * 经过etlTime和fieldsHasher处理后的String[]
	 * @param etlTime  etlTime的字段名称与位置
	 * @param fieldsHasher hash的字段名称与位置
	 * @param record String[]的输入
	 * @return 经过etlTime和fieldsHasher处理后的String[]
	 */
	public static Object[] getRecordByEtlTimeAndFieldsHasher(String etlTime,String fieldsHasher,Object[] record){
		if(etlTime==null && fieldsHasher==null){
			return record;
		}
		else if(etlTime !=null && fieldsHasher==null ) {
			Integer pos = parseEtlTimeColumnPosition(etlTime, record.length);
			if(pos>record.length) pos=record.length;//单个字段，位置字段最多+1
			String value = getCurrentDateTime();
			Object[] re = ArrayUtils.insertElement(record, value, pos);
			return re;
		}
		else if(etlTime ==null && fieldsHasher!=null ) {
			Integer pos = parseFieldsHasherColumnPosition(fieldsHasher, record.length);
			if(pos>record.length) pos=record.length;//单个字段，位置字段最多+1
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < record.length; i++) {
				sb=sb.append(",").append(record[i]==null ? "null":record[i].toString());
			}
			String val = DigestUtils.md5Hex(sb.toString());
			return ArrayUtils.insertElement(record, val, pos);
		}
		else {
			Integer etlPos = parseEtlTimeColumnPosition(etlTime, record.length);
			String etlVal = getCurrentDateTime();
			Integer hashPos = parseFieldsHasherColumnPosition(fieldsHasher, record.length);

			
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < record.length; i++) {
				sb=sb.append(",").append(record[i]==null ? "null":record[i].toString());
			}
			String hashVal = DigestUtils.md5Hex(sb.toString());
			if(etlPos<=hashPos){
				Object[] rec1 = ArrayUtils.insertElement(record, etlVal, etlPos);
				Object[] rec2 = ArrayUtils.insertElement(rec1, hashVal, hashPos+1);
				return rec2;
			}
			else {
				Object[] rec1 = ArrayUtils.insertElement(record, hashVal, hashPos);
				Object[] rec2 = ArrayUtils.insertElement(rec1, etlVal, etlPos+1);
				return rec2;
			}
		}
	}
	
	/**
	 * 经过etlTime和fieldsHasher处理后的String[]
	 * @param etlTime  etlTime的字段名称与位置
	 * @param fieldsHasher hash的字段名称与位置
	 * @param record 输入
	 * @return 经过etlTime和fieldsHasher处理后的String[]
	 */
	public static Object[] getRecordByEtlTimeAndFieldsHasher(String etlTime,String fieldsHasher,Record record){
    	Object[] objs= new Object[record.size()] ;  
    	for (int i = 0; i < record.size(); i++) {
    		objs[i]=record.get(i);
		}
    	Object[] objsRecord = getRecordByEtlTimeAndFieldsHasher(etlTime, fieldsHasher, objs);
		return objsRecord;
	}
	
	
	public static Fields getColomnsByEtlTimeAndFieldsHasher(String etlTime,String fieldsHasher,Fields columns){
		if(etlTime==null && fieldsHasher==null){
			return columns;
		}else if(etlTime !=null && fieldsHasher==null ) {
			Integer pos = parseEtlTimeColumnPosition(etlTime, columns.size());
			if(pos>columns.size()) pos=columns.size();//单个字段，位置字段最多+1
			String value = parseEtlTimeColumnName(etlTime);
			columns.add(pos, value);
			return columns;
		}
		else if(etlTime ==null && fieldsHasher!=null ){
			Integer pos = parseFieldsHasherColumnPosition(fieldsHasher, columns.size());
			if(pos>columns.size()) pos=columns.size();//单个字段，位置字段最多+1
			String val = parseFieldsHasherColumnName(fieldsHasher);
			columns.add(pos, val);
			return columns;
		}
		else{
			Integer etlPos = parseEtlTimeColumnPosition(etlTime, columns.size());
			String etlVal = parseEtlTimeColumnName(etlTime);
			Integer hashPos = parseFieldsHasherColumnPosition(fieldsHasher, columns.size());
			String hashVal = parseFieldsHasherColumnName(fieldsHasher);
			if(etlPos<=hashPos){
				if(etlPos>columns.size()) etlPos=columns.size();//单个字段，位置字段最多+1
				if(hashPos>columns.size()) hashPos=columns.size();//单个字段，位置字段最多+1
				columns.add(etlPos, etlVal);
				columns.add(hashPos+1, hashVal);
			}
			else{
				if(etlPos>columns.size()) etlPos=columns.size();//单个字段，位置字段最多+1
				if(hashPos>columns.size()) hashPos=columns.size();//单个字段，位置字段最多+1
				columns.add(hashPos, hashVal);
				columns.add(etlPos+1, etlVal);
			}
			return columns;
		}
	}

	 
	
    //得到当前日期时间
   public static String getCurrentDateTime() {
       java.text.SimpleDateFormat d = new java.text.SimpleDateFormat();
       d.applyPattern("yyyy-MM-dd HH:mm:ss");
       java.util.Date nowdate = new java.util.Date();
       return d.format(nowdate);
   }
	

	public static void main(String[] args) {
		String[] rec={ "a1","a2","a3","a4","a5","a6","a7" };
		ArrayUtils.printArray(rec);
		String etl_time = "etl:2";
		String fields_hasher=null;
		//String etl_time = "",fields_hasher="";
		
		System.out.println("############ EtlTimeColumnName="+parseEtlTimeColumnName(etl_time));
		System.out.println("############ parseEtlTimeColumnPosition="+parseEtlTimeColumnPosition(etl_time, rec.length));
		 
		
		System.out.println("############ parseFieldsHasherColumnName="+parseFieldsHasherColumnName(fields_hasher));
		System.out.println("############ parseFieldsHasherColumnPosition="+parseFieldsHasherColumnPosition(fields_hasher, rec.length));
		System.out.println("!!!!!!!!!!!!!!");
		Object[] rec11 = getRecordByEtlTimeAndFieldsHasher(etl_time, fields_hasher, rec);
		ArrayUtils.printArray(rec11);
		
		
		Fields columns=new Fields();
		
		columns.add("id");
		columns.add("name");
		columns.add("de");
		columns.add("fl");
		columns.add("st");
		columns.add("tx");
		Fields aaa = getColomnsByEtlTimeAndFieldsHasher(etl_time, fields_hasher, columns);
		for (int i = 0; i < aaa.size(); i++) {
			System.out.println("*******:"+aaa.get(i));
		}
		
		
		DefaultRecord record=new DefaultRecord(5);
		record.add("b1");
		record.add("b2");
		record.add("b3");
		record.add("b4");
		record.add("b5");
		
 
		Object[] r2 = getRecordByEtlTimeAndFieldsHasher(etl_time, fields_hasher, record);
		 
		
	}

}
