package com.concurrent.chatingroom.pojo;

import java.util.List;

public class Group {
    private String groupname;
    private List<String> users;

    public Group(String groupname, List<String> users) {
        this.groupname = groupname;
        this.users = users;
    }

    public Group() {
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
