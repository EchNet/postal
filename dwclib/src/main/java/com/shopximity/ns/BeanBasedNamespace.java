package com.shopximity.ns;

import java.lang.reflect.*;

// Bean is assumed not to be writable by us, but possibly mutable.
public class BeanBasedNamespace
	extends AbstractNamespace
	implements Namespace
{
	// Don't use the Introspector - it's slow.  Call getMethod as needed and cache the
	// results.
	//private static Map<ClassAndName,Method> methodCache = new HashMap<ClassAndName,Method>();
	private Object bean;

	public BeanBasedNamespace(Object bean)
	{
		this.bean = bean;
	}

	public BeanBasedNamespace(Namespace parent, Object bean)
	{
		super(parent);
		this.bean = bean;
	}

	protected boolean definesLocally(String name)
	{
		return findMethod(name) != null;
	}

	protected Object getLocally(String name)
	{
		Method meth = findMethod(name);
		if (meth == null)
			return null;
		try
		{
			return meth.invoke(bean, null);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e)
		{
			throw new RuntimeException(e.getCause());
		}
	}

	// TODO: implement caching.
	private Method findMethod(String name)
	{
		Method meth = null;
		for (String prefix : new String[] { "get", "is" })
		{
			String methName = prefix + Character.toUpperCase(name.charAt(0)) + name.substring(1);
			try
			{
				meth = bean.getClass().getMethod(methName, new Class[0]);
				if (meth != null)
					break;
			}
			catch (NoSuchMethodException e)
			{
			}
		}
		return meth;
	}
}
