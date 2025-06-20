import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private volatile boolean isRunning = true;

    public static void main(String[] args) {
        new Server().startServer();
    }

    private void startServer() {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("请输入端口号：");
            port = sc.nextInt();
            
            serverSocket = new ServerSocket(port);
            threadPool = Executors.newFixedThreadPool(10); // 使用固定大小线程池
            
            System.out.println("服务器已启动！端口号为：" + port);
            
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.execute(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("接受客户端连接时出错: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("服务器启动失败: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            ClientHandler client = new ClientHandler(clientSocket, this);
            client.start();
        } catch (Exception e) {
            System.err.println("处理客户端时出错: " + e.getMessage());
            try {
                clientSocket.close();
            } catch (IOException ex) {
                System.err.println("关闭客户端套接字时出错: " + ex.getMessage());
            }
        }
    }

    public synchronized void shutdown() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("关闭服务器套接字时出错: " + e.getMessage());
        }
        
        if (threadPool != null) {
            threadPool.shutdown();
        }
        System.out.println("服务器已关闭");
    }
}