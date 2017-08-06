#include <Arduino_FreeRTOS.h>
#include <semphr.h>  // add the FreeRTOS functions for Semaphores (or Flags).
#include <Servo.h>

Servo steering;
int16_t  throttleVal;
int8_t  steerVal;
uint8_t modeVal;
uint8_t  voltVal;
uint8_t  distVal;

// For serial passthrough
//int8_t mode;
//int8_t throttle;
//int8_t steer;
//int8_t distance;
//int8_t volt;

// Declare a mutex Semaphore Handle which we will use to manage the Serial Port.
// It will be used to ensure only only one Task is accessing this resource at any time.
SemaphoreHandle_t xSerialSemaphore;

// define two Tasks for DigitalRead & AnalogRead
void TaskThrottleMotor( void *pvParameters );
void TaskVoltRead( void *pvParameters );
void TaskSteerServo( void *pvParameters );
void TaskUltraSonar( void *pvParameters );
void TaskSerialRW( void *pvParameters);

// the setup function runs once when you press reset or power the board

void throttle(uint8_t togglePin, uint8_t motorPin, int16_t throttleVal)
{
  analogWrite(motorPin, abs(throttleVal));
  if (throttleVal < 0)
    {
      digitalWrite(togglePin, LOW);
    }
  else
  {
      digitalWrite(togglePin, HIGH);
  }
}
void setup()
{

  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB, on LEONARDO, MICRO, YUN, and other 32u4 based boards.
  }

  // Semaphores are useful to stop a Task proceeding, where it should be paused to wait,
  // because it is sharing a resource, such as the Serial port.
  // Semaphores should only be used whilst the scheduler is running, but we can set it up here.
  if ( xSerialSemaphore == NULL )  // Check to confirm that the Serial Semaphore has not already been created.
  {
    xSerialSemaphore = xSemaphoreCreateMutex();  // Create a mutex semaphore we will use to manage the Serial Port
    if ( ( xSerialSemaphore ) != NULL )
      xSemaphoreGive( ( xSerialSemaphore ) );  // Make the Serial Port available for use, by "Giving" the Semaphore.
  }

  // Now set up two Tasks to run independently.
  xTaskCreate(
    TaskThrottleMotor
    ,  (const portCHAR *)"ThrottleMotor"  // A name just for humans
    ,  128  // This stack size can be checked & adjusted by reading the Stack Highwater
    ,  NULL
    ,  3  // Priority, with 3 (configMAX_PRIORITIES - 1) being the highest, and 0 being the lowest.
    ,  NULL );

  xTaskCreate(
    TaskVoltRead
    ,  (const portCHAR *) "VoltRead"
    ,  128  // Stack size
    ,  NULL
    ,  5  // Priority
    ,  NULL );

  xTaskCreate(
    TaskSteerServo
    ,  (const portCHAR *) "SteerServo"
    ,  128  // Stack size
    ,  NULL
    ,  2  // Priority
    ,  NULL );

  xTaskCreate(
    TaskUltraSonar
    ,  (const portCHAR *) "UltraSonar"
    ,  128  // Stack size
    ,  NULL
    ,  4  // Priority
    ,  NULL );

  xTaskCreate(
    TaskSerialRW
    ,  (const portCHAR *) "SerialRW"
    ,  128  // Stack size
    ,  NULL
    ,  1  // Priority
    ,  NULL );
  // Now the Task scheduler, which takes over control of scheduling individual Tasks, is automatically started.
}

void loop()
{
  // Empty. Things are done in Tasks.
  // try added serial function
}

/*--------------------------------------------------*/
/*---------------------- Tasks ---------------------*/
/*--------------------------------------------------*/
void TaskSteerServo( void *pvParameters __attribute__((unused)) )  // This is a Task.
{
  /*
    DigitalReadSerial
    Reads a digital input on pin 2, prints the result to the serial monitor

    This example code is in the public domain.
  */
  // micro servo SG90 limited to 0-45 deg
  uint8_t  steerPin = 8;
  int8_t mapsteerVal;
  pinMode(steerPin, OUTPUT);
  steering.attach(steerPin);
  steering.write(22.5);

  for (;;) // A Task shall never return or exit.
  {
    // read the input pin:
    mapsteerVal = map(steerVal,-100,100,0,45);
    steering.write(mapsteerVal);
    // See if we can obtain or "Take" the Serial Semaphore.
    // If the semaphore is not available, wait 5 ticks of the Scheduler to see if it becomes free.
    if ( xSemaphoreTake( xSerialSemaphore, ( TickType_t ) 5 ) == pdTRUE )
    {
      // We were able to obtain or "Take" the semaphore and can now access the shared resource.
      // We want to have the Serial Port for us alone, as it takes some time to print,
      // so we don't want it getting stolen during the middle of a conversion.
      // print out the state of the button:

      xSemaphoreGive( xSerialSemaphore ); // Now free or "Give" the Serial Port for others.
    }

    vTaskDelay(1);  // one tick delay (15ms) in between reads for stability
  }
}

