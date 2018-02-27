                                      
# Project Documentation
## App Name – Music Sender 2



MusicSender2 uses wifi direct to send data from a server device to client devices and tries to synchronize the audio playing in them. The implementation of the app requires sending files of size as large as 10 to 14 MB. Hence, due to slow data transfer rate and connectivity problems, Bluetooth isn’t the best way to implement communication for this application.

The application contains 2 major UI Component:
1. Music List – Lists all the audio files present in the device and allows to run them.
2. Connect – Responsible for establishing a connection with other devices.


-----------------------------------------------------------------------------------------------------------------

### User Guide for the app:
1. Make sure that ‘Automatic date & time’ and ‘Automatic time zone’ (under Setting -> Date & Time) are selected on both devices. 
2. Launch App on both devices. 
3. Grant storage permission to the app and make sure wifi hotspot is turned off.
4. Click the floating button ( right bottom button with email icon) on the screen of the device which contains the audio to be played.
5. On the other device, move to Connect option from side menu
6. List of all available devices should appear if any. 
7. Click on the device to connect. 
8. Once connection is established, click on the audio to be played

  Devices should start playing the audio after 10 seconds.

Alternatively, if audio needs to be played on a single device then click on the audio from music list that needs to be played. The audio should start playing with zero delay if the device is not connected.

--------------------------------------------------------------------------------------------------------------


### Working:
The WiFi P2p is a single master(server) multi-slave(client) full duplex protocol. The connection is established after making a group. The user can explicitly make group making him/her the group owner and hence server or if users haven’t made a group and are trying to establish a connection then the app automatically chooses a device and makes it group owner.
Sending data (or ACK) from clients to the server will result in congestion on the server side as there can be multiple clients. Hence, this app only allows server of the group to send data and no data what so ever is being sent from clients
After the connection is established, the server device gets system time up to millisecond of precision, add 10 seconds to it and stores it to be sent(let us call it timeToRun). It also schedules the audio to be played exactly at timeToRun. As devices are fetching time from network provider, hence, they are highly accurate and synchronized. Delay allows to wind up all connection and data transfer task before audio is played. Server and client connect their socket and timeToRun is send from server to client. After this, the audio is sent from the server to clients and clients schedule the received audio to be played at timeToRun.
Hence, audio files are triggered to play at the same time with accuracy and precision in milliseconds.


--------------------------------------------------------------------------------------------------------------

### Problem with the app:
* The app presently is designed only for single audio file transfer, after which devices are still connected but clients are not listening to state change.
* Although audio file are triggered to play with accuracy of milliseconds (‘mediaPlayer.start()’ being called with maximum 10ms, minimum 0 ms and an average of 2ms difference) but the start method in itself consumes a small amount of time to function which depends on device to device, thus desynchronizing the audio.
* The app shows all devices in range with wifiP2p enabled and not just the device running this app. Also, it shows no difference between server and client device.
* Didn’t perform test for multiple clients due to lack of devices.
                   
To stop music and connection, the app needs to be killed. This can be done by clicking the button next to home button which shows all onPause(running in background) application and sliding the app away.



<h4 align="center"> The apk file is available in the repo to try out the app.</h4>
 
             ------------------------------------------ xxx ------------------------------------------ 

