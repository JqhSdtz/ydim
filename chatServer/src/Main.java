import controller.ChatHandleThread;
import controller.ServerHandleThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        System.out.println("Chat Server starting");
        try {
            ServerSocket serverSocket = new ServerSocket(9227);
            ServerSocket chatSocket = new ServerSocket(9228);
            Thread serverThread = new Thread(() -> {
                System.out.println("Server Socket Established");
                try{
                    while (true) {
                        Socket socket = serverSocket.accept();
                        System.out.println("ServerSocket Accept socket " + socket.getInetAddress());
                        socket.setSoTimeout(5 *60 * 1000);
                        Thread serverHandleThread = new Thread(new ServerHandleThread(socket));
                        serverHandleThread.start();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            });
            serverThread.setPriority(Thread.MIN_PRIORITY);
            serverThread.start();
            Thread chatThread = new Thread(() -> {
                System.out.println("Chat Socket Established");
                try{
                    while (true) {
                        Socket socket = chatSocket.accept();
                        System.out.println("ChatSocket Accept socket " + socket.getInetAddress());
                        socket.setSoTimeout(5 * 60 * 1000);
                        Thread chatHandleThread = new Thread(new ChatHandleThread(socket));
                        chatHandleThread.start();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            });
            chatThread.setPriority(Thread.MIN_PRIORITY);
            chatThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