void TaskThrottleMotor( void *pvParameters __attribute__((unused)) )  // This is a Task.
{
  /*
  Throttling starts here

  */
  // l293 motor driver with toggle switch direction
  uint8_t  motorPin = 6;
  uint8_t  togglePin = 7;


  pinMode(motorPin, OUTPUT);
  pinMode(togglePin, OUTPUT);

  for (;;) // A Task shall never return or exit.
  {
    // run command

    throttle(togglePin, motorPin, throttleVal);

    // See if we can obtain or "Take" the Serial Semaphore.
    // If the semaphore is not available, wait 5 ticks of the Scheduler to see if it becomes free.
    if ( xSemaphoreTake( xSerialSemaphore, ( TickType_t ) 5 ) == pdTRUE )
    {
      // We were able to obtain or "Take" the semaphore and can now access the shared resource.
      // We want to have the Serial Port for us alone, as it takes some time to print,
      // so we don't want it getting stolen during the middle of a conversion.
      // print out the state of the button:

      xSemaphoreGive( xSerialSemaphore ); // Now free or "Give" the Serial Port for others.
    }

    vTaskDelay(1);  // one tick delay (15ms) in between reads for stability
  }
}

void TaskUltraSonar( void *pvParameters __attribute__((unused)) )  // This is a Task.
{
  /*
  Throttling starts here

  */
  // l293 motor driver with toggle switch direction
  uint8_t  motorPin = 6;
  uint8_t  togglePin = 7;


  pinMode(motorPin, OUTPUT);
  pinMode(togglePin, OUTPUT);

  for (;;) // A Task shall never return or exit.
  {
    // run command

    throttle(togglePin, motorPin, throttleVal);

    // See if we can obtain or "Take" the Serial Semaphore.
    // If the semaphore is not available, wait 5 ticks of the Scheduler to see if it becomes free.
    if ( xSemaphoreTake( xSerialSemaphore, ( TickType_t ) 5 ) == pdTRUE )
    {
      // We were able to obtain or "Take" the semaphore and can now access the shared resource.
      // We want to have the Serial Port for us alone, as it takes some time to print,
      // so we don't want it getting stolen during the middle of a conversion.
      // print out the state of the button:

      xSemaphoreGive( xSerialSemaphore ); // Now free or "Give" the Serial Port for others.
    }

    vTaskDelay(1);  // one tick delay (15ms) in between reads for stability
  }
}

void TaskVoltRead( void *pvParameters __attribute__((unused)) )  // This is a Task.
{

  for (;;)
  {
    // read the input on analog pin 0:
    int sensorValue = analogRead(A0);

    // See if we can obtain or "Take" the Serial Semaphore.
    // If the semaphore is not available, wait 5 ticks of the Scheduler to see if it becomes free.
    if ( xSemaphoreTake( xSerialSemaphore, ( TickType_t ) 5 ) == pdTRUE )
    {
      // We were able to obtain or "Take" the semaphore and can now access the shared resource.
      // We want to have the Serial Port for us alone, as it takes some time to print,
      // so we don't want it getting stolen during the middle of a conversion.
      // print out the value you read:
      Serial.println(sensorValue);

      xSemaphoreGive( xSerialSemaphore ); // Now free or "Give" the Serial Port for others.
    }

    vTaskDelay(1);  // one tick delay (15ms) in between reads for stability
  }
}

void TaskSerialRW( void *pvParameters __attribute__((unused)) )  // This is a Task.
{
  String receivedArs[2];
  String receivedString;
  String receivedArr[2];
  for (;;)
  {

    // See if we can obtain or "Take" the Serial Semaphore.
    // If the semaphore is not available, wait 5 ticks of the Scheduler to see if it becomes free.
    if ( xSemaphoreTake( xSerialSemaphore, ( TickType_t ) 5 ) == pdTRUE )
    {
      // We were able to obtain or "Take" the semaphore and can now access the shared resource.
      // We want to have the Serial Port for us alone, as it takes some time to print,
      // so we don't want it getting stolen during the middle of a conversion.
      // print out the value you read:
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
		    modeVal = receivedArr[0].toInt();
      }
	    else
	    {
	    }
	  }
	// If no input send value
//      Serial.print(distance);
//      Serial.print(",");
//      Serial.println(volt);
      xSemaphoreGive( xSerialSemaphore ); // Now free or "Give" the Serial Port for others.
	}	

    vTaskDelay(1);  // one tick delay (15ms) in between reads for stability
}


