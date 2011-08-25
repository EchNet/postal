package com.shopximity.asm;

import java.io.IOException;

class TextNode extends Node
{
	private String characters;

	public TextNode(ElementNode parent, String characters)
	{
		super(parent);
		this.characters = characters;
	}

	@Override
	public void expand(DocumentContext context, boolean includeRoot)
		throws AssemblerException
	{
		context.write(characters);
	}
}
