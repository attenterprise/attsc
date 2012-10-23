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

  def sessionId = resp.getData().platform.login.sessionId

  // Parameters of broken Asset
  def serial = parameters.serial
      
  String loc = parameters.location;
  def lat = loc.split(",")[0]
  def lng =loc.split(",")[1]

  def record = "<platform><record><device_id>"+serial+"</device_id><latitude>"+lat+"</latitude><longitude>"+lng+"</longitude><status>NEW</status></record></platform>";

  resp = attPaas.post(path: "/networking/rest/record/Alerts",
      requestContentType: XML,
      contentType: XML,
      body: record,
      headers: [Cookie : "JSESSIONID=" + sessionId]);

  //Logout request
  resp = attPaas.get(path: "/networking/rest/logout",
      requestContentType: URLENC,
      contentType: XML,
      headers: [Cookie : "JSESSIONID=" + sessionId]);
    
} catch(Exception e){
  logger.error(e.getMessage());
}
