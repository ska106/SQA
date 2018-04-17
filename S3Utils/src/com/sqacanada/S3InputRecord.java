package com.sqacanada;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;

public class S3InputRecord
{
  public String name;
  public String activity;
  public String multipart;
  public String partSize;
  public String rate;
  public String interval;
  public String minSize;
  public String maxSize;
  public String duration;
  public String startOffset;
  public String filePath;
  private long fileSize;
  public String[] paritionedFiles;
  public ArrayList<SQACanMultipartResponse> responseList;
   
  /**
   * Default Constructor 
   */
  public S3InputRecord(){}
  
   /**
     * Custom Constructor with parameters.  
	 * @param name
	 * @param activity
	 * @param multipart
	 * @param partSize
	 * @param rate
	 * @param interval
	 * @param minSize
	 * @param maxSize
	 * @param duration
	 * @param startOffset
	 */
  public S3InputRecord(String name, String activity, String multipart, String partSize, String rate, String interval,
		String minSize, String maxSize, String duration, String startOffset) 
  {
	super();
	this.name = name.trim();
	this.activity = activity.trim();
	this.multipart = multipart.trim();
	this.partSize = partSize.trim();
	this.rate = rate.trim();
	this.interval = interval.trim();
	this.minSize = minSize.trim();
	this.maxSize = maxSize.trim();
	this.duration = duration.trim();
	this.startOffset = startOffset.trim();
	this.fileSize = Long.parseLong(this.maxSize);
	responseList = new ArrayList<SQACanMultipartResponse>();
  }//S3InputRecord

  	@Override
  public String toString() 
  {
  		return "<S3InputRecord> [name=" + name + ", activity=" + activity + ", multipart=" + multipart + ", partSize="
			+ partSize + ", rate=" + rate + ", interval=" + interval + ", minSize=" + minSize + ", maxSize=" + maxSize
			+ ", duration=" + duration + ", startOffset=" + startOffset + ", fileSize(long)="+this.fileSize+"]";
  }//toString
  	
  /**
   * For test purposes only.
   * @param args
   */
  public static void main (String [] args)
  {
//	  String testString  = "1 byte xsmall,upload,single,1,5,200,0,1,,,1,1,,,";
//	  String testString  = "xsmall-20-1.data,upload,single,20,5,200,0,20,,,1,20,,,";
//	  String testString  = "200 KB smalls,upload,single,204800,20,50,0,204800,,,1,204800,,,";
//	  String testString  = "1 MB med,upload,single,1048576,2,500,0,1048576,,,1,1048576,,,";
//	  String testString  = "32 MB med,upload,single,33554432,0.05,20000,0,33554432,,,1,33554432,,,";
//	  String testString  = "1.0_GB,upload,multipart,52428800,0.001,900000,0,1073741824,,,1,1073741824,,,";
//	  String testString  = "1.5 GB,upload,multipart,1610612736,0.001,900000,0,1610612736,,,1,1610612736,,,";

	//String [] parts =  testString.split(",");
	/*.out.println("length :" + parts.length );
	S3InputRecord rec = new S3InputRecord(parts[0],parts[1],parts[2],parts[3],parts[4],parts[5],parts[6],parts[7],parts[8],parts[9]);
	rec.name = parts[0].trim();
	rec.activity = parts[1].trim();
	rec.multipart = parts[2].trim();
	rec.partSize = parts[3].trim();
	rec.rate = parts[4].trim();
	rec.interval = parts[5].trim();
	rec.minSize = parts[6].trim();
	rec.maxSize = parts[7].trim();
	rec.duration = parts[8].trim();
	rec.startOffset = parts[9].trim();
	rec.filePath = "C:\\Apache\\apache-jmeter-3.3\\apache-jmeter-3.3\\testFiles\\workFiles\\";
	System.out.println("Record - " + rec);*/
	try
	{
		S3InputRecord rec = new S3InputRecord();
		rec.writeFile("large-500M-user2.data","C:\\Apache\\apache-jmeter-3.3\\apache-jmeter-3.3\\testFiles\\workFiles\\","522752000","104587600","single");
		//rec.getFilePartsAsList();
		
		//rec.writeFile(rec.name,rec.filePath, rec.maxSize, rec.partSize,rec.multipart);
	}//try
	catch (Exception e) 
	{	
		e.printStackTrace();
	}//catch
  }//main 
  
