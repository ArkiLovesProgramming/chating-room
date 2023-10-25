package com.concurrent.chatingroom.config;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.concurrent.chatingroom.pojo.Group;
import com.concurrent.chatingroom.pojo.User;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket/{groupid}/{userId}")
    @Component
    public class WebSocket {
        private static ConcurrentHashMap<String, com.concurrent.chatingroom.config.WebSocket> webSocketMap = new ConcurrentHashMap<>();
        //Example Create a session. This session is the websocket session
        private Session session;

        //Added a method for actively sending messages to clients
        public static void sendMessage(Object message, String userId) {
            com.concurrent.chatingroom.config.WebSocket webSocket = webSocketMap.get(userId);
//            System.out.println(userId + "的socket 是否打开：" + webSocket.session.isOpen());//因为它关闭的时候已经移除了
            if (webSocket != null) {
                try {
                    webSocket.session.getBasicRemote().sendText(JSONUtil.toJsonStr(message));
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> map = mapper.readValue((String) message, Map.class);
                    System.out.println(map);
                    System.out.println("【websocket message】send message successfully, user="+userId+", content:"+ (String) map.get("msg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    public static ConcurrentHashMap<String, WebSocket> getWebSocketMap() {
        return webSocketMap;
    }

    public static void setWebSocketMap(ConcurrentHashMap<String, WebSocket> webSocketMap) {
        WebSocket.webSocketMap = webSocketMap;
    }

    //前端请求时一个websocket时
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        webSocketMap.put(userId, this);
        String msg = "{\n" +
                "    \"code\":\"1\",\n" +
                "    \"msg\":\"[System notification]:connected with this group\",\n" +
                "    \"sender\":\"system\"\n" +
                "}";
        sendMessage(msg, userId);
        System.out.println("【websocket message】new connection, userid is " +userId + " Currently online ：" + webSocketMap.size());
    }

    //The front-end closes when a websocket
    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        webSocketMap.remove(userId);
        System.out.println("【websocket message】disconnected, total num:"+ webSocketMap.size());
    }

    //The front end sends messages to the back end
    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId, @PathParam("groupid") String groupid) throws IOException {
        if (!message.equals("ping")) {
            System.out.println("【websocket message】receive group " + groupid + " client end " + userId + " message:"+message);
            // File file = ResourceUtils.getFile("classpath:data/groupinfo.txt");  // jar invalidated
            String groupinfoFilePath = System.getProperty("user.dir") + "/" + "groupinfo.txt";
            //InputStream inputStream = WebSocket.class.getClassLoader().getResourceAsStream("data/groupinfo.txt");
//            File file = null;
//            FileUtils.copyInputStreamToFile(inputStream, file);
//            String json = FileUtils.readFileToString(file, "UTF-8");
            FileInputStream inputStream = new FileInputStream(groupinfoFilePath);
            String json = IOUtils.toString(inputStream, String.valueOf(StandardCharsets.UTF_8));
            List<Group> groups = JSON.parseArray(json, Group.class);
            for (Group group : groups){
                if (group.getGroupname().equals(groupid)){
                    for (String user : group.getUsers()){
                        if (!user.equals(userId)){
                            sendMessage(message, user);
                        }
                    }
                }
            }
        }
    }


}
