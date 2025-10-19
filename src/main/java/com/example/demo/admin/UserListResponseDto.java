package com.example.demo.admin;

import com.example.demo.auth.UserDto;
import java.util.List;

public class UserListResponseDto {
    private List<UserDto> users;
    private int totalPages;
    // getter, setter
    public List<UserDto> getUsers() { return users; }
    public void setUsers(List<UserDto> users) { this.users = users; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}

