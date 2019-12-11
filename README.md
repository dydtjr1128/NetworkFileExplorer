# NetworkFileExplorer

![image](https://user-images.githubusercontent.com/19161231/70204936-b1c78480-1765-11ea-836a-5747db9539a9.png)

This project that allows you to view a client's directory on the Admin page, just like the File Explorer in Windows.

The Admin page provides several functions.
- Show file & directory (include name, last-modified date, type, file size)
- Provides a file & directory deletion.
- Provides a file upload/download
- Provides a file move
- Provides a file copy
- Provides a file name change
- Show client connection in real time.
- Support Windows/Linux

## Structure

<img src="https://user-images.githubusercontent.com/19161231/70580224-56c6df00-1bf6-11ea-9f8c-03c2a77e7207.png" width="80%"/>

The server acts as a broker between the admin page and the client. 
Data communication between the client and the server uses the protocols below. Also, messages between sending and receiving data are compressed using the `Snappy` library.

## Message Protocol

<img src="https://user-images.githubusercontent.com/19161231/70580225-56c6df00-1bf6-11ea-9762-cbc1d92864a1.png" width="60%"/>

This protocol is used to send with receive server and clients

## Class Diagram

### Server
![ServerClassDiagram](https://user-images.githubusercontent.com/19161231/70594005-707e1b80-1c22-11ea-929a-8f8c1bc35d4a.png)

### Client
![ClientClassDiagram](https://user-images.githubusercontent.com/19161231/70594004-707e1b80-1c22-11ea-99c1-41efba568910.png)

## Sequence diagram

### Server
![Server_sequenceDiagram](https://user-images.githubusercontent.com/19161231/70585732-7d8e1100-1c08-11ea-9046-ad1ad71fce9d.png)

### Client
![Client_sequenceDiagram](https://user-images.githubusercontent.com/19161231/70585731-7d8e1100-1c08-11ea-993f-b65bf025e317.png)

