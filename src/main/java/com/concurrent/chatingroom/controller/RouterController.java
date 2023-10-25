package com.concurrent.chatingroom.controller;


import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.concurrent.chatingroom.config.WebSocket;
import com.concurrent.chatingroom.pojo.Group;
import com.concurrent.chatingroom.pojo.User;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class RouterController {
    //index
    @RequestMapping(value = {"/home", "/"})
    public String home() {
        return "index";
    }

    @RequestMapping("/home/chatwin/{groupname}")
    public String chatwin(@PathVariable String groupname, HttpServletRequest request, Model model) {
        model.addAttribute("groupname", groupname);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        System.out.println(user);
        session.setAttribute("username", user.getUsername());
        return "chatwindow";
    }

    @RequestMapping("/home/logout")
    public String chatwin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("user", null);
        return "forward:/home";
    }

    @RequestMapping("/empty")
    @ResponseBody
    public void empty(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        pw.println("<h1>Please choose a group from left list.</h1>");
    }

    @RequestMapping("/home/create")
    public String create() {
        return "create";
    }

    @RequestMapping("/create")
    public String create(@RequestParam("groupname") String groupname, HttpSession session) throws IOException {
        try{
            String filePath = System.getProperty("user.dir") + "/" + "userinfo.txt";
//            InputStream is = RouterController.class.getClassLoader().getResourceAsStream("data/userinfo.txt");
            FileInputStream fi  = new FileInputStream(filePath);
            String jsonuserforgroup = IOUtils.toString(fi, String.valueOf(StandardCharsets.UTF_8));
            List<User> users = JSON.parseArray(jsonuserforgroup, User.class);
            List<String> usernames = new ArrayList<>();
            for (User user1 : users){
                usernames.add(user1.getUsername());
            }
            fi.close();
//            写入group.txt
            String groupinfofilePath = System.getProperty("user.dir") + "/" + "groupinfo.txt";
//            InputStream inputStream = RouterController.class.getClassLoader().getResourceAsStream("data/groupinfo.txt");
            FileInputStream fi2  = new FileInputStream(groupinfofilePath);
            String json = IOUtils.toString(fi2, String.valueOf(StandardCharsets.UTF_8));
            List<Group> groups = JSON.parseArray(json, Group.class);
            for (Group group1 : groups) {
                if (groupname.equals(group1.getGroupname())){
                    System.out.println("[system error]:The groupname is existing!");
                    return "forward:/home";
                }
            }
            Group group = new Group(groupname, usernames);
            groups.add(group);
            String groupsJson = JSON.toJSONString(groups);
            System.out.println(groupsJson);
//            InputStream inputStream1 = WebSocket.class.getClassLoader().getResourceAsStream("data/groupinfo.txt");
            //String resourceFilePath = new File(WebSocket.class.getClassLoader().getResource("data/groupinfo.txt").toURI()).getAbsolutePath();
            String resourceFilePath = System.getProperty("user.dir") + "/" + "groupinfo.txt";
            FileOutputStream fileOutputStream = new FileOutputStream(resourceFilePath);
            //写入groupinfo
            fileOutputStream.write(groupsJson.getBytes());
            fi2.close();
            fileOutputStream.close();

//            写入user.txt
//            InputStream inputStream2 = RouterController.class.getClassLoader().getResourceAsStream("data/userinfo.txt");
//            String json2 = IOUtils.toString(inputStream2, String.valueOf(StandardCharsets.UTF_8));
//            List<User> users = JSON.parseArray(json2, User.class);
            List<User> newusers = new ArrayList<>();
            User cuser = (User) session.getAttribute("user");
            for (User user1 : users){
                List<String> groups1 = user1.getGroups();
                groups1.add(groupname);
                user1.setGroups(groups1);
                if(user1.getUsername().equals(cuser.getUsername()) && user1.getPassword().equals(cuser.getPassword())){
                    session.setAttribute("user", user1);
                }
                newusers.add(user1);
            }
            String s = JSON.toJSONString(newusers);
            String userinfoFilePath = System.getProperty("user.dir") + "/" + "userinfo.txt";
            //URL resource2 = WebSocket.class.getClassLoader().getResource("data/userinfo.txt");
            FileOutputStream fileOutputStream2 = new FileOutputStream(userinfoFilePath, false);
            //写入users
            fileOutputStream2.write(s.getBytes());
//            inputStream2.close();
            fileOutputStream2.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return "forward:/home";
    }

    @RequestMapping("/home/login")
    public String login() {
        return "login";
    }

    //service about user
    @RequestMapping("/login")
    public String login(Model model, HttpServletRequest request, HttpServletResponse response, @RequestParam String username, @RequestParam String password) throws IOException {
        HttpSession session = request.getSession();
        response.setDateHeader("Expires", System.currentTimeMillis()+1*60*60*1000);
        // File file = ResourceUtils.getFile("classpath:data/userinfo.txt");
        String userinfoFilePath = System.getProperty("user.dir") + "/" + "userinfo.txt";
        //InputStream inputStream = RouterController.class.getClassLoader().getResourceAsStream("data/userinfo.txt");
        FileInputStream inputStream = new FileInputStream(userinfoFilePath);
//        File file = null;
//        FileUtils.copyInputStreamToFile(inputStream, file);
//        String json = FileUtils.readFileToString(file, "UTF-8");
        String json = IOUtils.toString(inputStream, String.valueOf(StandardCharsets.UTF_8));
        List<User> users = JSON.parseArray(json, User.class);
        for (User user : users){
            if (user.getUsername().equals(username) && user.getPassword().equals(password)){
                session.setAttribute("user", user);
//                model.addAttribute("user", user);
                System.out.println(user);
                return "forward:/home";
            }
        }
        model.addAttribute("msg", "Incorrect Input");
        return "login";
    }

}
