package com.shopximity.asm;

import org.junit.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AssemblerTest
{
	Assembler assembler; 

	@Before
	public void setUp()
	{
		assembler = new Assembler();
	}

	@Test
	public void testDefaultExpressionEvaluator()
	{
		assertNotNull(assembler.getExpressionEvaluator());
	}

	@Test
	public void testDefaultDocumentSource()
	{
		assertNotNull(assembler.getDocumentSource());
	}

	@Test
	public void testBadInputPath()
	{
		Throwable error = null;
		try
		{
			assembler.assemble("no-such-component-template");
		}
		catch (AssemblerException e)
		{
			error = e.getCause();
		}
		assertTrue(error instanceof java.io.IOException);
	}
}
