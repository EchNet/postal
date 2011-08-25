package com.shopximity.asm;

import com.shopximity.ns.*;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Assembler
{
	private TemplateLoader templateLoader = new TemplateLoader();

	public void assemble(URL url, Writer writer)
		throws AssemblerException
	{
		assemble(url, new AbstractNamespace(), writer);
	}

	public void assemble(URL url, Namespace data, Writer writer)
		throws AssemblerException
	{
		new AsmDocumentContext(url, data, writer).expand(templateLoader.loadTemplate(url));
	}

	private class AsmDocumentContext
		implements DocumentContext
	{
		private URL url;
		private Namespace data;
		private Writer writer;

		public AsmDocumentContext(URL url, Namespace data, Writer writer)
		{
			this.url = url;
			this.data = data;
			this.writer = writer;
		}

		public DocumentContext createChildContext(Map locals)
		{
			return new AsmDocumentContext(url, new MapBasedNamespace(data, locals), writer);
		}

		public void write(String str)
			throws AssemblerException
		{
			try
			{
				writer.write(str);
			}
			catch (IOException e)
			{
				throw new AssemblerException(e);
			}
		}

		public void write(char[] chars, int off, int len)
			throws AssemblerException
		{
			try
			{
				writer.write(chars, off, len);
			}
			catch (IOException e)
			{
				throw new AssemblerException(e);
			}
		}
		
		public void expand(Node rootNode)
			throws AssemblerException
		{
			try
			{
				writer.write("<!DOCTYPE html>\n");
				rootNode.expand(this, true);
				writer.flush();
			}
			catch (Exception e)
			{
				while (e.getCause() != null && (e.getCause() instanceof Exception))
					e = (Exception) e.getCause();
				throw new AssemblerException(e);
			}
		}

		/**
		 * Evaluate an expression string to produce a value.
		 */
		public Object evaluateExpression(String expression)
			throws AssemblerException
		{
			return new Evaluator().evaluate(expression, data);
		}

		/**
		 * Expand a string that may contain expression tokens.
		 */
		public String expandText(String text)
			throws AssemblerException
		{
			StringBuilder outbuf = null; 
			for (int pos = 0;;)
			{
				int delim = text.indexOf("{{", pos);
				int endDelim = delim >= 0 ? text.indexOf("}}", delim + 2) : -1;
				if (delim < 0 || endDelim < 0)
				{
					if (outbuf == null) return text;
					outbuf.append(text.substring(pos));
					break;
				}
				outbuf = new StringBuilder();
				outbuf.append(text.substring(pos, delim));
				outbuf.append(Evaluator.toString(evaluateExpression(text.substring(delim + 2, endDelim))));
				pos = endDelim + 2;
			}

			return outbuf.toString();
		}

		/**
		 * Expand a template within the current context.
		 */
		public void includeComponent(String includePath)
			throws AssemblerException
		{
			try
			{
				URL includeUrl = new URL(url, includePath);
				Node subTemplate = templateLoader.loadTemplate(includeUrl);
				subTemplate.expand(this, false);
			}
			catch (MalformedURLException e)
			{
				throw new AssemblerException(e);
			}
		}
	}
}
