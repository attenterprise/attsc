<%@ page import="com.att.enablers.locaid.*" %> 
<%@ page import="com.att.enablers.locaid.response.*" %> 

<html> 
                                                         
<head>
</head>

<body>
<div width=60% align=center>
<h2>Agent's Phone - Location Information</h2>
<form >

<%
java.util.HashMap params = new java.util.HashMap();
LocationAnswerResponseBean locationAnswerResponse = null;
String idlist = null;
String origTransactionId = null;

if (controllerResponse != null) {
	// just requested via controller
	params = (java.util.HashMap)controllerResponse.getData();
	
	idlist = (String)params.get("idlist");
	origTransactionId = (String)params.get("origTransactionId");
	locationAnswerResponse = (LocationAnswerResponseBean)params.get("locaid_response");
}

if (locationAnswerResponse != null){
	if (origTransactionId != null){
%>
		<input type=hidden name='action' value='latlongAnswer' />
		<input type=hidden name='idlist' value='<%=idlist%>' />
		<input type=hidden name='origTransactionId' value='<%=origTransactionId%>' />
		<input type=submit name='submit' value='Request Update' />
<%
	}
%>

<table width=100% >
<%
	Long transactionId = locationAnswerResponse.getTransactionId();
	if (transactionId != null){
%>
<tr><td align=right width=50%>Transaction ID:</td><td align=left><%=String.valueOf(transactionId)%></td></tr>
<%	
	}
	String status = locationAnswerResponse.getStatus();
	if (status != null){
%>

		<tr><td align=right width=50%>Status:</td><td align=left><%=status%></td></tr>

<%
	}
	List<LocationResponseBean> locationResponseList = locationAnswerResponse.getLocationResponse();
	List<MsisdnErrorResponseBean> msisdnErrorList = locationAnswerResponse.getMsisdnError();
	BaseErrorResponseBean error = locationAnswerResponse.getError();
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
	if (locationResponseList != null){
		for (LocationResponseBean locationResponse: locationResponseList) {
			String direction = locationResponse.getDirection();
			String speed = locationResponse.getSpeed();
			String technology = locationResponse.getTechnology();
%>
			<tr><td colspan=2>&nbsp;</td></tr>
			<tr><td align=right width=50%>MSISDN:</td><td align=left><%=locationResponse.getNumber()%></td></tr>
			<tr><td align=right width=50%>Status:</td><td align=left><%=locationResponse.getStatus()%></td></tr>
			<tr><td align=right width=50%>Direction:</td><td align=left><%=(direction != null ? direction : "")%></td></tr>
			<tr><td align=right width=50%>Speed:</td><td align=left><%=(speed != null ? speed : "")%></td></tr>
			<tr><td align=right width=50%>Technology:</td><td align=left><%=(technology != null ? technology : "")%></td></tr>
<%
			CoordinateGeo coor = locationResponse.getCoordinateGeo();
			if (coor != null){
%>		
				<tr><td align=right width=50%>Coordinates format:</td><td align=left><%=coor.getFormat()%></td></tr>
				<tr><td align=right width=50%>Coordinates Y:</td><td align=left><%=coor.getY()%></td></tr>
				<tr><td align=right width=50%>Coordinates X:</td><td align=left><%=coor.getX()%></td></tr>
<%			
			}
			
			Geometry geometry = locationResponse.getGeometry();
			if (geometry != null){
%>
				<tr><td align=right width=50%>Geometry in-radius:</td><td align=left><%=String.valueOf(geometry.getInRadius())%></td></tr>
				<tr><td align=right width=50%>Geometry out-radius:</td><td align=left><%=String.valueOf(geometry.getOutRadius())%></td></tr>
				<tr><td align=right width=50%>Geometry radius:</td><td align=left><%=String.valueOf(geometry.getRadius())%></td></tr>
				<tr><td align=right width=50%>Geometry start angle:</td><td align=left><%=String.valueOf(geometry.getStartAngle())%></td></tr>
				<tr><td align=right width=50%>Geometry stop angle:</td><td align=left><%=String.valueOf(geometry.getStopAngle())%></td></tr>
				<tr><td align=right width=50%>Geometry type:</td><td align=left><%=geometry.getType()%></td></tr>
<%
			}
			
			LocationTime locationTime = locationResponse.getLocationTime();
			if (locationTime != null){
%>
				<tr><td align=right width=50%>Location Time:</td><td align=left><%=locationTime.getTime()%></td></tr>
				<tr><td align=right width=50%>Location Time (UTC):</td><td align=left><%=locationTime.getUtc()%></td></tr>
<%
			}
		}
	}
%>

</table>

<% 

}else{ 

 %>

<p>Location information is not available.</p>

<% }	%>

</form>
</div>
</body>

</html>