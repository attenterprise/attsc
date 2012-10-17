<%@ page import="com.att.enablers.locaid.*" %> 
<%@ page import="com.att.enablers.locaid.response.*" %> 

<html> 
                                                         
<head>
</head>

<body>
<div width=60% align=center>
<h2>Location Information Services powered by LocAid</h2>
<h2>Geo-fencing Result</h2>
<form >

<%
java.util.HashMap params = new java.util.HashMap();
String origTransactionId = null;
if (controllerResponse != null) {
	params = (java.util.HashMap)controllerResponse.getData();
	origTransactionId = (String)params.get("origTransactionId");
}

BaseTransactionResponseBean geoFencingResponseObj = (BaseTransactionResponseBean)params.get("locaid_response");
if (geoFencingResponseObj != null){
	Long transaction_id = geoFencingResponseObj.getTransactionId();
%>

<input type=hidden name='action' value='getGeoFenceAnswer' />
<input type=hidden name='origTransactionId' value='<%=origTransactionId%>' />

<table width=100% >

<%
	BaseErrorResponseBean error = geoFencingResponseObj.getError();
	if (error != null) {
		String code = error.getErrorCode();
		String message = error.getErrorMessage();
%>
		<tr><td align=right width=50%>Error Code:</td><td align=left><%=code%></td></tr>
		<tr><td align=right width=50%>Error Message:</td><td align=left><%=message%></td></tr>
<%
	} 
	else{
%>
		<tr><td colspan=2 align=center><input type="submit" name="go" value="Request Result" /></td></tr>
		<tr><td colspan=2>&nbsp;</td></tr>
<%
	}
%>

<tr><td align=right width=50%>Transaction ID:</td><td align=left><%=String.valueOf(transaction_id)%></td></tr>
</table>

<% 

}else{ 

 %>

<p>Geo-fencing information is not available.</p>

<% }	%>

</div>
</body>

</html>