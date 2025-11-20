package com.sakuray.service;

import com.sakuray.entity.User;
import com.sakuray.dto.user.*;
import java.util.*;
import org.springframework.data.domain.Page;

public interface UserService {
    String register(RegisterDTO dto);
    String login(LoginDTO dto);
    Page<User> getUserByPage(int page, int size);
    String updateUser(Long id, UpdateDTO dto);
    String deleteUser(Long id);
}