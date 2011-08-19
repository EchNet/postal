package com.shopximity.document;

import java.io.*;

public class FileDocumentSource
	implements DocumentSource
{
	public File baseFile;

	public FileDocumentSource()
	{
		this(".");
	}

	public FileDocumentSource(String basePath)
	{
		setBasePath(basePath);
	}

	public String getBasePath()
	{
		return baseFile.toString();
	}

	public void setBasePath(String basePath)
	{
		this.baseFile = new File(basePath);
	}

	public Reader open(String path)
		throws java.io.IOException
	{
		if (path.startsWith("/"))
			throw new IllegalArgumentException(path);
		if (!baseFile.isDirectory())
			throw new IOException(baseFile + ": not a directory");
		if (!baseFile.canRead())
			throw new IOException(baseFile + ": not readable");
		File file = new File(baseFile, path);
		return new BufferedReader(new FileReader(file));
	}
}
