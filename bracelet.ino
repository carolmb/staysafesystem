#include "SoftwareSerial.h"
#include "Time.h"
#include <ArduinoJson.h>

SoftwareSerial bluetooth(2, 3); //TX, RX (Bluetooth)

int Senval1 = 1;
int SenpinButton1 = A1;

int Senval2 = 2;
int SenpinButton2 = A2;

int pulsePin = A6;

StaticJsonBuffer<200> jsonBuffer;

void setup()
{
  Serial.begin(9600);
  bluetooth.begin(9600);
}

int getPulse() {

  int count = 0;
  int last = 0;
  int current = 0;
  bool bigger = true;
  int sinal[100];
  for(int i = 0; i < 100; i++) {  
    delay(100);
    current = analogRead(pulsePin);
    sinal[i] = current;
  }
  for(int i = 1; i < 100; i++) {  
    last = sinal[i-1];
    current = sinal[i];
    if(current > last && bigger) {
    
    } else if (current < last && bigger){
      count++;
      bigger = false;
    } else if (current < last && !bigger) {
      
    } else if (current > last && !bigger) {
      bigger = true;
    }
  }
  if(count > 100) {
    count = -1;  
  }
  return count*6; 
}

bool sendDataBluetooth(int idButton) {
  Serial.println("sendDataBluetooth foi chamado");
  int pulse = getPulse();
  JsonObject& msg = jsonBuffer.createObject();
  msg["buttonId"] = idButton;
  msg["pulse"] = pulse;

  char buffer[256];
  msg.printTo(buffer, sizeof(buffer));
  bluetooth.println(buffer);
  
  Serial.println("sendDataBluetooth foi encerrado");
  delay(1000);
}

void loop()
{
  Senval1 = analogRead(SenpinButton1);
  Senval2 = analogRead(SenpinButton2);
  
  if(Senval1 > 500) {
    sendDataBluetooth(1);
  }
  
  if(Senval2 > 500) {
    sendDataBluetooth(2);
  }

  delay(200);
}