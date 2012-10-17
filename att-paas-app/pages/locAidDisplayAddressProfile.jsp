<%@ page import="com.att.enablers.locaid.*" %> 
<%@ page import="com.att.enablers.locaid.response.*" %> 

<html> 
                                                         
<head>
</head>

<body>
<div width=60% align=center>
<h2>Location Information Services powered by LocAid</h2>
<h2>Address Request</h2>
<form >

<%
java.util.HashMap params = new java.util.HashMap();
if (controllerResponse != null) {
	params = (java.util.HashMap)controllerResponse.getData();
}

AddressProfileResponseBean addressResponseObj = (AddressProfileResponseBean)params.get("locaid_response");
if (addressResponseObj != null){
	Long transaction_id = addressResponseObj.getTransactionId();
%>

<input type=hidden name='action' value='address' />
<input type=hidden name='idlist' value='<%=(String)params.get("idlist")%>' />
<input type=hidden name='id' value='<%=(String)params.get("id")%>' />

<table width=100% >
<tr><td align=right width=50%>Transaction ID:</td><td align=left><%=String.valueOf(transaction_id)%></td></tr>

<%
	BaseErrorResponseBean error = addressResponseObj.getError();
	if (error != null) {
		String code = error.getErrorCode();
		String message = error.getErrorMessage();
%>
		<tr><td align=right width=50%>Error Code:</td><td align=left><%=code%></td></tr>
		<tr><td align=right width=50%>Error Message:</td><td align=left><%=message%></td></tr>
<%
	} 
	else{
		String profileId = addressResponseObj.getAddressProfileId();

%>
		<tr><td align=right width=50%>Profile ID:</td><td align=left><%=profileId%>
		<input type=hidden name='profileId' value='<%=String.valueOf(profileId)%>' /></td></tr>
	  <tr><td align=right width=50%>Location Method:</td>
	       <td align=left>
	   	 <select name="locationMethod">
		   <option value="LEAST_EXPENSIVE" selected>Least expensive</option>
		   <option value="MOST_ACCURATE">Most accurate</option>
		   <option value="CELL">Cell</option>
		   <option value="A-GPS">Assisted GPS</option>
		 </select>
	       </td>
	   </tr>
	   <tr><td align=right width=50%>Synchronization Type:</td>
	       <td align=left>
	   	 <select name="syncType">
		   <option value="syn" selected>Synchronized</option>
		   <option value="asyn">Asynchronized</option>
		 </select>
	       </td>
	   </tr>
	   <tr><td align=right width=50%>Overage:</td>
	       <td align=left>
	   	 <select name="overage">
		   <option value="1" selected>Without limit</option>
		   <option value="0">With limit</option>
		 </select>
	       </td>
	   </tr>
	   <tr><td colspan=2>&nbsp;</td></tr>
	   <tr><td colspan=2>&nbsp;</td></tr>
	   <tr><td colspan=2 align=center><input type="submit" name="go" value="Request Address" /></td></tr>
<%
	}
%>

</table>

<% 

}else{ 

 %>

<p>Profile information is not available.</p>

<% }	%>

</div>
</body>

</html>