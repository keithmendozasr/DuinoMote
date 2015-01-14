.PHONY: build makelinks upload

# CXX=avr-gcc
AVR_BASE=/Applications/Arduino.app/Contents/Resources/Java/hardware/tools/avr/bin
CXX=$(AVR_BASE)/avr-g++
CPP=$(AVR_BASE)/avr-gcc
ARDUINO_BASE=/Applications/Arduino.app/Contents/Resources/Java
ARDUINO_LIB=$(ARDUINO_BASE)/libraries

ARDUINO_CORE_DIR=$(ARDUINO_BASE)/hardware/arduino/cores/arduino/
ARDUINO_CORE_FILES=\
	$(ARDUINO_CORE_DIR)/HardwareSerial.cpp \
	$(ARDUINO_CORE_DIR)/IPAddress.cpp \
	$(ARDUINO_CORE_DIR)/Print.cpp \
	$(ARDUINO_CORE_DIR)/wiring.c \
	$(ARDUINO_CORE_DIR)/wiring_digital.c \
	$(ARDUINO_LIB)/WiFi/WiFi.cpp \
	$(ARDUINO_LIB)/WiFi/WiFiClient.cpp \
	$(ARDUINO_LIB)/WiFi/WiFiServer.cpp \
	$(ARDUINO_LIB)/Wire/Wire.cpp \
	$(ARDUINO_LIB)/WiFi/utility/server_drv.cpp \
	$(ARDUINO_LIB)/WiFi/utility/socket.c \
	$(ARDUINO_LIB)/WiFi/utility/spi_drv.cpp \
	$(ARDUINO_LIB)/Wire/utility/twi.c \
	$(ARDUINO_LIB)/WiFi/utility/wifi_drv.cpp \

MOTOR_SHIELD_DIR=/Users/kmendoza/Documents/Arduino/libraries/Adafruit_Motor_Shield/
MOTOR_SHIELD_FILES=\
	$(MOTOR_SHIELD_DIR)/Adafruit_MotorShield.cpp \
	$(MOTOR_SHIELD_DIR)/utility/Adafruit_PWMServoDriver.cpp

INCLUDE=-I ./ \
	-I $(ARDUINO_CORE_DIR) \
	-I $(ARDUINO_BASE)/hardware/arduino/variants/standard \
	-I $(ARDUINO_LIB)/WiFi \
	-I $(ARDUINO_LIB)/WiFi/utility \
	-I $(ARDUINO_LIB)/Wire \
	-I $(ARDUINO_LIB)/Wire/utility \
	-I $(ARDUINO_LIB)/SPI \
	-I $(MOTOR_SHIELD_DIR) \
	-I $(MOTOR_SHIELD_DIR)/utility

LIBS= -lm
MCU=-mmcu=atmega328p
CPU_SPEED=-DF_CPU=16000000UL
CFLAGS=$(MCU) $(CPU_SPEED) -Os -Wall -fno-exceptions -ffunction-sections -fdata-sections -mmcu=atmega328p -MMD -DUSB_VID=null -DUSB_PID=null -DARDUINO=105
PORT=/dev/cuaU0 # FreeBSD
OSTYPE=$(shell uname)
ifeq ($(OSTYPE),Linux)
	PORT=/dev/ttyACM0
else ifeq ($(OSTYPE),Darwin)
	PORT=$(PORT)
endif

PROGNAME=mendozabot

default: build upload

build: makelinks
build: $(PROGNAME).eep $(PROGNAME).hex

makelinks:
	- ln -s $(ARDUINO_CORE_FILES) $(MOTOR_SHIELD_FILES) ./

$(PROGNAME).eep: $(PROGNAME).elf
	$(AVR_BASE)/avr-objcopy -O ihex -j .eeprom --set-section-flags=.eeprom=alloc,load --no-change-warnings --change-section-lma .eeprom=0 $< $@

$(PROGNAME).hex: $(PROGNAME).elf
	$(AVR_BASE)/avr-objcopy -O ihex -R .eeprom $< $@

ARDUINO_OBJECTS=Print.o HardwareSerial.o socket.o WiFi.o WiFiServer.o WiFiClient.o IPAddress.o server_drv.o wifi_drv.o spi_drv.o Wire.o twi.o Adafruit_MotorShield.o Adafruit_PWMServoDriver.o wiring.o wiring_digital.o

$(PROGNAME).elf: $(ARDUINO_OBJECTS) mendozabot.o
	$(CPP) -Os -Wl,--gc-sections -mmcu=atmega328p $^ -o $@ $(LIBS)

upload:
	$(AVR_BASE)/avrdude -V -F -patmega328p -c arduino -b 115200 -D -Uflash:w:$(PROGNAME).hex:i -P$(PORT) -C /Applications/Arduino.app/Contents/Resources/Java/hardware/tools/avr/etc/avrdude.conf

clean:
	@echo -n Cleaning ...
	$(shell rm $(PROGNAME).elf 2> /dev/null)
	$(shell rm $(PROGNAME).hex 2> /dev/null)
	$(shell rm *.o *.d 2> /dev/null)
	$(shell find . -type l -name \\*.c -delete)
	$(shell find . -type l -name \\*.cpp -delete)
	@echo " done"

%.o: %.cpp
	$(CXX) $< $(CFLAGS) $(INCLUDE) -c -o $@

%.o: %.c
	$(CPP) $< $(CFLAGS) $(INCLUDE) -c -o $@
