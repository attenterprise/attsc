<!--
	Licensed by AT&T under AT&T Public Source License Version 1.0.' 2012
	
	TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: http://developer.att.com/apsl-1.0
	Copyright 2012 AT&T Intellectual Property. All rights reserved. http://pte.att.com/index.aspx
	For more information contact: g15287@att.att-mail.com
-->
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