/*
 * Copyright 2014-2015 Keith Mendoza Sr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

static inline void connect()
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
    Serial.println("Connected");
}

inline void setup() {
    Serial.begin(9600);

    connect();
    AFMS.begin();  // create with the default frequency 1.6KHz
}

static inline void handleClient(WiFiServer &server)
{
    WiFiClient client = server.available();
    if(client)
    {
        Serial.println("Got client");
        uint8_t dir = RELEASE;
        uint8_t speed = 100;
        leftMotor->setSpeed(speed);
        rightMotor->setSpeed(speed);

        while(client.connected())
        {
            if(client.available())
            {
                char c = client.read();
                Serial.print("Got data: ");
                Serial.println(c);
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
                    speed=100;
                    dir = RELEASE;
                    break;
                case 'u':
                    if(speed<150 && dir != RELEASE)
                    {
                        speed+=10;
                        leftMotor->setSpeed(speed);
                        rightMotor->setSpeed(speed);
                    }
                    break;
                case 'd':
                    if(speed > 100 && dir != RELEASE)
                    {
                        speed-=10;
                        leftMotor->setSpeed(speed);
                        rightMotor->setSpeed(speed);
                    }
                    break;
                }
            }
        }

        Serial.println("Lost client");
        client.stop();
        leftMotor->run(RELEASE);
        rightMotor->run(RELEASE);
    }
    else
        Serial.println("No client at this time");
}

int main()
{
    init();
    setup();

    WiFiServer server(8888);
    server.begin();
    while(1)
    {
        handleClient(server);
        if(WiFi.status() != WL_CONNECTED)
            connect();
        else
            Serial.println("Still connected");
    }
}
