# speedometer-master
The Arduino communicate with an old rooted Galaxy S6 phone with a HC05 module. The sim reader was broken so I am glad it has a new purpose. Every processes that are not essential had been deleted. The speedometer app is opened in a protected pin mode that prevent someone to go elsewhere without password. There had been an attempt to make the screen waterproof. The metallic case unfortunately makes the gps signal drop by a fair amount, it results by a 2 seconds lag on the speed but is still usable for now.

The phone checks the acceleration sensors for the alarm, receive every arduino pins triggered by buttons and can turn on every relay inside the motorcycle. For example when there is a blinking light, it is the phone that turn it on and off. The blinking sound is an mp3 from an old Piaggio.

A cheap 433mhz transmitter/receiver module had been tried to open the gates. But while it could receive the key, sending it had no effect on my gates. So it is just a soldered cheap programmable 433mhz remote control taped into what I call "the thing".

Edit to mention: the arduino estimates the battery percentage on an analog pin. There is a voltage divider between the battery (83V max) and the arduino (5V max)
