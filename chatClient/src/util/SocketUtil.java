package util;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketUtil {
    private static Socket serverSocket;
    private static Socket chatSocket;
    private static String serverHost = "47.102.147.24";
    private static int serverPort = 9227;
    private static String chatHost = "47.102.147.24";
    private static int chatPort = 9228;
    private static boolean chatOver = false;

    public static void setChatSocket() throws Exception {
        SocketUtil.chatSocket = new Socket(chatHost, chatPort);
        Thread thread = new Thread(() -> {
            try{
                while (!chatOver) {
                    String msg = getChatMessage();
                    Platform.runLater(() -> ControllerManager.chatController.onMessage(msg));
                }
                chatSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }


    public static void setServerSocket() throws Exception {
        SocketUtil.serverSocket = new Socket(serverHost, serverPort);
    }

    public static void sendMessage(String msg) throws Exception{
        sendMsg(serverSocket, msg);
    }

    public static String getMessage() throws IOException{
        return getMsg(serverSocket);
    }

    public static void sendChatMessage(String msg) throws Exception{
        sendMsg(chatSocket, msg);
    }

    public static String getChatMessage() throws Exception{
        return getMsg(chatSocket);
    }

    private static void sendMsg(Socket socket, String msg) throws Exception{
        if(socket.isClosed())
            setServerSocket();
        msg = AESUtil.encrypt(msg, AESUtil.getChatKey());
        OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
        writer.write(msg);
        int[] endStr = {31, 16, 15, 6, 14, 27};
        for(int i = 0; i < 6; ++i){
            writer.write(endStr[i]);
        }
        writer.flush();
    }

    private static String getMsg(Socket socket) throws IOException{
        if(socket == null)
            return null;
        InputStream in = socket.getInputStream();
        int value;
        int[] endStr = {31, 16, 15, 6, 14, 27};
        int endCnt = 0;
        StringBuilder sb = new StringBuilder();
        while((value = in.read()) != -1){
            if(value == endStr[endCnt]){
                ++endCnt;
                if(endCnt == 6)
                    break;
            }
            else{
                endCnt = 0;
                sb.append((char)value);
            }
        }
        return AESUtil.decrypt(sb.toString(), AESUtil.getChatKey());
    }

    public static void closeChatSocket() throws IOException{
        chatOver = true;
        //chatSocket.close();
    }

    public static void closeServerSocket() throws IOException{
        serverSocket.close();
    }

}
