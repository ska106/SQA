package com.sqacanada;

public class SQACanMultipartResponse 
{
	public String name;
	public String eTag ;
	
	public SQACanMultipartResponse(){}
	
	/**
	 * @param name
	 * @param eTag
	 */
	public SQACanMultipartResponse(String name, String eTag)
	{
		super();
		this.name = name;
		this.eTag = eTag;
	}//SQACanMultipartResponse

	@Override
	public String toString() 
	{
		return "<SQACanMultipartResponse> [name=" + name + ", eTag=" + eTag + "]";
	}//toString
}//SQACanMultipartResponse