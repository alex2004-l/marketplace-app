package com.example.userservice.feignClient;

import com.example.userservice.dto.UserDTOs.AddUserDto;
import com.example.userservice.dto.UserDTOs.UserDTO;
import com.example.userservice.dto.UserDTOs.UserUpdateDTO;
import com.example.userservice.model.UserModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@FeignClient(name = "io-service", url = "http://io-service:8080/internal/io")
public interface IOClient {

    @PostMapping("/user/update")
    public UserDTO updateUserData(@RequestBody UserUpdateDTO userDTO, @RequestParam("keycloakId") String keycloakId);

    @GetMapping("/user/check_if_exists/{id}")
    Boolean checkIfUserExists(@PathVariable String id);

    @PostMapping("/user/add_user")
    public void addUser(@RequestBody AddUserDto dto);

    @GetMapping("/user/find_user/{id}")
    public Optional<UserModel> findUserByKeycloakId(@PathVariable String id);

    @DeleteMapping("/user/delete_users")
    public void deleteUsersTable();
}
