<%@ page import="com.att.enablers.locaid.*" %> 
<%@ page import="com.att.enablers.locaid.response.*" %> 

<html> 
                                                         
<head>
</head>

<body>
<div width=60% align=center>
<h2>Location Information Services powered by LocAid</h2>
<h2>Phone Subscription Status</h2>

<%
java.util.HashMap params = new java.util.HashMap();
if (controllerResponse != null) {
	params = (java.util.HashMap)controllerResponse.getData();
}

PhoneStatusListResponseBean statusResponseObj = (PhoneStatusListResponseBean)params.get("locaid_response");
if (statusResponseObj != null){
	Long transaction_id = statusResponseObj.getTransactionId();
%>

<table width=100% >
<tr><td align=right width=50%>Transaction ID:</td><td align=left><%=transaction_id%></td></tr>

<%
	BaseErrorResponseBean error = statusResponseObj.getError();
	if (error != null) {
		String code = error.getErrorCode();
		String message = error.getErrorMessage();
%>
		<tr><td align=right width=50%>Error Code:</td><td align=left><%=code%></td></tr>
		<tr><td align=right width=50%>Error Message:</td><td align=left><%=message%></td></tr>
<%
	} 
	else{
		List<ComplexMsisdnResponseBean> msisdnResponseList = statusResponseObj.getMsisdnList();
		if (msisdnResponseList != null){
			for (ComplexMsisdnResponseBean msisdnResponseObj : msisdnResponseList) {
				String msisdnReturned = msisdnResponseObj.getMsisdn();
%>
				<tr><td colspan=2>&nbsp;</td></tr>
				<tr><td align=right width=50%>MSISDN:</td><td align=left><%=msisdnReturned%></td></tr>
<%

				error = msisdnResponseObj.getError();
				if (error != null) {
					String code = error.getErrorCode();
					String message = error.getErrorMessage();
%>
					<tr><td align=right width=50%>Error Code:</td><td align=left><%=code%></td></tr>
					<tr><td align=right width=50%>Error Message:</td><td align=left><%=message%></td></tr>
<%
				} 
				else{
					List<ClassIDStatusResponseBean> classIdStatusList = msisdnResponseObj.getClassIdList();
					if (classIdStatusList != null){
						for (ClassIDStatusResponseBean classIdStatusObj : classIdStatusList) {
							String classIdReturned = classIdStatusObj.getClassId();
%>								
							<tr><td align=right width=50%>Class ID:</td><td align=left><%=classIdReturned%></td></tr>
<%								
							error = classIdStatusObj.getError();
							if (error != null) {
								String code = error.getErrorCode();
								String message = error.getErrorMessage();
%>			
								<tr><td align=right width=50%>Error Code:</td><td align=left><%=code%></td></tr>
								<tr><td align=right width=50%>Error Message:</td><td align=left><%=message%></td></tr>
<%
							} 
							else{
								String status = classIdStatusObj.getStatus();
%>									
								<tr><td align=right width=50%>Status:</td><td align=left><%=status%></td></tr>
<%									
							}
						}
					}
				}
			}
		}
	}
%>

</table>

<% 

}else{ 

 %>

<p>Status is not available.</p>

<% }	%>

</div>
</body>

</html>