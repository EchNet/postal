package com.shopximity.ns;

public class AbstractNamespace
    implements Namespace
{
	private Namespace parent;

	public AbstractNamespace()
	{
		this(null);
	}

	public AbstractNamespace(Namespace parent)
	{
		this.parent = parent;
	}

	public boolean defines(String name)
	{
		AbstractNamespace ans = this;
		for (;;)
		{
			if (ans.definesLocally(name))
				return true;
			Namespace ns = ans.parent;
			if (ns == null)
				break;
			if (ns instanceof AbstractNamespace)
				ans = (AbstractNamespace) ns;
			else
				return ns.defines(name);
		}
		return false;
	}

	public Object get(String name)
	{
		AbstractNamespace ans = this;
		for (;;)
		{
			if (ans.definesLocally(name))
				return ans.getLocally(name);
			Namespace ns = ans.parent;
			if (ns == null)
				break;
			if (ns instanceof AbstractNamespace)
				ans = (AbstractNamespace) ns;
			else
				return ns.get(name);
		}
		return null;
	}

	protected boolean definesLocally(String name)
	{
		return false;
	}

	protected Object getLocally(String name)
	{
		return null;
	}
}
