package com.shopximity.jpt;

import junit.framework.TestCase;
import java.io.*;

public class FirstTest extends TestCase
{
	public void setUp() throws Exception
	{
	}

	public void testResource() throws Exception
	{
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream("templates/page.xhtml");
		int b = stream.read();
		assertTrue(b > 0);
	}
}
