# speedometer-master
The Arduino communicate with the phone with a HC05 module. 

The phone checks the acceleration sensors for the alarm, receive every arduino pins triggered by buttons and can turn on every relay inside the motorcycle. For example when there is a blinking light, it is the phone that turn it on and off. The blinking sound is an mp3 from an old Piaggio. It uses the fingerprint sensor to start the UI and the motorcycle.

Edit to mention: the arduino estimates the battery percentage on an analog pin. There is a voltage divider between the battery (83V max) and the arduino (5V max)

Sorry for the pinout written in french, I will try in the future to make one in English. The white letters are the commands Android makes to the Arduino in order to turn on or off a relay.

![alt text](https://user-images.githubusercontent.com/16885275/115361480-eb652800-a1c0-11eb-9bd4-1d9657ae973b.png)

There are 3 limitations that did not bother me much for my specific usage but needs to be issued in the long term : 
1) Android automatically connects to the first bluetooth device registered. So in order to test the code I suggest you to remove the others.
2) While the connection is solid (I left them paired for weeks), when it drops the Android code doesn't try to connect again. If Android tries to send a command at this time then the program will crash and you have to restart it. 
3) Please wait 5 seconds after the program starts in order to ensure the connection is made. You can start the Android app on the page 1 in order to have more feedback on the connection.

Please note that the Android app is designed to stay always on. It displays a black screen when it is in alarm mode. You can password pin the app in order to lock the user in it.
