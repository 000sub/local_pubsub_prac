package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 캐시를 사용하지 않고 DB를 직접 조회하는 API
     * GET /users/db/1
     */
    @GetMapping("/users/db/{id}")
    public User getUserDirectlyFromDb(@PathVariable Long id) {
        return userService.getUserFromDb(id);
    }

    /**
     * 예시)
     * GET /users/local/1 -> Local Cache 조회
     */
    @GetMapping("/users/local/{id}")
    public User getUserLocalCache(@PathVariable Long id) {
        return userService.getUserFromLocalCache(id);
    }

    /**
     * 예시)
     * GET /users/redis/1 -> Redis Cache 조회
     */
    @GetMapping("/users/redis/{id}")
    public User getUserRedisCache(@PathVariable Long id) {
        return userService.getUserFromRedisCache(id);
    }

    /**
     * 예시)
     * POST /users -> JSON body로 name, email 필드
     */
    @PostMapping("/users")
    public User createOrUpdateUser(@RequestBody User user) {
        return userService.saveOrUpdateUser(user);
    }

    /**
     * 예시)
     * DELETE /users/1
     */
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
