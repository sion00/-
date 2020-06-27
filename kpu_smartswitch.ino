
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include "FS.h"
#include <TimeLib.h>

#ifndef APSSID
#define APSSID "AndroidHotspot4925" //server wi-fi name
#define APPSK  "950429mm"// wi-fi password
#endif

const char *ssid = APSSID;
const char *password = APPSK;

unsigned long Current_time, Off_time, Timer_time = 0;
int Light_state, Sensor_state, Security_state = 0;
int Timer = 0;
int Timer_t = 5000;
int Light = D5;
int Sensor = D6;
int Security = D1;
String ip;

int val1, val2 = 0; // digital signal of Sensor, Security

ESP8266WebServer server(80);
WiFiClient client;

void mainpage(){ //server status

  int y=year();
  int m=month();
  int d=day();
  int h=hour();
  int mi=minute();
  int s=second();
  
  String message = "";
  
  message += "<html>";  
  
  message += "<body>";
  message += "Server IP : ";
  message += ip;
  message += "<br /><br />";  
  message += "Time : ";
  message += y;
  message += ".";
  if(m < 10){message += "0";}
  message += m;
  message += ".";
  if(d < 10){message += "0";}
  message += d;
  message += " ";
  if(h < 10){message += "0";}
  message += h;
  message += ":";
  if(mi < 10){message += "0";}
  message += mi;
  message += ":";
  if(s < 10){message += "0";}
  message += s;
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

void controlpage(){ //control form - led, timer, sensor, security
  
  String message = "";
  message += "<html>";
  message += "<body>";
  
  message += "<FORM method=\"get\" action=\"/control.cgi\">";
  message += "<P><INPUT type=\"radio\" name=\"LightStatus\" value=\"1\">Light ON";
  message += "<P><INPUT type=\"radio\" name=\"LightStatus\" value=\"0\">Light OFF";

  message += "<P><INPUT type=\"radio\" name=\"TimerStatus\" value=\"1\">Timer ON";
  message += "<P><INPUT type=\"radio\" name=\"TimerStatus\" value=\"0\">Timer OFF";
  
  message += "<P><INPUT type=\"radio\" name=\"SensorStatus\" value=\"1\">Sensor ON";
  message += "<P><INPUT type=\"radio\" name=\"SensorStatus\" value=\"0\">Sensor OFF";
  
  message += "<P><INPUT type=\"radio\" name=\"SecurityStatus\" value=\"1\">Security ON";
  message += "<P><INPUT type=\"radio\" name=\"SecurityStatus\" value=\"0\">Security OFF";
  message += "<P><INPUT type=\"submit\" value=\"Submit\"> </FORM>";

  message += "</body>";
  message += "</html>";

  server.send(200, "text/html", message);
}

void controlcgi(){ //control cgi - led, timer, sensor, security
  
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

  if(server.argName(0) == "SecurityStatus"){
    int state = server.arg(0).toInt();

    Security_state = state;
    
    String message = "";
    message += "<html>";
    message += "<body>";
    message += "Currently Sensor is ";
    message += (Security_state ? "1" : "0");
    message += "</body>";
    message += "</html>";

   server.send(200, "text/html", message);
  }

   if(server.argName(0) == "TimerStatus"){
    int state = server.arg(0).toInt();

    Timer = state;
    Timer_time = millis() + Timer_t;

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

/*void timepage(){ //time setting form (for test through browser)
  String message = "";
  message += "<html>";
  message += "<body>";
  
  message += "<FORM method=\"get\" action=\"/time.cgi\">";

  message += "<P>Year<INPUT type=\"text\" name=\"Year\">";
  message += "<P>Month<INPUT type=\"text\" name=\"Month\">";
  message += "<P>Day<INPUT type=\"text\" name=\"Day\">";
  message += "<P>Hour<INPUT type=\"text\" name=\"Hour\">";
  message += "<P>Minute<INPUT type=\"text\" name=\"Minute\">";
  message += "<P>Second<INPUT type=\"text\" name=\"Second\">";
  
  message += "<P><INPUT type=\"submit\" value=\"Submit\"> </FORM>";

  message += "</body>";
  message += "</html>";

  server.send(200, "text/html", message);
}*/

void timecgi(){//time setting cgi
  
    int year = server.arg("Year").toInt();
    int month = server.arg("Month").toInt();
    int day = server.arg("Day").toInt();
    int hour = server.arg("Hour").toInt();
    int minute = server.arg("Minute").toInt();
    int second = server.arg("Second").toInt();

    setTime(hour, minute, second, day, month, year);

    mainpage();
}

void timelog(){ //user's sleeping timelog data showing
  
   String message = "";
   
   File file = SPIFFS.open("/log.txt", "r");
   message += file.readString();
   file.close();

  server.send(200, "text/plain", message);
}

void setting(){

  String message = "";
  message += "<FORM method=\"get\" action=\"/setting.cgi\">";

  message += "<P>Timer<INPUT type=\"text\" name=\"timer\">sec";

  message += "<P><INPUT type=\"submit\" value=\"Submit\"> </FORM>";

  message += "</body>";
  message += "</html>";

  server.send(200, "text/html", message);
}

void settingcgi(){
  if(server.argName(0) == "timer"){
    int state = server.arg(0).toInt();
    Timer_t = state * 1000;
  }

  mainpage();
}


String timenow(){ //print string(time of now)

  int y=year();
  int m=month();
  int d=day();
  int h=hour();
  
  String message = "";

  message += y;
  if(m < 10){message += "0";}
  message += m;
  if(d < 10){message += "0";}
  message += d;
  if(h < 10){message += "0";}
  message += h;

  return message;
}

void setup() {
  delay(1000);
  Serial.begin(115200);

  pinMode(Light, OUTPUT);
  pinMode(Sensor, INPUT);
  pinMode(Security, INPUT);
  digitalWrite(Light, Light_state);
  
  Serial.println();
  Serial.print("Configuring access point...  ");
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
  ip = WiFi.localIP().toString();

  setTime(0, 0, 0, 1, 1, 2020);

  SPIFFS.begin();
  File file = SPIFFS.open("/log.txt", "w");
  file.close();
      
  server.on("/", mainpage);
  server.on("/control", controlpage);
  server.on("/control.cgi", controlcgi);
  //server.on("/time", timepage);
  server.on("/time.cgi", timecgi);
  server.on("/timelog", timelog);
  server.on("/setting", setting);
  server.on("/setting.cgi", settingcgi);
  server.begin();
  Serial.println("HTTP server started");
}

void loop(){

  int y=year();
  int m=month();
  int d=day();
  int h=hour();
  int mi=minute();
  int s=second();

  Current_time = millis();
  val1 = digitalRead(Sensor);
  val2 = digitalRead(Security);
  
  server.handleClient();
  
  if(Sensor_state==1){ //센서기능
    if(val1 == HIGH){
      
      Off_time = Current_time + 5000; // 소등 조건 불만족

      if(Light_state==0){ //소등 후 수면시간 중 뒤척임감지
        String nowt = timenow();
        File file = SPIFFS.open("/log.txt", "a");
        file.println(nowt);
        delay(5000);
        file.close();
      }
    }
    else{
      if(Current_time == Off_time){ //소등 조건 만족, 로그파일 초기화 후 소등

        File file = SPIFFS.open("/log.txt", "w");
        file.close();
        
        Light_state = 0;
        digitalWrite(Light, Light_state);
      }
    }
  }

  if(Security_state==1){ //보안기능
     if(val2 == HIGH){
      digitalWrite(Light, HIGH);
      delay(1000);
      digitalWrite(Light, LOW);
      delay(1000);
      digitalWrite(Light, HIGH);
      delay(1000);
      digitalWrite(Light, LOW);
      delay(1000);
      digitalWrite(Light, HIGH);
      delay(1000);
      digitalWrite(Light, LOW);
      delay(1000);

      if (!client.connect("wirepusher.com", 80)){ //Needs to download WirePusher(APP) - id
        Serial.println("connection failed");
        return;
        }
        client.print(String("GET ") + "/send?id=E49JmpkX3&title=Alert!&message=Invader Detected&type=Default" + " HTTP/1.1\r\n" +
               "Host: " + "wirepusher.com" + "\r\n" + 
               "Connection: close\r\n\r\n");
        client.stop();
      digitalWrite(Light, Light_state);
      Security_state = 0;
     }
  }

  if(Timer==1){ //타이머기능
    if(Light_state==1){
      if(Current_time==Timer_time){
        Light_state = 0;    
        digitalWrite(Light, Light_state);
        Timer = 0;
      }
    }
  }
}
