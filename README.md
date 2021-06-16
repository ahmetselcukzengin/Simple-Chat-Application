# Simple-Chat-Application
Users can send chat messages in plain text to each other using the client module. The server module, on the other hand, mediates the transmission and storage of messages sent by users. The server supports multiple users to connect and send messages to each other at the same time.


If the receiver is online when the message is sent, the server forwards the message to the receiver without waiting. The client, on the other hand, instantly displays the messages received by the server on the screen. If the recipient is offline, the server will save the message for later transmission. But pending messages will be stored in a collection (like List, Map) in memory. However, since the messages are stored in data structures such as List, Map, the messages will be deleted when the server application is closed.


The protocol of the chat system will consist of the following messages:

- BEGIN_SESSION \<username> : The client uses this message to log in. The server replies by notifying the number of SMS pending in the user's mailbox.

Server: "You have \<N> SMS messages"

  
- SEND_SMS \<receiving user> \<SMS message> :
    The client uses this message to send SMS.

Server: "Your message has been received"


- POP_SMS : The client uses this message to retrieve a pending SMS message. The server returns the first pending SMS message and deletes it from the pending messages list.

Server: \<sending user> \<SMS message>

If there are no pending SMS messages:

Server: "You do not have an SMS message"


- END_SESSION : The client uses this message to log out. After this message, the client closes the socket connection.


- \<Invalid message> : When the client sends a message other than the above, the server replies with "Message not understood".

In the images below, you can see how the application works step by step.

1:<br>
<img src="/img/1.jpg">

2:
<img src="/img/2.jpg">

3:
<img src="/img/3.jpg">

4:
<img src="/img/4.jpg">

5:
<img src="/img/5.jpg">

6:
<img src="/img/6.jpg">
