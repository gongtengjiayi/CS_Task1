import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler extends Thread {
    private static final int TYPE_AGREEMENT = 2;
    private static final int TYPE_RESPONSE = 4;
    
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
    }
    
    @Override
    public void run() {
        try (clientSocket;
             BufferedReader reader = new BufferedReader(
                 new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.US_ASCII));
             PrintWriter writer = new PrintWriter(
                 new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.US_ASCII), true)) {
            
            this.in = reader;
            this.out = writer;
            
            String parts = in.readLine();
            if (parts == null || parts.isEmpty()) {
                throw new IOException("无效的客户端请求");
            }

            if (parts.charAt(0) == '1') {
                out.println(createAgreementResponse());
            }

            System.out.printf("%s已连接！%n", clientSocket.getInetAddress().getHostAddress());
            
            int N = Integer.parseInt(parts.substring(2));
            for (int i = 1; i <= N; i++) {
                String data = in.readLine();
                if (data == null) break;
                System.out.printf("收到来自%s的第%d块信息：%s%n", 
                    clientSocket.getInetAddress().getHostAddress(), i, data);
                String reply = createReverseResponse(data);
                System.out.printf("将要发送第%d块信息：%s给%s%n", 
                    i, reply, clientSocket.getInetAddress().getHostAddress());
                out.println(reply);
            }
        } catch (IOException e) {
            System.err.println("客户端处理错误: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("协议格式错误: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("未知错误: " + e.getMessage());
        }finally{
            try {
                clientSocket.close();
                System.out.println("请求已完成，关闭"+clientSocket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                System.err.println("关闭客户端 Socket 时出错: " + e.getMessage());
            }
        }
    }

    private String createAgreementResponse() {
        return String.valueOf(TYPE_AGREEMENT);
    }

    private String createReverseResponse(String data) {
        String[] parts = data.split(" ", 3);
        if (parts.length < 3) {
            throw new IllegalArgumentException("无效的数据格式");
        }
        
        int length = Integer.parseInt(parts[1]);
        String reversedData = reverseString(parts[2]);
        
        return String.format("%d %d %s", TYPE_RESPONSE, length, reversedData);
    }

    private String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}