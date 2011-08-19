package com.shopximity.document;

import java.io.*;

public class ClassPathDocumentSource
	implements DocumentSource
{
	public Reader open(String path)
		throws java.io.IOException
	{
		if (path.startsWith("/"))
			throw new IllegalArgumentException(path);
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
		if (inputStream == null)
			throw new java.io.IOException(path + ": no such resource");
		return new InputStreamReader(inputStream);
	}
}