  /**
   * This method will write the file to the path specified in the object, with random values upto of a specified file size.
   * @throws Exception
   */
  public void writeFile() throws Exception
  {	
	  System.out.println(">>> writeFile() ::: Details =" + this.toString() );
	  String uploadFileName = this.filePath + this.name;
	  System.out.println(this.toString());
	  System.out.println("Max File Size : " + this.maxSize +" ; File Size : " + Long.parseLong(this.maxSize));
	  
	  try 
	  {
		RandomAccessFile file = null;
	  	
	  	if (this.islargeOrxLarge() && this.multipart.equalsIgnoreCase("multipart"))
	  	{
	  		int maxFileSize = Integer.parseInt(this.maxSize);
	  		int morefiles = maxFileSize;
	  		
	  		int subPartSize = Integer.parseInt(this.partSize);
	  		int numberOfMultipartFiles = maxFileSize/subPartSize;
	  		if (maxFileSize%subPartSize > 0)
	  		{
	  			numberOfMultipartFiles +=1;
	  		}//if
	  		ArrayList <String> subFileList = new ArrayList<String>(numberOfMultipartFiles);
	  		System.out.println("Number of sub files to be created for : " + this.name + " is : " + numberOfMultipartFiles);
	  		int fileCounter = 1;
	  		String subFileName = null;
	  		while (morefiles > 0)
	  		{
	  			subFileName = this.name+"_part_" + fileCounter++;
	  			subFileList.add(subFileName);
	  			file = new RandomAccessFile(this.filePath + subFileName, "rw");
	  					
	  			morefiles -= subPartSize;
	  			file.setLength(Long.parseLong(this.partSize));
		  		file.writeBytes("S");
	  		}//while
	  		paritionedFiles = subFileList.toArray(new String [subFileList.size()]);
	  		System.out.println("Numbers of partitioned files created : " + paritionedFiles.length);
	  	}//if
	  	else
	  	{
	  		file = new RandomAccessFile(uploadFileName, "rw");
	  		file.setLength(Long.parseLong(this.maxSize));
	  		file.writeBytes("S");
	  	}//else
	  	file.close();
	  }//try
	  catch (Exception e) 
	  {
		 throw new Exception (e);
	  }//catch
	  System.out.println("<<< writeFile()");
  }//writeFile


/**
 *   
 * This method will write the file to the path specified in the object, with random values upto of a specified file size.
 * @param name
 * @param filePath
 * @param maxSize
 * @param partSize
 * @param multipart
 * @throws Exception
 */
  public void writeFile(String name, String filePath, String maxSize, String partSize, String multipart) throws Exception
  {	
	  this.name = name;
	  this.filePath = filePath;
	  this.maxSize = maxSize;
	  this.partSize = partSize;
	  this.multipart = multipart;
			
	  
	  System.out.println(">>> writeFile() ::: Details =" + this.toString() );
	  String uploadFileName = filePath + name;
	  System.out.println(this.toString());
	  System.out.println("Max File Size : " + maxSize +" ; File Size : " + Long.parseLong(maxSize));
	  
	  try 
	  {
		RandomAccessFile file = null;
	  	
	  	if (this.islargeOrxLarge() && multipart.equalsIgnoreCase("multipart"))
	  	{
	  		int maxFileSize = Integer.parseInt(maxSize);
	  		int morefiles = maxFileSize;
	  		
	  		int subPartSize = Integer.parseInt(partSize);
	  		int numberOfMultipartFiles = maxFileSize/subPartSize;
	  		if (maxFileSize%subPartSize > 0)
	  		{
	  			numberOfMultipartFiles +=1;
	  		}//if
	  		ArrayList <String> subFileList = new ArrayList<String>(numberOfMultipartFiles);
	  		System.out.println("Number of sub files to be created for : " + name + " is : " + numberOfMultipartFiles);
	  		int fileCounter = 1;
	  		String subFileName = null;
	  		subFileList.add(name);
	  		while (morefiles > 0)
	  		{
	  			subFileName = name+"_part_" + fileCounter++;
	  			subFileList.add(subFileName);
	  			file = new RandomAccessFile(filePath + subFileName, "rw");
	  					
	  			morefiles -= subPartSize;
	  			file.setLength(Long.parseLong(partSize));
	  		    String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ?/.,";
	  	        Random rnd = new Random();
	  	        char character = alphabet.charAt(rnd.nextInt(alphabet.length()));
	  	        // To ensure that every part that is generate is different - content wise.
		  		file.writeBytes(String.valueOf(character));
	  		}//while
	  		paritionedFiles = subFileList.toArray(new String [subFileList.size()]);
	  		System.out.println("Numbers of partitioned files created : " + paritionedFiles.length);
	  	}//if
	  	else
	  	{
	  		file = new RandomAccessFile(uploadFileName, "rw");
	  		file.setLength(Long.parseLong(maxSize));
	  		file.writeBytes("S");
	  	}//else
	  	file.close();
	  }//try
	  catch (Exception e) 
	  {
		 throw new Exception (e);
	  }//catch
	  System.out.println("<<< writeFile()");
  }//writeFile
  
