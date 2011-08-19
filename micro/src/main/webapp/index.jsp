<%@page language="java" contentType="text/html; charset=UTF-8" %>
<%@page import="com.shopximity.Data"%>
<%@page import="java.util.Map"%>
<%!
	static Data data = new Data();
%>
<%
	int n = request.getParameter("n") == null ? 1 : Integer.parseInt(request.getParameter("n"));
	Map<String,Object> currentItem = data.get(n);
%>
<head><title>MICRO</title></head>
<body>
<h1><%= currentItem.get("title") %></h1>
<% if (currentItem.containsKey("prime")) { %><h2>A prime number</h2><% } %>
<% if (currentItem.containsKey("base")) { %><div style="margin: 30px;"><span style="font-size: 48px; color: blue;"><%= currentItem.get("base") %><sup><%= currentItem.get("exponent") %></sup></span></div><% } %>
<p>Represented as <%= currentItem.get("decimal") %> in decimal, <%= currentItem.get("hexadecimal") %> in
hexadecimal, and <%= currentItem.get("octal") %> in octal.</p>
<% if (!currentItem.containsKey("prime") && !currentItem.containsKey("base")) { %>
<p>Some facts about <%= currentItem.get("value") %>...</p>
<ul>
<li>The sum of its proper divisors is <%= currentItem.get("sumOfProperDivisors") %></li>
<li>RAD(<%= currentItem.get("value") %>) = <%= currentItem.get("rad") %></li>
</ul>
<% } %>
</body>
