import groovyx.net.http.RESTClient
import groovy.json.*
import static groovyx.net.http.ContentType.*

RESTClient attPaas = new RESTClient("http://paas1.attplatform.com/");

// Credentials for AT&T platform
String username = "1347644721";
String password = "e21fe58b26048f43bb3b7ebdbf4cc918";

try {
  //Login to AT&T platform request
  String xml = "<platform><login><userName>"+username+"</userName><password>"+password+"</password></login></platform>"

  def resp = attPaas.post(path: "/networking/rest/login",
      requestContentType: XML,
      contentType: JSON,
      body: xml);
  
  // Identifier of current session
  def sessionId = resp.getData().platform.login.sessionId
  
  // Parameters of broken Asset
  def serial = parameters.serial
  
  // Find active alert for Asset
  def filter = "device_id contains '" + serial + "' AND status != 'DONE'";
      
  resp = attPaas.get(path: "/networking/rest/record/Alerts",
    requestContentType: JSON,
    contentType: JSON,
    params : [fieldList : "id,device_id,status", filter : filter],
    headers: [Cookie : "JSESSIONID=" + sessionId]);
  
  def data = resp.getData()
  
  def update = { recordId ->
      updateReq = "<platform><record><status>DONE</status></record></platform>"

      attPaas.put(path: "/networking/rest/record/Alerts/" + recordId,
        requestContentType: XML,
        contentType: JSON,
        body: updateReq,
        headers: [Cookie : "JSESSIONID=" + sessionId]);
  }
  
  if (data.platform.record != null) {
    if (data.platform.record[0] == null) {
        update(data.platform.record.id)
    } else {
      data.platform.record.id.each{
        update(it.value)
      }
    }
  }
  
  //Logout request
  resp = attPaas.get(path: "/networking/rest/logout",
      requestContentType: URLENC,
      contentType: XML,
      headers: [Cookie : "JSESSIONID=" + sessionId]);
  
} catch(Exception e){
  logger.error(e.getMessage());
}
