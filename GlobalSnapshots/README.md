# ECE382N Package for Global Snapshots
This repo demonstrates how to implement a global shapshot in a distributed system and retore the global state to that snapshot.  

When Main is launched, it creates the distributed system by spinning up multiple nodes and connecting their channels together.  Node 1 is slightly different from the other nodes because it stays coupled to Main which also takes commands from the terminal.  This user interface enables Node 1 to initiate snapshot and restore operations but the user interface could easily be coupled to any node.  

## Table of Contents:
1. [Project Specification](#project-specification)
2. [How to Build and Run the Project](#how-to-build-and-run-the-project)
3. [Node Logs](#node-logs)
3. [Contributors](#contributors)

## Project Specification

The following commands are supported by the user interface

- **0:Exit** - Shutdown all nodes
- **1:Snapshot** - Initiate a global snapshot operation
- **2:Restore** - Initiate a global restore operation.  All nodes restore their state to the state stored in the global snapshot.  

## How to Build and Run the Project

- cd to `..\src\main\java`
- `javac *java`
- `java Main`

## Node Logs
Each node keeps a log of significant events.  These logs are stored in `..\src\main\java` and are labeled `log<id>.txt` where `id` corresponds to the node id.  These logs are used to record each message that is delivered to the node and the state of the node when the message is delivered.

## Contributors
- [Blake De Garza](https://github.com/BDD16) bd6225
- [Jeff Tipps](https://github.com/jag2k2) jt45679
