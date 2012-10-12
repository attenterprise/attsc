import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*

RESTClient attPaas = new RESTClient("http://paas1.attplatform.com/");

// Credentials for AT&T platform
String username = "1347644721";
String password = "e21fe58b26048f43bb3b7ebdbf4cc918";

//Login to AT&T platform request
String xml = "<platform><login><userName>"+username+"</userName><password>"+password+"</password></login></platform>"

def resp = attPaas.post(path: "/networking/rest/login",
            requestContentType: XML,
            contentType: XML,
            body: xml);

def sessionId = "";

for (items in resp.getData().children()) {
  for (item in items.children()) {    
    if (item.name() == 'sessionId') {
      sessionId = item.text();
    }
  }
}


// Parameters of broken Asset
def serial = parameters.serial
def lat = parameters.lat
def lng = parameters.lng

//
def record = "<record><device_id>"+serial+"</device_id><latitude>"+lat+"</latitude><longitude>"+lng+"</longitude><status>NEW</status></record>";
record = "<platform>" + record + "</platform>";

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

