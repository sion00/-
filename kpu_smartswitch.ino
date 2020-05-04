
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>

#ifndef APSSID
#define APSSID "KPUS/W"
#define APPSK  "1234567890"
#endif

const char *ssid = APSSID;
const char *password = APPSK;

unsigned long Current_time, Off_time;
int Light_state, Sensor_state, Security_state = 0;
int LED = D5;

ESP8266WebServer server(80);

/* Just a little test message.  Go to http://192.168.4.1 in a web browser
   connected to this access point to see it.
*/
void mainpage(){
  String message = "";
  message += "<html>";
  message += "<body>";

  message += "Currently Light is ";
  message += (Light_state ? "1" : "0");
  message += "Currently Sensor is ";
  message += (Sensor_state ? "1" : "0");
  message += "Currently Security is ";
  message += (Security_state ? "1" : "0");

  message += "</body>";
  message += "</html>";

  server.send(200, "text/html", message);
}

void controlpage(){
  
  String message = "";
  message += "<html>";
  message += "<body>";
  
  message += "<FORM method=\"get\" action=\"/control\">";
  message += "<P><INPUT type=\"radio\" name=\"LightStatus\" value=\"1\">Light ON";
  message += "<P><INPUT type=\"radio\" name=\"LightStatus\" value=\"0\">Light OFF";
 
  
  message += "<P><INPUT type=\"radio\" name=\"SensorStatus\" value=\"1\">Sensor ON";
  message += "<P><INPUT type=\"radio\" name=\"SensorStatus\" value=\"0\">Sensor OFF";
  
  message += "<P><INPUT type=\"radio\" name=\"SecurityStatus\" value=\"1\">Security ON";
  message += "<P><INPUT type=\"radio\" name=\"SecurityStatus\" value=\"0\">Security OFF";
  message += "<P><INPUT type=\"submit\" value=\"Submit\"> </FORM>";

  message += "</body>";
  message += "</html>";

  server.send(200, "text/html", message);
}

void controlcgi(){
  if(server.argName(0) == "LightStatus"){
    int Light_state = server.arg(0).toInt();
    digitalWrite(LED, Light_state);

    mainpage();
  }

  if(server.argName(0) == "SensorStatus"){
    int Sensor_state = server.arg(0).toInt();

    mainpage();
  }
}

void setup() {
  delay(1000);
  Serial.begin(115200);

  pinMode(LED, OUTPUT);
  digitalWrite(LED, 0);
  
  Serial.println();
  Serial.print("Configuring access point...");
  /* You can remove the password parameter if you want the AP to be open. */
  WiFi.softAP(ssid, password);

  IPAddress myIP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(myIP);
  server.on("/", mainpage);
  server.on("/control", controlpage);
  server.on("/control.cgi", controlcgi);
  server.begin();
  Serial.println("HTTP server started");
}

void loop() {
  server.handleClient();
}
