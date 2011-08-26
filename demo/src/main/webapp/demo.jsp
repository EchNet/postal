<%@page language="java" contentType="text/html; charset=UTF-8" %>
<%@page import="com.shopximity.asm.Assembler"%>
<%@page import="com.shopximity.ns.MapBasedNamespace"%>
<%@page import="com.shopximity.math.IntegerFunFacts"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.*"%>
<%!
	final static String ROOT_TEMPLATE_PATH = "templates/page.xhtml";

	static Assembler assembler = new Assembler();
%>
<%
	// Parse request parameters to create data context for assembly.
	Map<String,Object> data = new HashMap<String,Object>();

	for (Object pe : request.getParameterMap().entrySet())
	{
		// Until we move up to Java EE 6...
		Map.Entry<String,String[]> paramEntry = (Map.Entry<String,String[]>) pe;
		String key = paramEntry.getKey();
		Object value;
		if (key.equals("n"))
		{
			value = new IntegerFunFacts(Integer.parseInt(paramEntry.getValue()[0]));
		}
		else
		{
			String[] values = paramEntry.getValue();
			if (values.length == 1)
				value = values[0];
			else
				value = values;
		}
		data.put(key, value);
	}

	// Form URL of root template.
	URL templateUrl = new URL(new URL(request.getRequestURL().toString()), ROOT_TEMPLATE_PATH);

	// Assemble the root component.
	assembler.assemble(templateUrl, new MapBasedNamespace(data), response.getWriter());
%>
