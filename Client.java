import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.SwingUtilities;

public class Client {
    private static Random random = new Random();

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Client().init();
        });
    }

    private void init() {
        Scanner sc = new Scanner(System.in);
        int port;
        String IpAddress;
        System.out.println("请输入IP地址：");
        IpAddress = sc.nextLine();
        System.out.println("请输入端口：");
        port = sc.nextInt();
        try {
            clientSocket = new Socket(IpAddress, port);
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "US-ASCII"), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "US-ASCII"));

            System.out.println("请输入最小reverseRequest长度");
            int Lmin = sc.nextInt();
            System.out.println("请输入最大reverseRequest长度");
            int Lmax = sc.nextInt();
            sc.nextLine();
            String longString = Read("input.txt");
            List<String> strings = qiepian(longString, Lmin, Lmax);
            Initialization initialization = new Initialization(strings.size());
            while (true) {
                out.println(initialization.toString());
                if (in.readLine().equals("2"))
                    break;
                else
                    System.exit(666);
            }
            System.out.println("连接成功！");
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (String str : strings) {
                i++;
                Thread.sleep(2000);
                System.out.println("正在发送第" + i + "块报文：" + str);
                out.println(str);
                String[] reply = in.readLine().split(" ", 3);
                System.out.println("收到第" + i + "块" + reply[2]);
                sb.insert(0, reply[2]);
            }
            System.out.println("发送、接收完毕！\n最后的生成结果为：" + sb.toString());
            Save(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            sc.close();
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (clientSocket != null)
                    clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> qiepian(String input, int minLen, int maxLen) {

        if (minLen <= 0 || maxLen < minLen) {
            throw new IllegalArgumentException("minLen 必须 > 0 且 maxLen >= minLen");
        }
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }

        if (input.length() < minLen) {
            List<String> result = new ArrayList<>();
            reverseRequest request = new reverseRequest(input);
            result.add(request.toString());
            return result;
        }

        List<String> slices = new ArrayList<>();
        StringBuilder sb = new StringBuilder(input);

        while (sb.length() >= minLen) {
            int remaining = sb.length();
            int maxPossibleSlice = Math.min(maxLen, remaining);

            int sliceLenth = random.nextInt(maxPossibleSlice - minLen + 1) + minLen;

            String slice = sb.substring(0, sliceLenth);
            reverseRequest request = new reverseRequest(slice);
            slices.add(request.toString());

            sb.delete(0, sliceLenth);
        }
        if (sb.length() > 0) {
            if (!slices.isEmpty()) {
                int lastIndex = slices.size() - 1;
                slices.set(lastIndex, slices.get(lastIndex) + sb.toString());
            } else {
                reverseRequest request = new reverseRequest(sb.toString());
                slices.add(request.toString());
            }
        }
        return slices;
    }

    private String Read(String filepath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                contentBuilder.append(currentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();

    }

    private void Save(String s) {
        try (FileWriter writer = new FileWriter("output.txt")) {
            writer.write(s);
            System.out.println("文件保存成功！");
        } catch (IOException e) {
            System.err.println("保存文件时出错: " + e.getMessage());
        }
    }

    private class Initialization {
        private static final short TYPE = 1;
        private int N;
        Initialization(int N) {
            this.N = N;
        }
        @Override
        public String toString() {
            return String.valueOf(TYPE) + " " + String.valueOf(N);
        }
    }

    private class reverseRequest {
        private static final short TYPE = 3;
        private int length;
        private String data;

        reverseRequest(String data) {
            length = data.length();
            this.data = data;
        }

        @Override
        public String toString() {
            return String.valueOf(TYPE) + " " + String.valueOf(length) + " " + data;
        }

    }
}
