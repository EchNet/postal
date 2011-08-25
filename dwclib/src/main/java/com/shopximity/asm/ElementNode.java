package com.shopximity.asm;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import org.xml.sax.*;

class ElementNode extends Node
{
	List<Node> children = new ArrayList<Node>();
	String localName;
	Attr[] attributes;
	Map<String,String> prefixMappings = new HashMap<String,String>();

	public ElementNode()
	{
		this(null);
	}

	public ElementNode(ElementNode parent)
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
		throws AssemblerException
	{
		String conditionExpression = null;
		String repeatExpression = null;
		String contentExpression = null;
		String includePath = null;
		String attributesExpression = null;

		for (Attr attr : attributes)
		{
			if (attr.uri.equals(Schema.NAMESPACE_URI))
			{
				if (attr.name.equals("content"))
				{
					contentExpression = attr.value;
				}
				else if (attr.name.equals("include"))
				{
					includePath = attr.value;
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

		if (contentExpression != null && includePath != null)
		{
			throw new AssemblerException("content and include attrs are mutually exclusive.");
		}

		// Handle dwc:condition attribute.
		if (conditionExpression != null &&
			!Evaluator.toBoolean(context.evaluateExpression(conditionExpression)))
			return;

		// Handle dwc:repeat attribute.
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
			Object value = context.evaluateExpression(repeatExpression);
			if (value == null)
				return;  // act like condition==false.
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
				childContext = context.createChildContext(Collections.singletonMap(loopVariable, v));
			}
			doExpand(childContext, contentExpression, includePath, attributesExpression, includeRoot);
		}
	}

	private void doExpand(DocumentContext context,
						  String contentExpression,
						  String includePath,
						  String attributesExpression,
						  boolean includeRoot)
		throws AssemblerException
	{
		// TODO: emit warning/error if dwc:attributes appear in root of included element?
		if (includeRoot)
		{
			context.write("<");
			context.write(localName);

			if (prefixMappings.containsKey(""))
			{
				context.write(" xmlns=\"");
				context.write(prefixMappings.get(""));
				context.write("\"");
			}

			Set attrOverrides = new HashSet<String>();
			if (attributesExpression != null)
			{
				// TODO: clean this up.
				for (StringTokenizer toks = new StringTokenizer(attributesExpression, ",");
					 toks.hasMoreTokens(); )
				{
					// TODO: detect duplicate attribute error.
					String attrName = toks.nextToken();
					String attrValue = context.expandText(toks.nextToken());
					writeAttribute(context, attrName, attrValue);
					attrOverrides.add(attrName);
				}
			}

			for (Attr attr : attributes)
			{
				if (attrOverrides.contains(attr.name)) continue;
				if (!attr.uri.equals(Schema.NAMESPACE_URI))
				{
					writeAttribute(context, attr.name, attr.value);
				}
			}
		}

		if (contentExpression != null || children.size() > 0)
		{
			if (includeRoot)
			{
				context.write(">");
			}
			if (contentExpression != null)
			{
				context.write(context.expandText(contentExpression));
			}
			else if (includePath != null)
			{
				context.includeComponent(context.expandText(includePath));
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
				context.write("</" + localName + ">");
			}
		}
		else if (includeRoot)
		{
			context.write("/>");
		}
	}

	private void writeAttribute(DocumentContext context, String attrName, String attrValue)
		throws AssemblerException
	{
		// TODO: escape special characters.
		context.write(" ");
		context.write(attrName);
		context.write("=\"");
		context.write(attrValue);
		context.write("\"");
	}
}

