# CXX=avr-gcc
AVR_BASE=/Applications/Arduino.app/Contents/Resources/Java/hardware/tools/avr/bin
CXX=$(AVR_BASE)/avr-g++
CPP=$(AVR_BASE)/avr-gcc
INSTALL_DIR=/Applications/Arduino.app/Contents/Resources/Java
ARDUINO_BASE_LIB=$(INSTALL_DIR)/libraries

INCLUDE=-I ./ \
    -I $(INSTALL_DIR)/hardware/arduino/cores/arduino \
    -I $(INSTALL_DIR)/hardware/arduino/variants/standard \
    -I $(ARDUINO_BASE_LIB)/WiFi \
    -I $(ARDUINO_BASE_LIB)/WiFi/utility \
    -I $(ARDUINO_BASE_LIB)/Wire \
    -I $(ARDUINO_BASE_LIB)/Wire/utility \
    -I ~/Documents/Arduino/libraries/Adafruit_Motor_Shield \
    -I ~/Documents/Arduino/libraries/Adafruit_Motor_Shield/utility \
    -I $(ARDUINO_BASE_LIB)/SPI

LIBS= -lm
MCU=-mmcu=atmega328p
CPU_SPEED=-DF_CPU=16000000UL
CFLAGS=$(MCU) $(CPU_SPEED) -Os -Wall -fno-exceptions -ffunction-sections -fdata-sections -mmcu=atmega328p -MMD -DUSB_VID=null -DUSB_PID=null -DARDUINO=105
PORT=/dev/cuaU0 # FreeBSD
OSTYPE=$(shell uname)
ifeq ($(OSTYPE),Linux)
	PORT=/dev/ttyACM0
else ifeq ($(OSTYPE),Darwin)
	PORT=/dev/tty.usbmodem1411
endif

default: build upload

build: duinobot.eep duinobot.hex

duinobot.eep: duinobot.elf
	$(AVR_BASE)/avr-objcopy -O ihex -j .eeprom --set-section-flags=.eeprom=alloc,load --no-change-warnings --change-section-lma .eeprom=0 $< $@

duinobot.hex: duinobot.elf
	$(AVR_BASE)/avr-objcopy -O ihex -R .eeprom $< $@

OBJECTS=Print.o HardwareSerial.o socket.o WiFi.o WiFiServer.o WiFiClient.o IPAddress.o server_drv.o wifi_drv.o spi_drv.o Wire.o twi.o Adafruit_MotorShield.o Adafruit_PWMServoDriver.o duinobot.o wiring.o wiring_digital.o
duinobot.elf: $(OBJECTS)
	$(CPP) -Os -Wl,--gc-sections -mmcu=atmega328p $^ -o $@ $(LIBS)

upload:
	$(AVR_BASE)/avrdude -V -F -patmega328p -c arduino -b 115200 -D -Uflash:w:duinobot.hex:i -P$(PORT) -C /Applications/Arduino.app/Contents/Resources/Java/hardware/tools/avr/etc/avrdude.conf

clean:
	@echo -n Cleaning ...
	$(shell rm duinobot.elf 2> /dev/null)
	$(shell rm duinobot.hex 2> /dev/null)
	$(shell rm *.o 2> /dev/null)
	@echo " done"

%.o: %.cpp
	$(CXX) $< $(CFLAGS) $(INCLUDE) -c -o $@

%.o: %.c
	$(CPP) $< $(CFLAGS) $(INCLUDE) -c -o $@
