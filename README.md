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

# Structure

<img src="https://user-images.githubusercontent.com/19161231/70580224-56c6df00-1bf6-11ea-9f8c-03c2a77e7207.png" width="80%"/>

The server acts as a broker between the admin page and the client. 
Data communication between the client and the server uses the protocols below. Also, messages between sending and receiving data are compressed using the `Snappy` library.

# Message Protocol

<img src="https://user-images.githubusercontent.com/19161231/70580225-56c6df00-1bf6-11ea-9762-cbc1d92864a1.png" width="60%"/>

This protocol is used to send with receive server and clients

# Class Diagram

## Server
![server](https://user-images.githubusercontent.com/19161231/70579681-b3c19580-1bf4-11ea-90c1-41b3f24ecfd4.png)

## Client
![client](https://user-images.githubusercontent.com/19161231/70579682-b3c19580-1bf4-11ea-9565-9fe42d1f783b.png)
