<%@ page import="com.att.enablers.locaid.*" %> 
<%@ page import="com.att.enablers.locaid.response.*" %> 

<html> 
                                                         
<head>
</head>

<body>
<div width=60% align=center>
<h2>Location Information Services powered by LocAid</h2>
<h2>Geo-coordinates</h2>

<%
java.util.HashMap params = new java.util.HashMap();
if (controllerResponse != null) {
	// just requested via controller
	params = (java.util.HashMap)controllerResponse.getData();
}

LocationResponseBean locationResponse = (LocationResponseBean)params.get("locaid_response");
if (locationResponse != null){
%>

<form >
<table width=100% >

<%
	BaseErrorResponseBean error = locationResponse.getError();
	if (error != null) {
		String code = error.getErrorCode();
		String message = error.getErrorMessage();
%>
		<tr><td align=right width=50%>Error Code:</td><td align=left><%=code%></td></tr>
		<tr><td align=right width=50%>Error Message:</td><td align=left><%=message%></td></tr>
<%
	} 
	else{
			String direction = locationResponse.getDirection();
			String speed = locationResponse.getSpeed();
%>
		<tr><td align=right width=50%>MSISDN:</td><td align=left><%=locationResponse.getNumber()%></td></tr>
		<tr><td align=right width=50%>Status:</td><td align=left><%=locationResponse.getStatus()%></td></tr>
		<tr><td align=right width=50%>Direction:</td><td align=left><%=(direction != null ? direction : "")%></td></tr>
		<tr><td align=right width=50%>Speed:</td><td align=left><%=(speed != null ? speed : "")%></td></tr>
		<tr><td align=right width=50%>Technology:</td><td align=left><%=locationResponse.getTechnology()%></td></tr>
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
%>

</table>
</form>

<% 

}else{ 

 %>

<p>Location information is not available.</p>

<% }	%>

</div>
</body>

</html>