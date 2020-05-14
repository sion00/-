
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>

#ifndef APSSID
#define APSSID "KPUS/W"
#define APPSK  "1234567890"
#endif

const char *ssid = APSSID;
const char *password = APPSK;

unsigned long Current_time, Off_time, Timer_time = 0;
int Light_state, Sensor_state, Security_state = 0;
int Timer = 0;
int Light = D5;
int Sensor = D6;
int val = 0;

ESP8266WebServer server(80);

void mainpage(){
  String message = "";
  message += "<html>";
  message += "<body>";

  message += "Server IP : ";
  message += "192.168.4.1";
  message += "<br /><br />";
  message += "Currently Light is ";
  message += (Light_state ? "1" : "0");
  message += "<br /><br />";
  message += "Currently Timer is ";
  message += (Timer ? "1" : "0");
  message += "<br /><br />";
  message += "Currently Sensor is ";
  message += (Sensor_state ? "1" : "0");
  message += "<br /><br />";
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
  
  message += "<FORM method=\"get\" action=\"/control.cgi\">";
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

void timerpage(){
  
  String message = "";
  message += "<html>";
  message += "<body>";

  message += "<FORM method=\"get\" action=\"/timer.cgi\">";
  message += "<P><INPUT type=\"radio\" name=\"TimerStatus\" value=\"1\">Timer ON";
  message += "<P><INPUT type=\"radio\" name=\"TimerStatus\" value=\"0\">Timer OFF";
  message += "<P><INPUT type=\"submit\" value=\"Submit\"> </FORM>";

  message += "</body>";
  message += "</html>";

  server.send(200, "text/html", message);
}

void controlcgi(){
  if(server.argName(0) == "LightStatus"){
    int state = server.arg(0).toInt();

    Light_state = state;    
    digitalWrite(Light, Light_state);

    String message = "";
    message += "<html>";
    message += "<body>";
    message += "Currently Light is ";
    message += (Light_state ? "1" : "0");
    message += "</body>";
    message += "</html>";

   server.send(200, "text/html", message);
  }

  if(server.argName(0) == "SensorStatus"){
    int state = server.arg(0).toInt();

    Sensor_state = state;
    Off_time = millis() + 5000;
    
    String message = "";
    message += "<html>";
    message += "<body>";
    message += "Currently Sensor is ";
    message += (Sensor_state ? "1" : "0");
    message += "</body>";
    message += "</html>";

   server.send(200, "text/html", message);
  }
}

void timercgi(){
  if(server.argName(0) == "TimerStatus"){
    int state = server.arg(0).toInt();

    Timer = state;
    Timer_time = millis() + 5000;

    String message = "";
    message += "<html>";
    message += "<body>";
    message += "Currently Timer is ";
    message += (Timer ? "1" : "0");
    message += "</body>";
    message += "</html>";

   server.send(200, "text/html", message);
}
}

void setup() {
  delay(1000);
  Serial.begin(115200);

  pinMode(Light, OUTPUT);
  pinMode(Sensor, INPUT);
  digitalWrite(Light, Light_state);
  
  Serial.println();
  Serial.print("Configuring access point...  ");
  WiFi.softAP(ssid, password);

  IPAddress myIP = WiFi.softAPIP();
  Serial.print("AP IP address:");
  Serial.println(myIP);
  server.on("/", mainpage);
  server.on("/control", controlpage);
  server.on("/control.cgi", controlcgi);
  server.on("/timer", timerpage);
  server.on("/timer.cgi", timercgi);
  server.begin();
  Serial.println("HTTP server started");
}

void loop(){

  Current_time = millis();
  val = digitalRead(Sensor); // 센서값 읽기
  
  server.handleClient();
  
  if(Sensor_state==1){
    if(val == HIGH){
      Off_time = Current_time + 5000;
    }
    else{
      if(Current_time == Off_time){
        Light_state = 0;    
        digitalWrite(Light, Light_state);
        Sensor_state = 0;
      }
    }
  }

  if(Timer==1){
    if(Light_state==1){
      if(Current_time==Timer_time){
        Light_state = 0;    
        digitalWrite(Light, Light_state);
        Timer = 0;
      }
    }
  }
  
/*  if(Sensor_state == 1){
    if(val == HIGH) {
       Light_state = 1;    
        digitalWrite(Light, Light_state);
    }
    else {
        Light_state = 0;    
        digitalWrite(Light, Light_state);
     }
  }*/
}
