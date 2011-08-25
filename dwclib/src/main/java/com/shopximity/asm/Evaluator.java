package com.shopximity.asm;

import com.shopximity.ns.*;
import java.io.*;
import java.util.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

public class Evaluator
{
	public static String toString(Object obj)
	{
		if (obj == null) return "";
		return  obj.toString();
	}

	public static boolean toBoolean(Object obj)
	{
		return !(obj == null ||
			     obj.equals(Boolean.FALSE) ||
			     obj.equals("") ||
				 obj.equals(Collections.emptyList()));
	}

	public Object evaluate(String expression, Namespace data)
		throws AssemblerException
	{
		try
		{
			TokenStream tStream = new TokenStream(expression);

			if (tStream.peek(1) != null && tStream.peek(0).type == StreamTokenizer.TT_WORD && tStream.peek(1).type == ':')
			{
				return new URI(expression);
			}

			Node node = new ExpressionState().parse(tStream);
			Token t = tStream.pop();
			if (t != null) 
				throw new ParseException("dangling crud", 0);

			if (node != null)
			{
				return node.evaluate(data);
			}

			return null;
		}
		catch (URISyntaxException e)
		{
			throw new AssemblerException(e);
		}
		catch (ParseException e)
		{
			throw new AssemblerException(e);
		}
		catch (IOException e)
		{
			throw new AssemblerException(e);
		}
	}

	private abstract static class ParseState
	{
		abstract Node parse(TokenStream tStream)
			throws ParseException, IOException;
	}

	private static class ExpressionState extends ParseState
	{
		Node parse(TokenStream tStream)
			throws ParseException, IOException
		{
			Token t = tStream.peek(0);
			if (t == null) return null;
			switch (t.type)
			{
			case '!':
				tStream.pop();
				NotNode nn = new NotNode();
				nn.child = parse(tStream);
				return nn;
			default:
				return new TermState().parse(tStream);
			}
		}
	}

	private static class TermState extends ParseState
	{
		Node parse(TokenStream tStream)
			throws ParseException, IOException
		{
			Token t = tStream.peek(0);
			if (t == null) return null;
			switch (t.type)
			{
			case '(':
				tStream.pop();
				Node node = new ExpressionState().parse(tStream);
				t = tStream.pop();
				if (t == null) throw new ParseException("missing closing paren", 0);
				if (t.type != ')') throw new ParseException("that's not a closing paren", 0);
				return node;
			case StreamTokenizer.TT_WORD:
				return new ValueState().parse(tStream);
			default:
				throw new ParseException("expected something else", 0);
			}
		}
	}

	private static class ValueState extends ParseState
	{
		Node parse(TokenStream tStream)
			throws ParseException, IOException
		{
			Node node = null;
			for (;;)
			{
				Token t = tStream.pop();
				if (t == null || t.type != StreamTokenizer.TT_WORD)
					throw new ParseException("expected name", 0);
				NameRefNode newNode = new NameRefNode();
				newNode.lhs = node;
				newNode.name = t.sval;
				node = newNode;
				t = tStream.peek(0);
				if (t == null || t.type != '.')
					break;
				tStream.pop();
			}

			return node;
		}
	}

	private abstract static class Node
	{
		public abstract Object evaluate(Namespace data);
	}

	private static class NotNode extends Node
	{
		Node child;

		public Object evaluate(Namespace data)
		{
			return !toBoolean(child.evaluate(data));
		}
	}

	private static class NameRefNode extends Node
	{
		Node lhs;
		String name;

		public Object evaluate(Namespace data)
		{
			Namespace namespace = null;
			if (lhs == null) 
				namespace = data;
			else
			{
				Object result = lhs.evaluate(data);
				if (result != null)
				{
					if (result instanceof Namespace)
						namespace = (Namespace) result;
					else if (result instanceof Map)
						namespace = new MapBasedNamespace((Map<String,Object>)result);
					else
						namespace = new BeanBasedNamespace(result);
				}
			}
			return namespace == null ? null : namespace.get(name);
		}
	}

	private static class TokenStream
	{
		private String expression;
		private StreamTokenizer sTok;
		private List<Token> buffer = new ArrayList<Token>();

		public TokenStream(String expression)
		{
			sTok = new StreamTokenizer(new StringReader(expression));
			sTok.parseNumbers();
			sTok.whitespaceChars(0, ' ');
			sTok.ordinaryChar('.');
		}

		public Token peek(int index)
			throws IOException
		{
			fillBuffer(index);
			return buffer.size() <= index ? null : buffer.get(index);
		}

		public Token pop()
			throws IOException
		{
			Token t = peek(0);
			if (t != null)
				buffer.remove(0);
			return t;
		}

		private void fillBuffer(int index)
			throws IOException
		{
			while (buffer.size() <= index && sTok.nextToken() != StreamTokenizer.TT_EOF)
			{
				buffer.add(new Token(sTok.ttype, sTok.sval));
			}
		}
	}

	private static class Token
	{
		int type;
		String sval;

		Token (int type, String sval)
		{
			this.type = type;
			this.sval = sval;
		}
	}
}
