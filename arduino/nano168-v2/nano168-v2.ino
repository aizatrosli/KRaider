#include <Servo.h>

Servo steering;

uint8_t  steerPin = 8;
uint8_t  motorPin = 7;
uint8_t  togglePin = 10;

int16_t  throttleVal;
int8_t  steerVal;
String modeVal;
String receivedArs[2];
String receivedString;
String receivedArr[2];

int8_t mapsteerVal;
int8_t mapthrottleVal;


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(steerPin, OUTPUT);
  pinMode(motorPin, OUTPUT);
  pinMode(togglePin, OUTPUT);
  steering.attach(steerPin);
  steering.write(23);

}

void loop() {
  // put your main code here, to run repeatedly:
      if (Serial.available() > 0)
      {
        receivedString = Serial.readStringUntil('\n');
        int com1index = receivedString.indexOf(',');
        int com2index = receivedString.indexOf(',', com1index + 1);
        receivedArr[0] = receivedString.substring(0,com1index);
        receivedArr[1] = receivedString.substring(com1index + 1, com2index);
        receivedArr[2] = receivedString.substring(com2index + 1);
        steerVal = receivedArr[2].toInt();
        throttleVal = receivedArr[1].toInt();
        modeVal = receivedArr[0];
      }
    mapsteerVal = map(steerVal,-100,100,0,46);
    mapthrottleVal = map(abs(throttleVal),0,100,0,255);
    steering.write(mapsteerVal);
    throttle(togglePin, motorPin, throttleVal, mapthrottleVal);
}
void throttle(uint8_t togglePin, uint8_t motorPin, int8_t realthrottleVal, uint8_t unrealthrottleVal)
{
  if (realthrottleVal < 0)
    {
      digitalWrite(togglePin, HIGH);
    }
  else
  {
      digitalWrite(togglePin, LOW);
  }
  analogWrite(motorPin, unrealthrottleVal);
}
