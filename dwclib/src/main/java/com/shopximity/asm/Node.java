package com.shopximity.asm;

import java.io.IOException;

abstract class Node
{
	private ElementNode parent;

	public Node(ElementNode parent)
	{
		this.parent = parent;
		if (parent != null)
		{
			parent.children.add(this);
		}
	}

	public ElementNode getParent()
	{
		return parent;
	}

	public abstract void expand(DocumentContext context, boolean includeRoot)
		throws AssemblerException;
}
