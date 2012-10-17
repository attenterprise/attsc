<%@ page import="com.att.enablers.locaid.*" %> 
<%@ page import="com.att.enablers.locaid.response.*" %> 

<html> 
                                                         
<head>
</head>

<body>
<div width=60% align=center>
<h2>Location Information Services powered by LocAid</h2>
<h2>Zip-code Result</h2>
<form >

<%
java.util.HashMap params = new java.util.HashMap();
ZipCodeListResponseBean responseObj = null;
String idlist = null;
String origTransactionId = null;

if (controllerResponse != null) {
	// just requested via controller
	params = (java.util.HashMap)controllerResponse.getData();
	
	idlist = (String)params.get("idlist");
	origTransactionId = (String)params.get("origTransactionId");
	responseObj = (ZipCodeListResponseBean)params.get("locaid_response");
}

if (responseObj != null){
	if (origTransactionId != null){
%>
		<input type=hidden name='action' value='zipCodeAnswer' />
		<input type=hidden name='idlist' value='<%=idlist%>' />
		<input type=hidden name='origTransactionId' value='<%=origTransactionId%>' />
		<input type=submit name='submit' value='Request Update' />
<%
	}
%>

<table width=100% >
<%
	Long transactionId = responseObj.getTransactionId();
	if (transactionId != null){
%>
<tr><td align=right width=50%>Transaction ID:</td><td align=left><%=String.valueOf(transactionId)%></td></tr>
<%	
	}
	String status = responseObj.getStatus();
	if (status != null){
%>

		<tr><td align=right width=50%>Status:</td><td align=left><%=status%></td></tr>

<%
	}
	List<ZipCodeResponseBean> zipCodeResponseList = responseObj.getZipCodeList();
	List<MsisdnErrorResponseBean> msisdnErrorList = responseObj.getMsisdnErrorList();
	BaseErrorResponseBean error = responseObj.getError();
	if (error != null) {
		String code = error.getErrorCode();
		String message = error.getErrorMessage();
%>
		<tr><td align=right width=50%>Error Code:</td><td align=left><%=code%></td></tr>
		<tr><td align=right width=50%>Error Message:</td><td align=left><%=message%></td></tr>
<%
	} 
	if (msisdnErrorList != null) {
		for(MsisdnErrorResponseBean msisdnError: msisdnErrorList){
			String code = msisdnError.getErrorCode();
			String message = msisdnError.getErrorMessage();
%>
			<tr><td colspan=2>&nbsp;</td></tr>
			<tr><td align=right width=50%>MSISDN:</td><td align=left><%=msisdnError.getMsisdn()%></td></tr>
			<tr><td align=right width=50%>Error Code:</td><td align=left><%=code%></td></tr>
			<tr><td align=right width=50%>Error Message:</td><td align=left><%=message%></td></tr>
<%
		}
	}
	if (zipCodeResponseList != null){
		for (ZipCodeResponseBean zipCodeResponse: zipCodeResponseList){
			String msisdn = zipCodeResponse.getMsisdn();
			status = zipCodeResponse.getStatus();
%>
			<tr><td colspan=2>&nbsp;</td></tr>
			<tr><td align=right width=50%>MSISDN:</td><td align=left><%=msisdn%></td></tr>
			<tr><td align=right width=50%>Status:</td><td align=left><%=status%></td></tr>
<%
			error = zipCodeResponse.getError();
			if (error != null){
				String code = error.getErrorCode();
				String message = error.getErrorMessage();
%>
				<tr><td align=right width=50%>Error Code:</td><td align=left><%=code%></td></tr>
				<tr><td align=right width=50%>Error Message:</td><td align=left><%=message%></td></tr>
<%
			}
			else{
				String zipcode = zipCodeResponse.getZipCode();
%>
				<tr><td align=right width=50%>Zip-code:</td><td align=left><%=zipcode%></td></tr>
<%
			}
		}
	}
%>

</table>

<% 

}else{ 

 %>

<p>Zip-code and city information is not available.</p>

<% }	%>

</form>
</div>
</body>

</html>