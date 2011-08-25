package com.shopximity.asm;

import java.util.Map;

interface DocumentContext
{
	public DocumentContext createChildContext(Map locals);

	public void write(String str)
		throws AssemblerException;

	public void write(char[] chars, int off, int len)
		throws AssemblerException;

	public Object evaluateExpression(String expression)
		throws AssemblerException;

	public String expandText(String text)
		throws AssemblerException;

	public void includeComponent(String includePath)
		throws AssemblerException;
}
