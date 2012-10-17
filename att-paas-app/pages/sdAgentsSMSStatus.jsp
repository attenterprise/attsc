<%@ page import="com.att.enablers.gsms.*;" %> 

<html> 
                                                         
<head>
</head>

<body>
<div width=60% align=center>
<h2>Agent - Send SMS Status</h2>

<%
java.util.HashMap params = new java.util.HashMap();
if (controllerResponse != null) {
	params = (java.util.HashMap)controllerResponse.getData();
}

GSMSResponse gsms_response = (GSMSResponse)params.get("gsms_response");
if (gsms_response != null){
	List<GSMSResponseMessage> msg_list = gsms_response.getMessages();
	if (msg_list != null){

%>

<table width=100% >

<%
		for (GSMSResponseMessage msg: msg_list){
			String message_id = msg.getMessageID();
			if (message_id != null){
%>
				<tr><td align=right width=50%>Message ID:</td><td align=left><%=message_id%></td></tr>
<%			
			}
			int error_code = msg.getErrorCode();
			if (error_code > 0){
%>
				<tr><td align=right width=50%>Error Code:</td><td align=left><%=String.valueOf(error_code)%></td></tr>
<%
			}
			String response_code = msg.getResponseCode();
			if (response_code != null){
%>
				<tr><td align=right width=50%>Response Code:</td><td align=left><%=response_code%></td></tr>
<%
			}
			String response_msg = msg.getResponseMessage();
			if (response_msg != null){
%>
				<tr><td align=right width=50%>Response Message:</td><td align=left><%=response_msg%></td></tr>
<%
			}
%>
			<tr><td colspan=2>&nbsp;</td></tr>
<%
		}
%>

</table>

<% 
	}
}else{ 

%>

<p>Status is not available.</p>

<% 
}	
%>

</div>
</body>

</html>