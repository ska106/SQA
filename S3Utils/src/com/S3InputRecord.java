package com.sqacanada;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

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
	List<String> filesNamelist = new ArrayList<String>();

	/**
	 * Default Constructor
	 */
	public S3InputRecord() {}

	/**
	 * Custom Constructor with parameters.
	 * 
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
			String minSize, String maxSize, String duration, String startOffset) {
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
	}// S3InputRecord

	@Override
	public String toString() {
		return "<S3InputRecord> [name=" + name + ", activity=" + activity + ", multipart=" + multipart + ", partSize="
				+ partSize + ", rate=" + rate + ", interval=" + interval + ", minSize=" + minSize + ", maxSize="
				+ maxSize + ", duration=" + duration + ", startOffset=" + startOffset + ", fileSize(long)="
				+ this.fileSize + "]";
	}// toString

	/**
	 * For test purposes only.
	 * @param args
	 */
	public static void main(String[] args) 
	{
 	    try 
		{
			S3InputRecord rec = new S3InputRecord();
			rec.writeFile("large-500M", "D:\\Jmeter\\apache-jmeter-3.3\\apache-jmeter-3.3\\testFiles\\workFiles\\",	"522752000", "104587600", "multipart");
			List<String> a = rec.getFilePartsAsList();
		} // try

		catch (Exception e) 
		{
			e.printStackTrace();
		} // catch
	}// main

	/**
	 * This method will write the file to the path specified in the object, with
	 * random values upto of a specified file size.
	 * @throws Exception
	 */
	public void writeFile() throws Exception 
	{
		System.out.println(">>> writeFile() ::: Details =" + this.toString());
		String uploadFileName = this.filePath + this.name;
		System.out.println(this.toString());
		System.out.println("Max File Size : " + this.maxSize + " ; File Size : " + Long.parseLong(this.maxSize));

		try {
			RandomAccessFile file = null;

			if (this.islargeOrxLarge() && this.multipart.equalsIgnoreCase("multipart")) {
				int maxFileSize = Integer.parseInt(this.maxSize);
				int morefiles = maxFileSize;

				int subPartSize = Integer.parseInt(this.partSize);
				int numberOfMultipartFiles = maxFileSize / subPartSize;
				if (maxFileSize % subPartSize > 0) {
					numberOfMultipartFiles += 1;
				} // if
				ArrayList<String> subFileList = new ArrayList<String>(numberOfMultipartFiles);
				System.out.println(
						"Number of sub files to be created for : " + this.name + " is : " + numberOfMultipartFiles);
				int fileCounter = 1;
				String subFileName = null;
				while (morefiles > 0) {
					subFileName = this.name + "_part_" + fileCounter++;
					subFileList.add(subFileName);
					file = new RandomAccessFile(this.filePath + subFileName, "rw");

					morefiles -= subPartSize;
					file.setLength(Long.parseLong(this.partSize));
					file.writeBytes("S");
				} // while
				paritionedFiles = subFileList.toArray(new String[subFileList.size()]);
				System.out.println("Numbers of partitioned files created : " + paritionedFiles.length);
			} // if
			else {
				file = new RandomAccessFile(uploadFileName, "rw");
				file.setLength(Long.parseLong(this.maxSize));
				file.writeBytes("S");
			} // else
			file.close();
		} // try
		catch (Exception e) {
			throw new Exception(e);
		} // catch
		System.out.println("<<< writeFile()");
	}// writeFile

	/**
	 * overloaded
	 * This method will write the file to the path specified in the object, with
	 * random values upto of a specified file size.
	 * 
	 * @param name
	 * @param filePath
	 * @param maxSize
	 * @param partSize
	 * @param multipart
	 * @throws Exception
	 */
	public void writeFile(String name, String filePath, String maxSize, String partSize, String multipart) throws Exception 
	{
		System.out.println(">>> writeFile() ::: Details =" + this.toString());
		String uploadFileName = filePath + name;
		System.out.println(this.toString());
		System.out.println("Max File Size : " + maxSize + " ; File Size : " + Long.parseLong(maxSize));
		RandomAccessFile file = null;
		try 
		{
			if (islargeOrxLarge(name) && multipart.equalsIgnoreCase("multipart")) 
			{
				int maxFileSize = Integer.parseInt(maxSize);
				ArrayList<String> subFileList = new ArrayList<String>();
				String subFileName = null;
				subFileName = name;
				subFileList.add(subFileName);
				file = new RandomAccessFile(filePath + subFileName, "rw");
				file.setLength(Long.parseLong(maxSize));
				file.writeBytes("S");
				paritionedFiles = subFileList.toArray(new String[subFileList.size()]);
				System.out.println("Numbers of partitioned files created : " + paritionedFiles.length);
				file.close();
				filesNamelist.add(subFileName);
				writeMultiPartFiles(name, filePath, maxSize, partSize, multipart);
			}
			else 
			{
				file = new RandomAccessFile(uploadFileName, "rw");
				file.setLength(Long.parseLong(maxSize));
				file.writeBytes("S");
				file.close();
			} // else
		} // try
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new Exception(e);

		} // catch
		System.out.println("<<< writeFile()");
	}// writeFile

	

	/**
	 * overriden
	 * This method will write paritioned files the file to the path specified in
	 * the object, with random values upto of a specified file size.
	 * 
	 * @param name
	 * @param filePath
	 * @param maxSize
	 * @param partSize
	 * @param multipart
	 * @throws Exception
	 */
	public void writeMultiPartFiles(String name, String filePath, String maxSize, String partSize, String multipart) throws Exception 
	{
		System.out.println(">>> writeFile multipart files");
		String uploadFileName = filePath + name;
		System.out.println(this.toString());
		System.out.println("Max File Size : " + maxSize + " ; File Size : " + Long.parseLong(maxSize));

		try 
		{
			RandomAccessFile file = null;

			int maxFileSize = Integer.parseInt(maxSize);
			int morefiles = maxFileSize;

			int subPartSize = Integer.parseInt(partSize);
			int numberOfMultipartFiles = maxFileSize / subPartSize;
			if (maxFileSize % subPartSize > 0) {
				numberOfMultipartFiles += 1;
			}
			ArrayList<String> subFileList = new ArrayList<String>(numberOfMultipartFiles);
			System.out.println("Number of sub files to be created for : " + name + " is : " + numberOfMultipartFiles);
			int fileCounter = 1;
			String subFileName = null;
			while (morefiles > 0) 
			{
				try 
				{
					subFileName = name + "_part_" + fileCounter++;
					subFileList.add(subFileName);
					file = new RandomAccessFile(filePath + subFileName, "rw");

					morefiles -= subPartSize;
					file.setLength(Long.parseLong(partSize));
					file.writeBytes("S");
				}//try
				finally 
				{
					file.close();
				}//finally
				filesNamelist.add(subFileName);
			}//while
			paritionedFiles = subFileList.toArray(new String[subFileList.size()]);
			System.out.println("Numbers of partitioned files created : " + paritionedFiles.length);

		}//try 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new Exception(e);
		}//catch
		System.out.println("<<< writing multipart files");
	}//writeMultiPartFiles

	private boolean islargeOrxLarge() 
	{
		boolean isLargeOrxLarge = this.name.contains("GB") || this.name.contains("MB");
		System.out.println("File :" + name + " Large or Extra Large ? : " + isLargeOrxLarge);
		return isLargeOrxLarge;
	}// isLargeOrxLarge

	/**
	 * OverLoaded method that determines if the file is Large or X-large based on the file name.
	 * @param fileName
	 * @return
	 */
	private boolean islargeOrxLarge(String fileName)
	{
		boolean isLargeOrxLarge = fileName.contains("GB") || fileName.contains("M");
		System.out.println("File :" + fileName + " Large or Extra Large ? : " + isLargeOrxLarge);
		return isLargeOrxLarge;
	}// isLargeOrxLarge

	public String getFileContent(String fileName) throws Exception 
	{
		System.out.println(">>> getFileContent() ::: fileName = " + fileName);
		String FQNFilePath = this.filePath + fileName;
		System.out.println("FQNFileName : " + FQNFilePath);
		InputStream is = new FileInputStream(new File(FQNFilePath));
		BufferedReader buf = new BufferedReader(new InputStreamReader(is));

		String line = buf.readLine();
		StringBuilder sb = new StringBuilder();

		while (line != null) 
		{
			sb.append(line).append("\n");
			line = buf.readLine();
		}// while
		is.close();
		buf.close();
		System.out.println("<<< getFileContent() ::: fileLength =" + sb.toString().length());
		return sb.toString();
	}// getFileContent

	/**
	 * overridden
	 * Return the name of files created
	 * 
	 * @return List of file names
	 */
	public List<String> getFilePartsAsList() 
	{
		return filesNamelist;
	}//getFilePartsAsList

	public String getMultipartResponseAssimilator()
	{
		System.out.println(">>> getMultipartResponseAssimilator() ::: Number of Parts = " + responseList.size());
		StringBuffer returnResponse = new StringBuffer("<CompleteMultipartUpload>");
		for (SQACanMultipartResponse item : responseList) 
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
		} // if
		responseList.add(item);
		System.out.println("Response List Size : " + responseList.size());
	}// addToResponseList
}// S3InputRecord