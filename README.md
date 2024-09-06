# Messaging App

## Overview
This project is a Distributed System and a multi-threaded messaging application built in Java using sockets. It demonstrates how to implement concurrent client-server communication, handle multiple client connections simultaneously, and process various message types efficiently.

## Features
Concurrent Server: Supports multiple clients connecting and communicating with the server at the same time. The server is designed to handle each client connection in a separate thread, ensuring that all clients can communicate concurrently.

Thread Safety: Special care has been taken to ensure thread safety when multiple threads access shared resources. Synchronization mechanisms (e.g., synchronized blocks, thread-safe collections, etc.) are used to prevent race conditions and ensure data consistency across threads.

Message Handling
Message handling is implemented using the Strategy Pattern. The message header is used to determine which handler should process the incoming message. This pattern allows us to add new message types easily without modifying existing code, thereby enhancing scalability and maintainability.

Scalability and Clean Code: The project leverages the Strategy Pattern to handle different message types, which helps to avoid large if-else or switch-case structures. This design promotes clean, maintainable, and scalable code as new message types can easily be added by defining new message handlers.

EMessageHeader: Enum that defines different message types such as OK, CLIENT_DISCONNECTED, SERVER_DISCONNECTED, SOCKET_CLOSED.
Handlers: Each message type has a corresponding handler that processes the message and performs the appropriate action.

Also DEFINE CUSTOM EXEPTIONS TO HANDLE ERROS