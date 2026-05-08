package com.example.userservice.feignClient;

import com.example.userservice.dto.UserDTOs.UserDTO;
import com.example.userservice.dto.UserDTOs.UserUpdateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "io-service", url = "http://io-service:8080/internal/io")
public interface IOClient {

    @PostMapping("/user/update")
    public UserDTO updateUserData(@RequestBody UserUpdateDTO userDTO);

}
