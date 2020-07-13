![issues](https://img.shields.io/github/issues/dydtjr1128/NetworkFileExplorer)
![forks](https://img.shields.io/github/forks/dydtjr1128/NetworkFileExplorer)
![stars](https://img.shields.io/github/stars/dydtjr1128/NetworkFileExplorer)
![license](https://img.shields.io/github/license/dydtjr1128/NetworkFileExplorer)

![image](https://user-images.githubusercontent.com/19161231/80490865-f2d26680-899c-11ea-9743-9a8d15d6e381.png)

## Welcome to NetworkFileExplorer project!

### What Is It?

The NetworkFileExplorer is a cross platform project that allows you to view a client's directory on the Admin page, just like the File Explorer in Windows.

This project was implemented based on Java asynchronous non-blocking socket channel(`AsynchronousSocketChannel`, `AsynchronousServerSocketChannel`). 

`AsynchronousSocketChannel` and `AsynchronousServerSocketChannel` work as an `IOCP` in a Windows and as an `epoll` in a Linux.

#### Demo screenshot

![image](https://user-images.githubusercontent.com/19161231/70204936-b1c78480-1765-11ea-836a-5747db9539a9.png)

The Admin page provides several functions.
- Show file & directory (include name, last-modified date, type, file size)
- Provides a file & directory deletion.
- Provides a file upload/download
- Provides a file move
- Provides a file copy
- Provides a file name change
- Show client connection in real time.
- Support Windows/Linux OS

## Structure

<img src="https://user-images.githubusercontent.com/19161231/70580224-56c6df00-1bf6-11ea-9f8c-03c2a77e7207.png" width="80%"/>

The server acts as a broker between the admin page and the client. 
Data communication between the client and the server uses the protocols below. Also, messages between sending and receiving data are compressed using the `Snappy` library.

## Message Protocol

<img src="https://user-images.githubusercontent.com/19161231/70580225-56c6df00-1bf6-11ea-9762-cbc1d92864a1.png" width="60%"/>

This protocol is used to send with receive server and clients

## Server package structure

![server_package](https://user-images.githubusercontent.com/19161231/70880841-d6e1b000-200d-11ea-9b16-f1d34f53572c.png)

## Class Diagram

### Server
![Server (1)](https://user-images.githubusercontent.com/19161231/70880853-e234db80-200d-11ea-8a6e-0a63d8fc0516.png)

### Client
![ClientClassDiagram](https://user-images.githubusercontent.com/19161231/70594004-707e1b80-1c22-11ea-99c1-41efba568910.png)

## Sequence diagram

### Server
![Server_sequenceDiagram](https://user-images.githubusercontent.com/19161231/70585732-7d8e1100-1c08-11ea-9046-ad1ad71fce9d.png)

### Client
![Client_sequenceDiagram](https://user-images.githubusercontent.com/19161231/70585731-7d8e1100-1c08-11ea-993f-b65bf025e317.png)

## TODO

- [ ] Improve file upload/download
- [ ] File access permission to admin
- [ ] need effective locking about simultaneous access
- [ ] React rendering time out.
- [ ] Event driven based asynchronous UI event about action when success/fail
- [ ] Packaging to docker.

## Contributing
1. Fork it (https://github.com/dydtjr1128/NetworkFileExplorer/)
2. Create your feature branch (git checkout -b feature/issueName)
3. Commit your changes (git commit -am 'Add some fooBar')
4. Push to the branch (git push origin feature/issueName)
5. Create a new Pull Request

<br/> 

<a href="mailto:dydtjr1994@gmail.com" target="_blank">
  <img src="https://img.shields.io/badge/E--mail-Yongseok%20choi-yellow.svg">
</a>
<a href="https://dydtjr1128.github.io/" target="_blank">
  <img src="https://img.shields.io/badge/Blog-cys__star%27s%20Blog-blue.svg">
</a>
