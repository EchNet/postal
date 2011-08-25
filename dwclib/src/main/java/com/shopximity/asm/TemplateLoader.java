package com.shopximity.asm;

import java.net.URL;
import java.util.*;

//
// Manage parsing and caching of templates.
//
class TemplateLoader
{
	// TODO: monitor the size of this cache.
	private Map<URL,CacheEntry> cache = new HashMap<URL,CacheEntry>();

	// TODO: synchronize effectively.
	public Node loadTemplate(URL url)
		throws AssemblerException
	{
		if (cache.containsKey(url) && cache.get(url).time < System.currentTimeMillis() - 2000)
			cache.remove(url);
		if (!cache.containsKey(url))
		{
			TemplateParser parser = new TemplateParser();
			cache.put(url, new CacheEntry(parser.parseTemplate(url)));
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
}
