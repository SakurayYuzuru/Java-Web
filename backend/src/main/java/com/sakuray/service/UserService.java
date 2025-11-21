package com.sakuray.service;

import org.springframework.data.domain.Page;

import com.sakuray.dto.user.AddDTO;
import com.sakuray.dto.user.LoginDTO;
import com.sakuray.dto.user.RegisterDTO;
import com.sakuray.dto.user.UpdateDTO;
import com.sakuray.entity.User;

public interface UserService {
    String register(RegisterDTO dto);
    String login(LoginDTO dto);
    String add(AddDTO dto);
    Page<User> getUserByPage(int page, int size);
    String update(Long id, UpdateDTO dto);
    String delete(Long id);
}