package com.concurrent.chatingroom.controller;


import com.alibaba.fastjson2.JSON;
import com.concurrent.chatingroom.pojo.User;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

@Controller
public class RouterController {
    @RequestMapping("/home")
    public String home() {
        return "index";
    }

    @RequestMapping("/home/chatwin/{groupname}")
    public String chatwin(@PathVariable String groupname, Model model) {
        model.addAttribute("groupname", groupname);
        return "chatwindow";
    }

    @RequestMapping("/home/create")
    public String create() {
        return "create";
    }

    @RequestMapping("/home/login")
    public String login() {
        return "login";
    }

    //service about user
    @RequestMapping("/login")
    public String login(Model model, HttpSession session, @RequestParam String username, @RequestParam String password) throws IOException {
        File file = ResourceUtils.getFile("classpath:data/userinfo.json");
        String json = FileUtils.readFileToString(file, "UTF-8");
        List<User> users = JSON.parseArray(json, User.class);
        for (User user : users){
            if (user.getUsername().equals(username)){
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
