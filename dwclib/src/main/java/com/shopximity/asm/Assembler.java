package com.shopximity.asm;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class Assembler
{
    private static SAXParserFactory spf = SAXParserFactory.newInstance();
	static
	{
        spf.setNamespaceAware(true);
		spf.setValidating(false);
	}

	// TODO: monitor the size of this cache.
	private Map<URL,CacheEntry> cache = new HashMap<URL,CacheEntry>();

	public void assemble(URL url, Writer writer)
		throws AssemblerException
	{
		assemble(url, new Object(), writer);
	}

	public void assemble(URL url, Object data, Writer writer)
		throws AssemblerException
	{
		new DocumentContext(url, data, writer).expand(loadTemplate(url));
	}

	// TODO: synchronize.
	private Node loadTemplate(URL url)
		throws AssemblerException
	{
		if (cache.containsKey(url) && cache.get(url).time < System.currentTimeMillis() - 2000)
			cache.remove(url);
		if (!cache.containsKey(url))
		{
			TemplateHandler handler = new TemplateHandler();

			try
			{
				SAXParser parser = spf.newSAXParser();
				InputSource inputSource = new InputSource(url.openStream());
				parser.parse(inputSource, handler);
			}
			catch (Exception e)
			{
				while (e.getCause() != null && (e.getCause() instanceof Exception))
					e = (Exception) e.getCause();
				throw new AssemblerException(e);
			}

			if (handler.fatalError != null)
				throw new AssemblerException(handler.fatalError);

			cache.put(url, new CacheEntry(handler.root));
		}
		return cache.get(url).root;
	}

	private static class CacheEntry
	{
		CacheEntry(Node root)
		{
			this.root = root;
			this.time = System.currentTimeMillis();
		}
		Node root;
		long time;
	}

	private class TemplateHandler
		extends DefaultHandler
	{
		ElementNode root;
		ElementNode current;
		List<Exception> errors = new ArrayList<Exception>();
		List<Exception> warnings = new ArrayList<Exception>();
		Exception fatalError;

		@Override
		public void startDocument() {}

		@Override
		public void endDocument() {}

		@Override
		public void startElement (String uri, String localName, String qName, Attributes attributes)
		{
			openNestedElement();
			current.localName = localName;
			current.attributes = copyAttributes(attributes);
		}

		@Override
		public void endElement (String uri, String localName, String qName)
		{
			current = current.parent;
		}

		@Override
		public void startPrefixMapping(String prefix, String uri)
		{
			openNestedElement();
			current.addPrefixMapping(prefix, uri);
		}

		@Override
		public void endPrefixMapping(String prefix)
		{
		}

		@Override
		public void characters (char[] ch, int start, int length)
		{
			new TextNode(current, new String (ch, start, length));
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length)
		{
		}

		@Override
		public void notationDecl(String name, String publicId, String systemId)
		{
			throw new RuntimeException("not implemented: notationDecl " + name + " " +  publicId + " " + systemId);
		}

		@Override
		public void processingInstruction(String target, String data)
		{
			throw new RuntimeException("not implemented: processingInstr " + target + " " +  data);
		}

		@Override
		public InputSource resolveEntity(String publicId, String systemId)
		{
			return null;
		}

		@Override
		public void skippedEntity(String name)
		{ 
			throw new RuntimeException("not implemented: skippedEntity: " + name);
		}

		@Override
		public void setDocumentLocator(Locator locator) {}

		@Override
		public void warning(SAXParseException e)
		{
			warnings.add(e);
		}

		@Override
		public void error(SAXParseException e)
		{
			errors.add(e);
		}

		@Override
		public void fatalError(SAXParseException e)
		{
			fatalError = e;
		}

		private void openNestedElement()
		{
			if (current == null || current.localName != null)
			{
				current = new ElementNode(current);
				if (root == null) root = current;
			}
		}

		private Attr[] copyAttributes(Attributes attributes)
		{
			Attr[] attrArray = new Attr[attributes.getLength()];
			for (int i = 0; i < attrArray.length; ++i)
			{
				attrArray[i] = new Attr(attributes, i);
			}
			return attrArray;
		}
	}

	private static class Attr
	{
		String uri;
		String name;
		String value;

		Attr(Attributes attributes, int index)
		{
			uri = attributes.getURI(index);
			name = attributes.getLocalName(index);
			value = attributes.getValue(index);
		}
	}

	private class DocumentContext
	{
		URL url;
		Object data;
		Writer writer;

		public DocumentContext(URL url, Object data, Writer writer)
		{
			this.url = url;
			this.data = data;
			this.writer = writer;
		}

		public DocumentContext(DocumentContext parent, String name, Object value)
		{
			this.url = parent.url;
			// TODO: inherit all value.
			this.data = Collections.singletonMap(name, value);
			this.writer = parent.writer;
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

		public Object evaluate(String expression)
			throws AssemblerException
		{
			try
			{
				return new Evaluator().evaluate(expression, data);
			}
			catch (Exception e)
			{
				throw new AssemblerException(e);
			}
		}

		public void expandContent(String expression)
			throws AssemblerException, IOException
		{
			try
			{
				String includePath = expression;
				URL includeUrl = new URL(url, includePath);
				Node subTemplate = loadTemplate(includeUrl);
				subTemplate.expand(this, false);
			}
			catch (MalformedURLException e)
			{
				throw new AssemblerException(e);
			}
		}
	}

	private abstract class Node
	{
		ElementNode parent;

		Node(ElementNode parent)
		{
			this.parent = parent;
			if (parent != null)
			{
				parent.children.add(this);
			}
		}

		public abstract void expand(DocumentContext context, boolean includeRoot)
			throws AssemblerException, IOException;
	}

	private class ElementNode extends Node
	{
		List<Node> children = new ArrayList<Node>();
		String localName;
		Attr[] attributes;
		Map<String,String> prefixMappings = new HashMap<String,String>();

		ElementNode(ElementNode parent)
		{
			super(parent);
		}

		public void addPrefixMapping(String prefix, String uri)
		{
			prefixMappings.put(prefix, uri);
		}

		public void removePrefixMapping(String prefix)
		{
			prefixMappings.remove(prefix);
		}

		@Override
		public void expand(DocumentContext context, boolean includeRoot)
			throws AssemblerException, IOException
		{
			String conditionExpression = null;
			String repeatExpression = null;
			String contentExpression = null;
			String attributesExpression = null;

			for (Attr attr : attributes)
			{
				if (attr.uri.equals(Schema.NAMESPACE_URI))
				{
					if (attr.name.equals("content"))
					{
						contentExpression = attr.value;
					}
					else if (attr.name.equals("repeat"))
					{
						repeatExpression = attr.value;
					}
					else if (attr.name.equals("attributes"))
					{
						attributesExpression = attr.value;
					}
					else if (attr.name.equals("condition"))
					{
						conditionExpression = attr.value;
					}
					else
					{
						throw new AssemblerException(Schema.NAMESPACE_URI + ":" + attr.name + ": illegal attribute.");
					}
				}
			}

			if (conditionExpression != null &&
				!Evaluator.toBoolean(context.evaluate(conditionExpression)))
				return;

			Object[] values = new Object[] { new Object() };
			String loopVariable = null;
			if (repeatExpression != null)
			{
				int comma = repeatExpression.lastIndexOf(',');
				if (comma >= 0)
				{
					loopVariable = repeatExpression.substring(comma + 1);
					repeatExpression = repeatExpression.substring(0, comma);
				}
				Object value = context.evaluate(repeatExpression);
				if (value == null)
					return;
				if (value instanceof Collection<?>)
					values = (Object[]) ((Collection<?>)value).toArray(values);
				else
					values[0] = value;
			}

			for (Object v : values)
			{
				DocumentContext childContext = context;
				if (loopVariable != null)
				{
					childContext = new DocumentContext(context, loopVariable, v);
				}
				doExpand(childContext, contentExpression, includeRoot);
			}
		}

		private void doExpand(DocumentContext context,
							  String contentExpression, boolean includeRoot)
			throws AssemblerException, IOException
		{
			if (includeRoot)
			{
				context.writer.write("<");
				context.writer.write(localName);

				if (prefixMappings.containsKey(""))
				{
					context.writer.write(" xmlns=\"");
					context.writer.write(prefixMappings.get(""));
					context.writer.write("\"");
				}

				for (Attr attr : attributes)
				{
					// TODO: handle attribute overrides.
					if (!attr.uri.equals(Schema.NAMESPACE_URI))
					{
						context.writer.write(" ");
						context.writer.write(attr.name);
						context.writer.write("=\"");
						context.writer.write(attr.value);
						context.writer.write("\"");
					}
				}
			}

			if (contentExpression != null || children.size() > 0)
			{
				if (includeRoot)
				{
					context.writer.write(">");
				}
				if (contentExpression != null)
				{
					Object contentValue = context.evaluate(contentExpression);
					if (contentValue instanceof URI)
					{
						context.expandContent(((URI)contentValue).getSchemeSpecificPart());
					}
					else 
					{
						if (contentValue == null) contentValue = "";
						context.writer.write(contentValue.toString());
					}
				}
				else
				{
					for (Node child : children)
					{
						child.expand(context, true);
					}
				}
				if (includeRoot)
				{
					context.writer.write("</" + localName + ">");
				}
			}
			else if (includeRoot)
			{
				context.writer.write("/>");
			}
		}
	}

	private class TextNode extends Node
	{
		String characters;

		TextNode(ElementNode parent, String characters)
		{
			super(parent);
			this.characters = characters;
		}

		@Override
		public void expand(DocumentContext context, boolean includeRoot)
			throws IOException
		{
			context.writer.write(characters);
		}
	}
}
