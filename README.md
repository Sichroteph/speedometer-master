# speedometer-master
The Arduino communicate with the phone with a HC05 module. 

The phone checks the acceleration sensors for the alarm, receive every arduino pins triggered by buttons and can turn on every relay inside the motorcycle. For example when there is a blinking light, it is the phone that turn it on and off. The blinking sound is an mp3 from an old Piaggio.

Edit to mention: the arduino estimates the battery percentage on an analog pin. There is a voltage divider between the battery (83V max) and the arduino (5V max)

Sorry for the pinout written in french, I will try in the future to make one in English. The white letters are the commands Android makes to the Arduino in order to turn on or off a relay.

![alt text](https://user-images.githubusercontent.com/16885275/115361480-eb652800-a1c0-11eb-9bd4-1d9657ae973b.png)
