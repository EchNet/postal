
package com.shopximity.asm;

import com.shopximity.document.DocumentSource;
import com.shopximity.document.ClassPathDocumentSource;
import com.shopximity.eval.ExpressionEvaluator;
import java.io.Reader;

public class Assembler
{
	public ExpressionEvaluator getExpressionEvaluator()
	{
		return new ExpressionEvaluator();
	}

	public DocumentSource getDocumentSource()
	{
		return new ClassPathDocumentSource();
	}

	public String assemble(String path)
		throws AssemblerException
	{
		try
		{
			Reader reader = getDocumentSource().open(path);
			return doAssemble(reader);
		}
		catch (AssemblerException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new AssemblerException(e);
		}
	}

	private String doAssemble(Reader reader)
		throws AssemblerException
	{
		throw new AssemblerException("NYI");
	}
}
