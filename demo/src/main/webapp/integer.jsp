<%@page language="java" contentType="text/html; charset=UTF-8" %>
<%@page import="com.shopximity.asm.Assembler"%>
<%@page import="com.shopximity.math.IntegerFunFacts"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.*"%>
<%!
	Assembler assembler = new Assembler();
%>
<%
	// Parse request parameters to create data context for assembly.
	Map<String,Object> data = new HashMap<String,Object>();

	for (Map.Entry<String,String[] paramEntry : request.getParameterMap())
	{
		String key = paramEntry.getKey();
		Object value;
		if (key.equals("n"))
		{
			value = new IntegerFunFacts(paramEntry.getValue()[0]);
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
	URL templateUrl = new URL(request.getRequestURL().toString());
	templateUrl = new URL(templateUrl, "templates/page.xhtml");
	assembler.assemble(templateUrl, data, response.getWriter());
%>
