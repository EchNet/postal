package com.shopximity.asm;

import java.net.URL;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

//
// Parsing of templates, based on SAX.
//
class TemplateParser
{
    private static SAXParserFactory spf = SAXParserFactory.newInstance();
	static
	{
        spf.setNamespaceAware(true);
		spf.setValidating(false);
	}

	public ElementNode parseTemplate(URL url)
		throws AssemblerException
	{
		SaxCallbackHandler handler = new SaxCallbackHandler();

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

		return handler.root;
	}

	private class SaxCallbackHandler
		extends DefaultHandler
	{
		ElementNode root;
		ElementNode current;
		// TODO: handle errors and warnings.
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
			current = current.getParent();
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
}
