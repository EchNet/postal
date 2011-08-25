package com.shopximity.ns;

// A truly read-only Map.
public interface Namespace
{
	public boolean defines(String name);
	public Object get(String name);
}