  private boolean islargeOrxLarge() 
	{
		boolean isLargeOrxLarge = this.name.contains("large"); 
		System.out.println("File :" + name + " Large or Extra Large ? : " + isLargeOrxLarge);
		return isLargeOrxLarge;
	}//isLargeOrxLarge
	
	public String getFileContent(String fileName) throws Exception
	{
		System.out.println(">>> getFileContent() ::: fileName = " + fileName);
		String FQNFilePath = this.filePath + fileName;
		System.out.println("FQNFileName : " + FQNFilePath);
		InputStream is = new FileInputStream(new File (FQNFilePath));
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));
		        
		String line = buf.readLine();
		StringBuilder sb = new StringBuilder();
		        
		while(line != null)
		{
		   sb.append(line).append("\n");
		   line = buf.readLine();
		}//while
		is.close();
		buf.close();      
		System.out.println("<<< getFileContent() ::: fileLength =" + sb.toString().length());
		return sb.toString();	
	}//getFileContent
	
	/**
	 * @return
	 */
	public String getMultipartResponseAssimilator()
	{
		System.out.println(">>> getMultipartResponseAssimilator() ::: Number of Parts = " + responseList.size());
		StringBuffer returnResponse = new StringBuffer("<CompleteMultipartUpload>");
		for (SQACanMultipartResponse item: responseList)
		{
			returnResponse.append(SQACanUtils.makePartTag(item.name, item.eTag));
		}//for
		returnResponse.append("</CompleteMultipartUpload>");
		System.out.println(">>> getMultipartResponseAssimilator() ::: return XML = " + returnResponse.toString()); 
		return returnResponse.toString();
	}//getMultipartResponseAssimilator
	
	public void addToResponseList(SQACanMultipartResponse item)
	{
		if (responseList == null)
		{
			responseList = new ArrayList<SQACanMultipartResponse>();
		}//if
		responseList.add(item);
		System.out.println("Response List Size : " + responseList.size());
	}//addToResponseList
	
	public ArrayList<String> getFilePartsAsList()
	{
		System.out.println(">>> getFilePartsAsList()");
		ArrayList<String> filePartList = new ArrayList<String>(paritionedFiles.length);
		for (String fileName : paritionedFiles )
		{
			System.out.println("File name : " + fileName);
			filePartList.add(fileName);
		}//for
		System.out.println("<<< getFilePartsAsList() ::: " + filePartList.size() + " part files." );
		return filePartList;
	}//getFileParts
}//S3InputRecord