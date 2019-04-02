package util;

import bean.user.UserBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketUtil {

    public static ConcurrentHashMap<String, Socket> socketMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Integer> offlineMessageMap = new ConcurrentHashMap<>();
    public static AtomicInteger onlineCount = new AtomicInteger(0);
    public static ConcurrentHashMap<String, Map<String, UserBean>> contactMapOfUser = new ConcurrentHashMap<>();

    public static void sendMessage(Socket socket, String msg) throws IOException{
        msg = AESUtil.encrypt(msg, AESUtil.getChatKey());
        OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
        writer.write(msg);
        int[] endStr = {31, 16, 15, 6, 14, 27};
        for(int i = 0; i < 6; ++i){
            writer.write(endStr[i]);
        }
        writer.flush();
    }

    public static String getMessage(Socket socket) throws  IOException{
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
}
