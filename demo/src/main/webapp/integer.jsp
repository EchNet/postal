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
	if (request.getParameter("n") != null)
	{
		data.put("n", new IntegerFunFacts(Integer.parseInt(request.getParameter("n"))));
	}

	// Form URL of root template.
	URL templateUrl = new URL(request.getRequestURL().toString());
	templateUrl = new URL(templateUrl, "templates/page.xhtml");
	assembler.assemble(templateUrl, data, response.getWriter());
%>
