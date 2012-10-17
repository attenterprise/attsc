<%@ page import="com.platform.api.Functions" %> 
<%
String tenantID = Functions.getEnv(ENV.USER.TENANT_ID);

String queryString = request.getQueryString();
if (queryString != null){
	queryString = "/sites/"+tenantID+"/ServiceDispatcherSMSReceiverSite/controller/com/platform/CServiceDispatcher/agents/AgentsSMSReceiver?action=receiveSMS&successPage=sdSMSOK.jsp&"+queryString;
}
else{
	queryString = "/sites/"+tenantID+"/ServiceDispatcherSMSReceiverSite/controller/com/platform/CServiceDispatcher/agents/AgentsSMSReceiver";
}

%>

<jsp:forward page="<%= queryString %>" />