package com.sqacanada;

public class SQACanUtils
{
	/**
	 * @param uploadId
	 * @return
	 */
	public static String getUploadId(String uploadId)
	{
		return uploadId.replace("<UploadId>", "").replace("</UploadId>", "");		
	}//getUploadId
	
	public static void main (String a[])
	{
		System.out.println(SQACanUtils.getUploadId("<UploadId>ZuBsBV83T6qf_6PukwiDTg</UploadId>"));
		System.out.println(SQACanUtils.getFQNFileName("Path1", "F1"));
		System.out.println(SQACanUtils.makePartTag("1", "eTag"));
	}
	
	/**
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static String getFQNFileName(String path, String fileName)
	{
		System.out.println(">>>getFQNFileName ::: path = " + path + "fileName = " + fileName);
		StringBuffer fqnFilename = new StringBuffer(path).append(fileName);
		System.out.println(">>>getFQNFileName ::: returns ==>   " + fqnFilename.toString());
		return fqnFilename.toString();
	}//getFQNFileName
	
	/*<CompleteMultipartUpload>
  <Part>
    <PartNumber>1</PartNumber>
    <ETag>"a54357aff0632cce46d942af68356b38"</ETag>
  </Part>
  <Part>
    <PartNumber>2</PartNumber>
    <ETag>"0c78aef83f66abc1fa1e8477f296d394"</ETag>
  </Part>
  <Part>
    <PartNumber>3</PartNumber>
    <ETag>"acbd18db4cc2f85cedef654fccc4a4d8"</ETag>
  </Part>
</CompleteMultipartUpload>*/
	public static String makePartTag(String partNumber, String eTag)
	{
		StringBuffer returnString = new StringBuffer("<Part>").
										append("<PartNumber>").append(partNumber).append("</PartNumber>").
										append("<ETag>\"").append(eTag).append("\"</ETag>").
										append("</Part>");
		return returnString.toString();
	}
}//SQACanUtils