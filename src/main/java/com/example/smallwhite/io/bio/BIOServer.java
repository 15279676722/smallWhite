package com.example.smallwhite.io.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yangqiang
 * @create 2021-09-02 11:37
 */
public class BIOServer {
    public static List<Socket> onLineSocketList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        //创建线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        //创建serverSocket
        ServerSocket serverSocket = new ServerSocket(8888);
        Socket socket = null;
        System.out.println("服务端已启动，端口号为8888...");
        System.out.println("等待连接...");
        while ((socket = serverSocket.accept()) != null) {
            System.out.println("线程的信息 id=" + Thread.currentThread().getId() + " 名称=" + Thread.currentThread().getName());
            onLineSocketList.add(socket);
            System.out.println("有一个客户端连接...");
            Socket finalSocket = socket;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    handler(finalSocket);
                }
            });
        }
    }

    public static void handler(Socket socket) {
            try {
                byte[] bytes = new byte[1024];
                //获取输入流
                InputStream inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String message = null;
                while ((message = bufferedReader.readLine()) != null && !socket.isClosed()) {
                    //输出客户端发送的数据
                    //向所有客户端发送消息
                    toMessageAll(message);
                }
            } catch (Exception e) {
                System.out.println("----------------exception");
                onLineSocketList.remove(socket);
            } finally {
                try {
                    socket.close();
                    onLineSocketList.remove(socket);
                    System.out.println("有人退出了!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

    }

    public static void toMessageAll(String message) {
        for (Socket socket : onLineSocketList) {
            OutputStream outputStream = null;
            try {
                outputStream = socket.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream);
                System.out.println("群发：" + message);
                printWriter.println(message);
                printWriter.flush();
            } catch (IOException e) {
                onLineSocketList.remove(socket);
                System.out.println("有人退出了!");
            }
        }
    }
}

