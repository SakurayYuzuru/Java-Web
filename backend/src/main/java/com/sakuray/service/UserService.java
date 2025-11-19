package com.sakuray.service;

import com.sakuray.entity.User;
import com.sakuray.dto.user.*;

public interface UserService {
    String register(RegisterDTO dto);
    String login(LoginDTO dto);
}