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

#define RED 2
#define GREEN 3
#define BLUE 4

//Hard-lined address 1
Adafruit_MotorShield AFMS = Adafruit_MotorShield(0x61); 

//Left wheel
Adafruit_DCMotor *leftMotor = AFMS.getMotor(1);

//Right wheel
Adafruit_DCMotor *rightMotor = AFMS.getMotor(4);

static inline void changeLights(char i)
{
    if(i == RED)
        digitalWrite(RED, HIGH);
    else
        digitalWrite(RED, LOW);
    
    if(i == GREEN)
        digitalWrite(GREEN, HIGH);
    else
        digitalWrite(GREEN, LOW);

    if(i == BLUE)
        digitalWrite(BLUE, HIGH);
    else
        digitalWrite(BLUE, LOW);
}

static inline void connect()
{
    static const char ssid[] = "<ENTER SSID>";
    static const char pass[] = "<ENTER PASS>";

    Serial.println("Connecting");
    changeLights(RED);
    uint8_t status = WiFi.begin((char *)ssid, (char *)pass);
    while(status != WL_CONNECTED)
    {
        delay(10000);
        status = WiFi.begin((char *)ssid, (char *)pass);
    }
    changeLights(GREEN);
    Serial.print("Connected. IP Address: ");
    Serial.println(WiFi.localIP());
}

inline void setup() {
    Serial.begin(9600);

    pinMode(RED, OUTPUT);
    pinMode(GREEN, OUTPUT);
    pinMode(BLUE, OUTPUT);

    connect();
    AFMS.begin();
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
        changeLights(BLUE);
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

        changeLights(GREEN);
        Serial.println("Lost client");
        client.stop();
        leftMotor->run(RELEASE);
        rightMotor->run(RELEASE);
    }
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
    }
}
