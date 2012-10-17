<!DOCTYPE html>

<%

java.util.HashMap params = new java.util.HashMap();
java.util.List<Parameters> agentList = new java.util.ArrayList();
int selectedIndex = 0;
String selectedPhone = "";
String selectedLat = "0";
String selectedLong = "0";

if (controllerResponse != null) {
	params = (java.util.HashMap)controllerResponse.getData();
}

String request_id = (String)params.get("id");
String customer_name = (String)params.get("customer_name");
String customer_address = (String)params.get("customer_address");
String description = (String)params.get("description");
String agent = (String)params.get("agent");
String requestLat = (String)params.get("latitude");
String requestLong = (String)params.get("longitude");
String max_distance = (String)params.get("max_distance");
if (max_distance == null){
	max_distance = "15";
}
 
int nAgents = 0;
if (params != null){
	agentList = (java.util.List<Parameters>) params.get("searchResultObj");

	if (agentList == null){
		Functions.throwError("Unexpected error - agent list is NULL!");
	}
	
	if (agentList != null){
		for(Parameters agentParams: agentList){	
			String recordID = agentParams.get("record_id");
			
			nAgents ++;
			if (recordID.equals(agent)){
				selectedIndex = nAgents;
				selectedPhone = agentParams.get("phone");
				selectedLat = agentParams.get("latitude");
				selectedLong = agentParams.get("longitude");
			}		
		}
	}
}

%>

<html>
<head>
<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?sensor=false"></script>

<script type="text/javascript"> 
  var map;
  var markersArray = [];
  var requestLocation;
  var coorInfoWindow;
    
  function createInfoWindowContent() {
        var numTiles = 1 << map.getZoom();

        return ['Customer: <%=customer_name%>',
                'Location: <%=customer_address%>'
               ].join('<br>');
  }

  function initialize(selectedIndex, selectedLat, selectedLong, selectedPhone, requestLat, requestLong) {
  	try{
  		
	  	if (selectedIndex > 0){
	  		selectAgent(selectedIndex, selectedLat, selectedLong, selectedPhone);
	  	}
	  	
	    //alert("Lat="+requestLat+", Lng="+requestLong);
		requestLocation = new google.maps.LatLng(requestLat, requestLong);
	
	    var myOptions = {
	      zoom: 9,
	      mapTypeId: google.maps.MapTypeId.ROADMAP
	    }
	    map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
	
	    map.setCenter(requestLocation);
	    var marker = new google.maps.Marker({
	        map: map, 
	        position: requestLocation
	    });
	    
	    coorInfoWindow = new google.maps.InfoWindow();
	    coorInfoWindow.setContent(createInfoWindowContent());
	    coorInfoWindow.setPosition(requestLocation);
	    coorInfoWindow.open(map);  
	    /*
	    google.maps.event.addListener(map, 'zoom_changed', function() {
	          coorInfoWindow.setContent(createInfoWindowContent());
	          coorInfoWindow.open(map);
	    });
	    */
	    
	    displayAgents();
	
	}
	catch(error){
		alert(error);
	}        
  }
  
  function displayAgents(){
  	try{
  		deleteOverlays();
  		
	<% if (nAgents > 0){ 
		for (int i = 1; i <= nAgents; i ++){
			Parameters agentParams = (Parameters)agentList.get(i-1);
			String latitude = agentParams.get("latitude");
			String longitude = agentParams.get("longitude");
			double distance = Double.parseDouble(agentParams.get("distance"));
			
	%>	
			var max = document.getElementById("max_distance").value;
			if (max == ""){
				max = Math.pow(2,32) - 1; // all agents
			}
			if (<%=distance%> <= max){
				var image = "http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=<%=i%>|FFFF00|000000";	
			    var aMarker = new google.maps.Marker({
			       		position: new google.maps.LatLng(<%=latitude%>, <%=longitude%>),
				        map: map,
				        icon: image
				    });
			    markersArray.push(aMarker);
			    document.getElementById("agent_<%=i%>").style.display = '';
			}
			else{
			    document.getElementById("agent_<%=i%>").style.display = 'none';
			}
	<% 		
		}
	   } 
	%>
	}
	catch(error){
		alert(error);
	}        
  }

  function deleteOverlays(){   
    if (markersArray){     
       for (i in markersArray){       
          markersArray[i].setMap(null);     
       }     
       markersArray.length = 0;   
    } 
  }
    
  function selectAgent(i, latitude, longitude, phone){
  	document.getElementById("agent_phone").value=phone;
  }
  
