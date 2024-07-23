package com.Sadetechno.user_module.Controller;
import com.Sadetechno.user_module.Service.UserService;
import com.Sadetechno.user_module.model.User;
import com.Sadetechno.user_module.model.UserCreationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        List<User> users = userService.getAllUsers();
        users.forEach(user -> {
            user.setProfileImagePath(getFullUrl(user.getProfileImagePath()));
            user.setBannerImagePath(getFullUrl(user.getBannerImagePath()));
        });
        return users;
    }
    private String getFullUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        return "http://localhost:8080/" + imagePath;
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/createUserWithImages")
    public ResponseEntity<?> createUserWithImages(@RequestParam("userJson") String userJson,
                                                  @RequestParam("profileImage") MultipartFile profileImage,
                                                  @RequestParam("bannerImage") MultipartFile bannerImage) {
        try {
            UserCreationDTO userCreationDTO = new UserCreationDTO();
            userCreationDTO.setUserJson(userJson);
            userCreationDTO.setProfileImage(profileImage);
            userCreationDTO.setBannerImage(bannerImage);
            User createdUser = userService.createUserWithImages(userCreationDTO);
            return ResponseEntity.ok("User created successfully with ID: " + createdUser.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @ModelAttribute UserCreationDTO userUpdateDTO) {
        try {
            User updatedUser = userService.updateUser(id, userUpdateDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}