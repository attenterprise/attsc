import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*


/*
 * Object name: ATTAssetIsDamaged
 * 
 * Script is responsible for propagating information about device failure to Long Jump.
 * 
 * Parameters:
 * 
 * serial - Device's serial number,
 * location - Device's location.
 */

// URL For PaaS connection
RESTClient paasRestClient = new RESTClient("http://paas1.attplatform.com/");

// Credentials for AT&T platform
String username = "1347644721";
String password = "e21fe58b26048f43bb3b7ebdbf4cc918";

try {
  //Login to AT&T platform request
  String loginXmlRequest = "<platform><login><userName>"+username+"</userName><password>"+password+"</password></login></platform>"

  def loginResponse = paasRestClient.post(path: "/networking/rest/login",
      requestContentType: XML,
      contentType: JSON,
      body: loginXmlRequest);

  def sessionId = loginResponse.getData().platform.login.sessionId

  // Parameters of broken Asset
  def serial = parameters.serial
      
  String location = parameters.location;
  def lat = location.split(",")[0]
  def lng =location.split(",")[1]
  
  // Adding new record in AT&T PAAS
  def newRecord = "<platform><record><device_id>"+serial+"</device_id><latitude>"+lat+"</latitude><longitude>"+lng+"</longitude><status>NEW</status></record></platform>";

  paasRestClient.post(path: "/networking/rest/record/Alerts",
      requestContentType: XML,
      contentType: XML,
      body: newRecord,
      headers: [Cookie : "JSESSIONID=" + sessionId]);

  //Logout request
  paasRestClient.get(path: "/networking/rest/logout",
      requestContentType: URLENC,
      contentType: XML,
      headers: [Cookie : "JSESSIONID=" + sessionId]);
    
} catch(Exception e){
  logger.error(e.getMessage());
}