</script>

</head>

<body onload="initialize(<%=selectedIndex%>,<%=selectedLat%>,<%=selectedLong%>, '<%=selectedPhone%>', <%=requestLat%>, <%=requestLong%>);" >

<form name="mainForm" method="POST" class="form1" action="/networking/controller/com/platform/CServiceDispatcher/requests/ServiceRequestsController">
<input type="hidden" name="action" value="assignAgent">     
<input type="hidden" name="request_id" value="<%=request_id%>"> 
<input type="hidden" id="agent_phone" name="agent_phone" value="<%=selectedPhone%>">

<input type="hidden" name="customer_name" value="<%=customer_name%>">     
<input type="hidden" name="customer_address" value="<%=customer_address%>">     
<input type="hidden" name="customer_description" value="<%=description%>">     

<center>

<h2>Service Request - Agent Assignment</h2>
<br>
<table width=100%>
<tr valign=top>
<td>
	<table width=100%>
	<tr><td align=right><b>Customer Name:</b></td><td align=left><%=customer_name%></td></tr>
	<tr><td align=right><b>Description:</b></td><td align=left><%=description%></td></tr>
	<tr><td align=right><b>Address:</b></td align=left><td><%=customer_address%></td></tr>
	<tr><td align=right><b>Service Request ID:</b></td align=left><td><%=request_id%></td></tr>
	<tr><td colspan=2 align=center>	<br>
	Max distance to agent (mi): <input type="text" id="max_distance" name="max_distance" value="<%=max_distance%>" size=5 maxlength=3>&nbsp;
	<input name="refresh_distance" type=button value="Change Distance" onClick="displayAgents();" /> 
	</td>
	</tr>
	</table>
	<br>
	<table width=100% border=1>
	<tr>
	<td align=center><b>Select</b></td>
	<td align=center><b>Agent ID</b></td>
	<td align=center><b>First Name</b></td>
	<td align=center><b>Last Name</b></td>
	<td align=center><b>Agent Phone</b></td>
	<td align=center><b>Distance to Agent (mi)</b></td>
	</tr>	
	
<% for (int i = 1; i <= nAgents; i ++){
	Parameters agentParams = (Parameters)agentList.get(i-1);

	String recordID = agentParams.get("record_id");
	String firstName = agentParams.get("first_name");
	String lastName = agentParams.get("last_name");
	String phone = agentParams.get("phone");
	String latitude = agentParams.get("latitude");
	String longitude = agentParams.get("longitude");
	String distance = agentParams.get("distance");
%>

	<tr id='agent_<%=i%>'>
	<td align=center>
		<b><%=i%></b>&nbsp;
		<input type=radio name="agent_selection" value="<%=recordID%>" <%= ((i == selectedIndex) ? "CHECKED" : "")%>
	  		onClick="selectAgent(<%=i%>, <%=latitude%>, <%=longitude%>, '<%=phone%>');" /></td>
	<td><%=recordID%></td>
	<td><%=firstName%></td>
	<td><%=lastName%></td>
	<td><%=phone%></td>
	<td><%=distance%></td>
	</tr>	

<%
} 
%>

	</table>
	<br>
	<center><input name="agent_submit" type=submit value="Assign Request to Selected Agent" /></center>
</td>
<td>
	<div id="map_canvas" style="width: 600px; height: 350px; position: relative;"></div>
</td>
</tr>
</table>

</center>

</form>

</body>
</html>