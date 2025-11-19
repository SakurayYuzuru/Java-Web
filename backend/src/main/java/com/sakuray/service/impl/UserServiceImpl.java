package com.sakuray.service.impl;

import com.sakuray.dto.user.LoginDTO;
import com.sakuray.dto.user.RegisterDTO;
import com.sakuray.entity.User;
import com.sakuray.repo.UserRepo;
import com.sakuray.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo repo;

    @Override
    public String register(RegisterDTO dto) {
        User exist = repo.findByUsername(dto.getUsername());
        if (exist != null) {
            return "用户名已存在";
        }

        User user = new User(dto.getUsername(), dto.getPassword());
        repo.save(user);

        return "注册成功";
    }

    @Override
    public String login(LoginDTO dto) {
        User user = repo.findByUsername(dto.getUsername());

        if (user == null) {
            return "用户不存在";
        }
        if (!user.getPassword().equals(dto.getPassword())) {
            return "密码错误";
        }

        return "登录成功";
    }
}
