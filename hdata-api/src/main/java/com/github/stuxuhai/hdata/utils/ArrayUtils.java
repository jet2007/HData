package com.github.stuxuhai.hdata.utils;

import java.util.Arrays;

public class ArrayUtils {

	/* 
     * 数组添加元素 
     * */  
    public static void main(String[] args) {  
        int array[] ={ 2, 5, -2, 6, -3, 8, 0, -7, -9, 4};  
        Arrays.sort(array);  
          
        printArray("数组排序：", array);  
          
        int index=Arrays.binarySearch(array, 3);  
          
        System.out.println("元素 3 所在位置（负数不存在）："+index);  
          
        int newIndex=-index-1;  
        System.out.println("元素 ）："+newIndex);    
        array=insertElement(array, 3, newIndex);  
          
        printArray("数组添加元素 3：", array);  
    }  
    
    public static void printArray(Object[] array){
    	System.out.print("#############=[");
    	for (int i = 0; i < array.length; i++) {
    		System.out.print(array[i]+",");  
		}
    	System.out.println("]");  
    	
    }
    
    
    public static void printArray(String message,int array[]){  
        System.out.println(message+"[length:"+array.length+"]");  
        for (int i = 0; i < array.length; i++) {  
            if (i!=0) {  
                System.out.print(",");  
            }  
            System.out.print(array[i]);  
        }  
        System.out.println();  
    }  
    public static int[] insertElement(int original[],int element,int index){  
          
        int length=original.length;  
        int destination[]=new int[length+1];  
        System.arraycopy(original, 0, destination, 0, index);  
        destination[index]=element;  
        System.arraycopy(original, index, destination, index+1, length-index);  
        return destination;  
    }  
      	
    public static Object[] insertElement(Object[] original,String element,int index){  
        int length=original.length;  
        Object[] destination= new Object[length+1] ;  
        if(index>length) index=length;
        System.arraycopy(original, 0, destination, 0, index);  
        destination[index]=element;  
        System.arraycopy(original, index, destination, index+1, length-index);  
        return destination;  
    }  
    
    
//    public static String[] insert2Elements(String[] original,String element1,int index1,String element2,int index2){  
//        int length=original.length;  
//        String[] destination= new String[length+2] ;  
//        int i1,i2;
//        String e1,e2;
//        if(index1<index2) {i1=index1;i2=index2;e1=element1;e2=element2;}
//        else {i1=index2;i2=index1;e1=element2;e2=element1;}
//        for (int i = 0; i < destination.length; i++) {
//        	if(i<i1) destination[i]=original[i];
//        	else if(i==i1) destination[i]=e1;
//        	else if(i<i2+2) destination[i]=original[i-1];
//        	else if(i==i2+2) destination[i]=e2;
//        	else destination[i]=original[i-2];
//		}
//        return destination;  
//    }
    
}
