package com.shopximity.asm;

import org.xml.sax.Attributes;

class Attr
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

