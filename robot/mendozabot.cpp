#include <Arduino.h>
#include <Wire.h>
#include <SPI.h>
#include <WiFi.h>

#include <Adafruit_MotorShield.h>


//Hard-lined address 1
Adafruit_MotorShield AFMS = Adafruit_MotorShield(0x61); 

//Left wheel
Adafruit_DCMotor *leftMotor = AFMS.getMotor(1);

//Right wheel
Adafruit_DCMotor *rightMotor = AFMS.getMotor(4);

void connect()
{
    static const char ssid[] = "<ENTER SSID>";
    static const char pass[] = "<ENTER PASS>";

    Serial.println("Connecting");
    pinMode(9, OUTPUT);
    digitalWrite(9, HIGH);
    uint8_t status = WiFi.begin((char *)ssid, (char *)pass);
    while(status != WL_CONNECTED)
    {
        delay(10000);
        status = WiFi.begin((char *)ssid, (char *)pass);
    }
    digitalWrite(9, LOW);
}

void setup() {
    connect();
    AFMS.begin();  // create with the default frequency 1.6KHz

    // Set the speed to start, from 0 (off) to 255 (max speed)
    leftMotor->setSpeed(100);
    rightMotor->setSpeed(100);

    rightMotor->run(FORWARD);
    leftMotor->run(FORWARD);
    delay(125);

    rightMotor->run(BACKWARD);
    leftMotor->run(BACKWARD);
    delay(125);

    rightMotor->run(RELEASE);
    leftMotor->run(RELEASE);
}

int main() 
{
    init();
    setup();

    WiFiServer server(80);
    server.begin();
    bool isUp = false;
    while(1)
    {
        digitalWrite(9, isUp ? HIGH : LOW);
        WiFiClient client = server.available();
        if(client)
        {
            uint8_t dir = RELEASE;
            while(client.connected())
            {
                if(client.available())
                {
                    char c = client.read();
                    switch(c)
                    {
                    case 'i':
                        dir = FORWARD;
                        leftMotor->run(dir);
                        rightMotor->run(dir);
                        break;
                    case 'j':
                        leftMotor->run(RELEASE);
                        rightMotor->run(dir != RELEASE ? dir : FORWARD);
                        break;
                    case 'k':
                        leftMotor->run(dir != RELEASE ? dir : FORWARD);
                        rightMotor->run(RELEASE);
                        break;
                    case 'm':
                        dir = BACKWARD;
                        leftMotor->run(dir);
                        rightMotor->run(dir);
                        break;
                    case 's':
                        leftMotor->run(RELEASE);
                        rightMotor->run(RELEASE);
                        break;
                    }
                }
            }

            client.stop();    
            leftMotor->run(RELEASE);
            rightMotor->run(RELEASE);
        }
    }
}
