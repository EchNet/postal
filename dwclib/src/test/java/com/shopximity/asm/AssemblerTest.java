package com.shopximity.asm;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AssemblerTest
{
	Assembler assembler; 
	Map data;

	@Before
	public void setUp()
	{
		assembler = new Assembler();
		data = new HashMap();
		data.put("user", new User("Fred"));
		data.put("users", Arrays.asList(new User[] { new User("Fred"), new User("Sam"), new User("Alex") }));
	}

	private static class User
	{
		private String name;

		public User(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	@Test
	public void testBadInputPath() throws Exception
	{
		final String BAD_PATH = "no-such";

		Throwable error = null;
		try
		{
			assembler.assemble(createClassPathUrl(BAD_PATH), new StringWriter());
		}
		catch (AssemblerException e)
		{
			error = e.getCause();
		}
		assertEquals(BAD_PATH, error.getMessage());
	}

	@Test
	public void testMinimalTemplate() throws Exception
	{
		final String INPUT = "<html xmlns=\"http://www.w3.org/1999/xhtml\"/>";
		final String EXPECTED_OUTPUT = "<!DOCTYPE html>\n" +
                      "<html xmlns=\"http://www.w3.org/1999/xhtml\"/>";

		StringWriter output = new StringWriter();
		assembler.assemble(createLiteralUrl(INPUT), output);
		assertEquals(EXPECTED_OUTPUT, output.toString());
	}

	@Test
	public void testMissingDocumentElement() throws Exception
	{
		final String INPUT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

		Throwable error = null;
		try
		{
			assembler.assemble(createLiteralUrl(INPUT), new StringWriter());
		}
		catch (AssemblerException e)
		{
			error = e.getCause();
		}
		assertNotNull(error);
	}

	@Test
	public void testSimple() throws Exception
	{
		verifyAssembly("simple");
	}

	@Test
	public void testIncludes() throws Exception
	{
		verifyAssembly("includes");
	}

	@Test
	public void testRepeats() throws Exception
	{
		verifyAssembly("repeats");
	}

	@Test
	public void testConditions() throws Exception
	{
		verifyAssembly("conditions");
	}

	private URL createLiteralUrl(final String text) throws Exception
	{
		return new URL("literal", "", 0, "", new URLStreamHandler() {
			protected URLConnection openConnection(URL url) {
				return new URLConnection(url) {
					public void connect() 
					{
						connected = true;
					}

					public InputStream getInputStream() throws java.io.IOException
					{
						connect();
						return new ByteArrayInputStream(text.getBytes("UTF-8"));
					}
				};
			}
		});
	}

	private URL createClassPathUrl(String path) throws Exception
	{
		return new URL("classpath", "", 0, path, new URLStreamHandler() {
			protected URLConnection openConnection(URL url) {
				return new URLConnection(url) {
					InputStream inputStream;

					public void connect() throws java.io.IOException
					{
						if (!connected)
						{
							inputStream = AssemblerTest.class.getClassLoader().getResourceAsStream(url.getFile());
							if (inputStream == null)
								throw new java.io.IOException(url.getFile());
							connected = true;
						}
					}

					public InputStream getInputStream() throws java.io.IOException
					{
						connect();
						return inputStream;
					}
				};
			}
		});
	}

	private String loadText(URL url) throws Exception
	{
		byte[] bytes = new byte[1024];
		InputStream inputStream = new BufferedInputStream(url.openStream());
		int b;
		int bcount = 0;
		while ((b = inputStream.read()) >= 0)
		{
			if (bcount == bytes.length)
			{
				byte[] newBytes = new byte[bytes.length * 2];
				System.arraycopy(bytes, 0, newBytes, 0, bcount);
				bytes = newBytes;
			}
			bytes[bcount++] = (byte) b;
		}
		String str = new String(bytes, 0, bcount, "UTF-8");
		if (str.endsWith("\n")) str = str.substring(0, str.length() - 1);
		return str;
	}

	private void verifyAssembly(String templateName) throws Exception
	{
		StringWriter output = new StringWriter();
		assembler.assemble(createClassPathUrl("templates/" + templateName + ".xhtml"), data, output);
		String expectedOutput = loadText(createClassPathUrl("solutions/" + templateName + ".html"));
		assertEquals(expectedOutput, output.toString());
	}
}
