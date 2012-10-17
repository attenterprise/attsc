<%@ page import="com.att.enablers.locaid.*" %> 
<%@ page import="com.att.enablers.locaid.response.*" %> 

<html> 
                                                         
<head>
</head>

<body>
<div width=60% align=center>
<h2>Location Information Services powered by LocAid</h2>
<h2>Phone Subscription</h2>

<%
java.util.HashMap params = new java.util.HashMap();
if (controllerResponse != null) {
	params = (java.util.HashMap)controllerResponse.getData();
}

SubscribePhoneResponseBean responseObj = (SubscribePhoneResponseBean)params.get("locaid_response");
if (responseObj != null){
	Long transaction_id = responseObj.getTransactionId();
%>

<table width=100% >
<tr><td align=right width=50%>Transaction ID:</td><td align=left><%=transaction_id%></td></tr>


<%
	BaseErrorResponseBean error = responseObj.getError();
	if (error != null) {
		String code = error.getErrorCode();
		String message = error.getErrorMessage();
%>
		<tr><td align=right width=50%>Error Code:</td><td align=left><%=code%></td></tr>
		<tr><td align=right width=50%>Error Message:</td><td align=left><%=message%></td></tr>
<%
	} 
	else{
		List<ComplexClassIDResponseBean> classIdListResponse = responseObj.getClassIdList();
		if (classIdListResponse != null){
			for (ComplexClassIDResponseBean classIdResponse : classIdListResponse) {
				String classIdReturned = classIdResponse.getClassId();
%>			
				<tr><td colspan=2>&nbsp;</td></tr>
				<tr><td align=right width=50%>Class ID:</td><td align=left><%=classIdReturned%></td></tr>
<%				
				error = classIdResponse.getError();
				if (error != null) {
					String code = error.getErrorCode();
					String message = error.getErrorMessage();
%>
						<tr><td align=right width=50%>Error Code:</td><td align=left><%=code%></td></tr>
						<tr><td align=right width=50%>Error Message:</td><td align=left><%=message%></td></tr>
<%
				} 
				else{	
					List<MsisdnStatusResponseBean> msisdnStatusList = classIdResponse.getMsisdnList();
					if (msisdnStatusList != null){
						for (MsisdnStatusResponseBean msisdnStatus : msisdnStatusList) {
							String msisdnReturned = msisdnStatus.getMsisdn();
%>							
							<tr><td align=right width=50%>MSISDN:</td><td align=left><%=msisdnReturned%></td></tr>
<%
							error = msisdnStatus.getError();
							if (error != null) {
								String code = error.getErrorCode();
								String message = error.getErrorMessage();
%>
								<tr><td align=right width=50%>Error Code:</td><td align=left><%=code%></td></tr>
								<tr><td align=right width=50%>Error Message:</td><td align=left><%=message%></td></tr>
<%
							}else{
								String status = msisdnStatus.getStatus();
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

<p>Subscription information is not available.</p>

<% }	%>

</div>
</body>

</html>