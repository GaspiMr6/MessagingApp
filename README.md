# Messaging App

## Overview
This project is a Distributed System and a multi-threaded messaging application built in Java using sockets. It demonstrates how to implement concurrent client-server communication, handle multiple client connections simultaneously, and process various message types efficiently.

## Features
 - **Concurrent Server**: Supports multiple clients connecting and communicating with the server at the same time. The server is designed to handle each client connection in a separate thread, ensuring that all clients can communicate concurrently.
- **Thread Safety**: Special care has been taken to ensure thread safety when multiple threads access shared resources. Synchronization mechanisms are used to prevent race conditions and ensure data consistency across threads.
- **Message Handling**: An enumeration is used as message headers to categorize and handle different types of messages, ensuring clear and efficient communication between the server and clients.
- **Custom Exception Handling**: Custom exception classes, such as `ClientDisconnectsException`, are defined to handle specific errors, providing clearer error handling and improving the robustness of the application.
