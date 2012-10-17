<%@ page import="com.att.enablers.locaid.*" %> 
<%@ page import="com.att.enablers.locaid.response.*" %> 

<html>  
                                                         
<head>
</head>

<body>
<div width=60% align=center>
<h2>Location Information Services powered by LocAid</h2>
<h2>Address</h2>
<form >

<%
java.util.HashMap params = new java.util.HashMap();
AddressListResponseBean addressResponseObj = null;
String idlist = null;
String origTransactionId = null;

if (controllerResponse != null) {
	// just requested via controller
	params = (java.util.HashMap)controllerResponse.getData();
	
	idlist = (String)params.get("idlist");
	origTransactionId = (String)params.get("origTransactionId");
	addressResponseObj = (AddressListResponseBean)params.get("locaid_response");
}

if (addressResponseObj != null){
	if (origTransactionId != null){
%>
		<input type=hidden name='action' value='addressAnswer' />
		<input type=hidden name='idlist' value='<%=idlist%>' />
		<input type=hidden name='origTransactionId' value='<%=origTransactionId%>' />
		<input type=submit name='submit' value='Request Update' />
<%
	}
%>

<table width=100% >
<%
	Long transactionId = addressResponseObj.getTransactionId();
	if (transactionId != null){
%>
<tr><td align=right width=50%>Transaction ID:</td><td align=left><%=String.valueOf(transactionId)%></td></tr>
<%	
	}
	String status = addressResponseObj.getStatus();
	if (status != null){
%>

		<tr><td align=right width=50%>Status:</td><td align=left><%=status%></td></tr>

<%
	}
	List<AddressByPhoneResponseBean> addressResponseList = addressResponseObj.getAddressResponseList();
	List<MsisdnErrorResponseBean> msisdnErrorList = addressResponseObj.getMsisdnErrorList();
	BaseErrorResponseBean error = addressResponseObj.getError();
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
	if (addressResponseList != null){
		for (AddressByPhoneResponseBean addressResponse: addressResponseList){
			String msisdn = addressResponse.getMsisdn();
			status = addressResponse.getStatus();
%>
			<tr><td colspan=2>&nbsp;</td></tr>
			<tr><td align=right width=50%>MSISDN:</td><td align=left><%=msisdn%></td></tr>
			<tr><td align=right width=50%>Status:</td><td align=left><%=status%></td></tr>
<%
			error = addressResponse.getError();
			if (error != null){
				String code = error.getErrorCode();
				String message = error.getErrorMessage();
%>
				<tr><td align=right width=50%>Error Code:</td><td align=left><%=code%></td></tr>
				<tr><td align=right width=50%>Error Message:</td><td align=left><%=message%></td></tr>
<%
			}
			else{
				String profile = addressResponse.getProfile();
				String street_number = addressResponse.getStreetNumber();
				String street = addressResponse.getStreetName();
				String city = addressResponse.getCity();
				String county = addressResponse.getCounty();
				String state = addressResponse.getStateName();
				String zipcode = addressResponse.getZipCode();
				String country = addressResponse.getCountry();
%>
				<tr><td align=right width=50%>Profile:</td><td align=left><%=profile%></td></tr>
				<tr><td align=right width=50%>Street number:</td><td align=left><%=street_number%></td></tr>
				<tr><td align=right width=50%>Street:</td><td align=left><%=street%></td></tr>
				<tr><td align=right width=50%>City:</td><td align=left><%=city%></td></tr>
				<tr><td align=right width=50%>County:</td><td align=left><%=county%></td></tr>
				<tr><td align=right width=50%>State:</td><td align=left><%=state%></td></tr>
				<tr><td align=right width=50%>Zip-code:</td><td align=left><%=zipcode%></td></tr>
				<tr><td align=right width=50%>Country:</td><td align=left><%=country%></td></tr>
<%
			}
		}
	}
%>

</table>

<% 

}else{ 

 %>

<p>Address is not available.</p>

<% }	%>

</form>
</div>
</body>

</html>