package com.shopximity.asm;

public class AssemblerException
	extends Exception
{
	public AssemblerException()
	{
	}

	public AssemblerException(String msg)
	{
		super(msg);
	}

	public AssemblerException(Throwable cause)
	{
		super(cause);
	}

	public AssemblerException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
}
