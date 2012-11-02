<%@ page import="com.att.enablers.locaid.*"%>
<%@ page import="com.att.enablers.locaid.response.*"%>
<%@ page import="java.lang.String"%>

<html>

<head>
</head>

<body>
	<div width=60% align=center>
		<%
		  java.util.HashMap params = new java.util.HashMap();
		  if (controllerResponse != null) {
		    params = (java.util.HashMap) controllerResponse.getData();
		  }
		  
		  String message = params.containsKey("message") ? (String)params.get("message") : "";
		  String errorMessage = params.containsKey("error_message") ? (String)params.get("error_message") : "";
		  String details = params.containsKey("details") ? (String)params.get("details") : "";
		%>
		
		<h2><%= message %></h2>
		<h2 style="color: red;"><%= errorMessage %></h2>
		<%= details %>
	</div>
</body>

</html>