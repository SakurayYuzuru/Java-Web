package com.sakuray.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakuray.dto.user.LoginDTO;
import com.sakuray.dto.user.RegisterDTO;
import com.sakuray.service.UserService;
import com.sakuray.entity.User;
import com.sakuray.dto.page.PageRequestDTO;
import com.sakuray.dto.user.UpdateDTO;
import com.sakuray.dto.user.DeleteDTO;
import com.sakuray.dto.user.AddDTO;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public String register(@RequestBody RegisterDTO dto) {
        return service.register(dto);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO dto) {
        return service.login(dto);
    }

    @PostMapping("/page")
    public Page<User> getUserByPage(@RequestBody PageRequestDTO dto) {
        return service.getUserByPage(dto.getPage(), dto.getSize());
    }

    @PostMapping("/update")
    public String updateUser(@RequestBody UpdateDTO dto) {
        return service.updateUser(dto.getId(), dto);
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestBody DeleteDTO dto) {
        return service.deleteUser(dto.getId());
    }

    @PostMapping("/add")
    public String addUser(@RequestBody AddDTO dto) {
        return service.AddUser(dto);
    }
}
