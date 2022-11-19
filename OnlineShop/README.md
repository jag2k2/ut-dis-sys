# ECE382N Online Store System
This repo includes both a server and client application that implements an online store system.  The client and server supports both TCP and UDP connections and the server can process requests from multiple clients concurrently.  

When launched, the server immediately spawns three threads (TCPListener, UDPListener and CommandHandler).  The TCP and UDP Listener threads are responsible for receiving incoming commands.  When a command is received by either listener, the command and the communication socket is passed to the CommandHandler using a queue.  The CommandHandler processes these commands from the queue one at a time, thus protecting the critical section from race conditions.

## Table of Contents:
1. [Project Specification](#project-specification)
2. [How to Build and Run the Project](#how-to-build-and-run-the-project)
3. [Contributors](#contributors)

## Project Specification

The following commands may be issued by the client

- **setmode T|U** - changes the connection mode to TCP / UDP respectively
- **purchase \<user-name\> \<product-name\> \<quantity\>** - places an order and returns an order id if successful
- **cancel \<order-id\>** - cancels a pending order
- **search \<user-name\>** - returns all pending orders for the specified user name
- **list** - displays inventory of the store

## How to Build and Run the Project

- cd to `..\src\main\java`
- `javac .\*java`
- `java Client localhost <tcp-port> <udp-port>`
- `java Server <tcp-port> <udp-port> .\input\inventory.txt`
### To Run the Project useful commands as implemented
- 0 + [enter] : Exit

#### Commands to be implemented
- Snapshot -- to be implemented in Node.java
- Restore -- to be implemented in Node.java

## Contributors
- [Blake De Garza](https://github.com/BDD16) bd6225
- [Jeff Tipps](https://github.com/jag2k2) jt45679
