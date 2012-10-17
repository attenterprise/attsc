<%@ page import="com.platform.api.Functions" %> 
<%
String tenantID = Functions.getEnv(ENV.USER.TENANT_ID);

String queryString = request.getQueryString();
if (queryString != null){
	queryString = "/sites/"+tenantID+"/SMSReceiverSite/controller/com/platform/cMobilityEnablersToolkit/gsms/GSMSReceiver?action=receiveSMS&successPage=smsOK.jsp&"+queryString;
}
else{
	queryString = "/sites/"+tenantID+"/SMSReceiverSite/controller/com/platform/cMobilityEnablersToolkit/gsms/GSMSReceiver";
}

%>

<jsp:forward page="<%= queryString %>" />