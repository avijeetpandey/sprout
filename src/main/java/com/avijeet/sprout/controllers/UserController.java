package com.avijeet.sprout.controllers;

import com.avijeet.sprout.config.api.ApiResponse;
import com.avijeet.sprout.config.controller.BaseController;
import com.avijeet.sprout.constants.ApiConstants;
import com.avijeet.sprout.dto.AddressRequestDto;
import com.avijeet.sprout.dto.UserRequestDto;
import com.avijeet.sprout.dto.UserResponseDto;
import com.avijeet.sprout.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> addUser(@RequestBody UserRequestDto userRequestDto) {
        return ok(ApiConstants.DONE_MESSAGE, userService.addUser(userRequestDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserDetails(@RequestParam(name = "email") String email) {
        return ok(ApiConstants.DONE_MESSAGE, userService.getUserDetailsByEmail(email));
    }

    @PatchMapping("/block")
    public  ResponseEntity<ApiResponse<Boolean>> blockUser(@RequestParam String email) {
        boolean result = userService.blockUserAccount(email);
        return ok(ApiConstants.DONE_MESSAGE,result);
    }

    @PostMapping("/address/{userId}")
    public ResponseEntity<ApiResponse<String>> addAddress(
            @PathVariable Long userId,
            @RequestBody AddressRequestDto addressRequestDto) {
        userService.addAddress(userId, addressRequestDto);
        return ok(ApiConstants.DONE_MESSAGE, "Address added successfully");
    }
}
