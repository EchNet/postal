package com.shopximity.ns;

import java.util.Map;

// Map is assumed to be read-only.
public class MapBasedNamespace
    extends AbstractNamespace
    implements Namespace
{
	private Map<String,Object> map;

	public MapBasedNamespace(Map<String,Object> map)
	{
		this.map = map;
	}

	public MapBasedNamespace(Namespace parent, Map<String,Object> map)
	{
		super(parent);
		this.map = map;
	}

	protected boolean definesLocally(String name)
	{
		return map.containsKey(name);
	}

	protected Object getLocally(String name)
	{
		return map.get(name);
	}
}
