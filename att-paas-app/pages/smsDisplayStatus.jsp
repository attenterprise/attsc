<%@ page import="com.att.enablers.gsms.*;" %> 

<html> 
                                                         
<head>
</head>

<body>
<div width=60% align=center>
<h2>Global Smart Messaging Suite</h2>

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
			String destination = msg.getDestination();
			if (destination != null){
%>
				<tr><td align=right width=50%>Destination:</td><td align=left><%=destination%></td></tr>
<%
			}
			String destination_code = msg.getDestinationCode();
			if (destination_code != null){
%>
				<tr><td align=right width=50%>Destination Code:</td><td align=left><%=destination_code%></td></tr>
<%
			}
			String destination_descr = msg.getDestinationDescription();
			if (destination_descr != null){
%>
				<tr><td align=right width=50%>Destination Description:</td><td align=left><%=destination_descr%></td></tr>
<%
			}
			String batch_id = msg.getBatchID();
			if (batch_id != null){
%>
				<tr><td align=right width=50%>Batch ID:</td><td align=left><%=batch_id%></td></tr>
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
	String gsms_response_str = (String)params.get("gsms_response_str");
	if (gsms_response_str != null){
%>
		<p><%=gsms_response_str%></p>
<%
   	}
   	else{
   %>

<p>Status is not available.</p>

<% 
	} 
}	
%>

</div>
</body>

</html>