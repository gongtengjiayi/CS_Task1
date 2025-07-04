# 计网课设第一部分


## 代码结构
### 1. `Server.java`
- **功能**：启动服务器，监听指定端口，接受客户端连接，并为每个客户端分配一个线程进行处理。
- **关键步骤**：
  1. 从控制台读取端口号并启动 `ServerSocket`。
  2. 使用固定大小的线程池处理客户端连接。
  3. 调用 `ClientHandler` 类处理每个客户端的请求。
  4. 提供 `shutdown` 方法用于关闭服务器。

### 2. `ClientHandler.java`
- **功能**：处理客户端的连接和请求，接收客户端发送的数据块，对其中的字符串进行反转处理，并将结果返回给客户端。
- **关键步骤**：
  1. 读取客户端的初始化请求，若为协议类型 1，则返回协议类型 2 的响应。
  2. 读取客户端发送的数据块，对其中的字符串进行反转处理。
  3. 将反转后的字符串封装成协议类型 4 的响应并返回给客户端。

### 3. `Client.java`
- **功能**：与服务器建立连接，从文件中读取字符串，将其分割成多个小块并发送给服务器，接收服务器返回的反转后的字符串，拼接并保存到文件中。
- **关键步骤**：
  1. 从控制台读取服务器的 IP 地址和端口号，以及反转请求的最小和最大长度。
  2. 从 `input.txt` 文件中读取字符串。
  3. 将字符串分割成多个小块，每个小块的长度在指定范围内。
  4. 发送初始化请求，等待服务器的响应。
  5. 依次发送每个小块的数据，并接收服务器返回的反转后的字符串。
  6. 将所有反转后的字符串拼接并保存到 `output.txt` 文件中。

## 运行步骤
### 1. 启动服务器
- 编译 `Server.java` 文件：
```sh
javac Server.java
```
- 运行服务器程序：
```sh
java Server
```
- 按照提示输入服务器监听的端口号。

### 2. 启动客户端
- 编译 `Client.java` 文件：
```sh
javac Client.java
```
- 运行客户端程序：
```sh
java Client
```
- 按照提示输入服务器的 IP 地址、端口号，以及反转请求的最小和最大长度。

### 3. 准备输入文件
确保项目根目录下存在 `input.txt` 文件，该文件包含要进行反转处理的字符串。

### 4. 查看输出文件
处理完成后，反转后的字符串将保存到 `output.txt` 文件中。

## 注意事项
- 确保服务器和客户端在同一网络中，或者客户端可以通过指定的 IP 地址和端口号访问服务器。
- 输入文件 `input.txt` 必须存在，否则客户端将抛出异常。
- 客户端和服务器使用 `US-ASCII` 字符集进行通信，确保输入文件中的字符在该字符集范围内。
