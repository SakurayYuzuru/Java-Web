package com.sakuray.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sakuray.entity.User;

@Repository
// 继承 JpaRepository，<实体类型, 主键类型>
public interface UserRepo extends JpaRepository<User, Long> {

    /**
     * Spring Data JPA 魔法：
     * 只需要定义方法签名，Spring 会自动根据方法名生成对应的 SQL 查询。
     * @param username 用户名
     * @return 匹配的用户对象 (Optional 避免空指针)
     */
    User findByUsername(String username);
    Page<User> findAll(Pageable pageable);

    // 💡 提示：您也可以使用 Optional 来更好地处理“用户不存在”的情况
    // Optional<User> findByUsername(String username);
    // 但为了保持与您现有 service 层的兼容性，我们使用原始的 User 返回类型。
}