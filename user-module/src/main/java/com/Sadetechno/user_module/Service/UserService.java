package com.Sadetechno.user_module.Service;
import com.Sadetechno.user_module.Repository.UserRepository;
import com.Sadetechno.user_module.model.User;
import com.Sadetechno.user_module.model.UserCreationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final ObjectMapper objectMapper;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    public User createUserWithImages(UserCreationDTO userCreationDTO) throws IOException {
        if (userCreationDTO == null) {
            throw new IllegalArgumentException("UserCreationDTO is null");
        }

        String userJson = userCreationDTO.getUserJson();
        if (userJson == null || userJson.isEmpty()) {
            throw new IllegalArgumentException("userJson is null or empty");
        }

        User user;
        try {
            user = objectMapper.readValue(userJson, User.class);
        } catch (IOException e) {
            log.error("Error deserializing userJson", e);
            throw new IOException("Error deserializing userJson: " + e.getMessage(), e);
        }

        // Process profile image
        if (userCreationDTO.getProfileImage() != null && !userCreationDTO.getProfileImage().isEmpty()) {
            try {
                String profileImagePath = fileUploadService.uploadFile(userCreationDTO.getProfileImage());
                user.setProfileImagePath(profileImagePath);
            } catch (IOException e) {
                log.error("Error uploading profile image", e);
                throw new IOException("Error uploading profile image: " + e.getMessage(), e);
            }
        }

        // Process banner image
        if (userCreationDTO.getBannerImage() != null && !userCreationDTO.getBannerImage().isEmpty()) {
            try {
                String bannerImagePath = fileUploadService.uploadFile(userCreationDTO.getBannerImage());
                user.setBannerImagePath(bannerImagePath);
            } catch (IOException e) {
                log.error("Error uploading banner image", e);
                throw new IOException("Error uploading banner image: " + e.getMessage(), e);
            }
        }

        try {
            User savedUser = userRepository.save(user);
            log.info("Saved User: {}", savedUser);
            return savedUser;
        } catch (Exception e) {
            log.error("Unexpected error saving user", e);
            throw new RuntimeException("Unexpected error saving user: " + e.getMessage(), e);
        }
    }

    public User updateUser(Long id, UserCreationDTO userUpdateDTO) throws IOException {
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (!existingUserOptional.isPresent()) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }

        User existingUser = existingUserOptional.get();

        if (userUpdateDTO == null) {
            throw new IllegalArgumentException("UserCreationDTO is null");
        }

        String userJson = userUpdateDTO.getUserJson();
        if (userJson != null && !userJson.isEmpty()) {
            try {
                User updatedUserData = objectMapper.readValue(userJson, User.class);

                // Update fields from updatedUserData to existingUser
                if (updatedUserData.getName() != null) existingUser.setName(updatedUserData.getName());
                if (updatedUserData.getAboutMe() != null) existingUser.setAboutMe(updatedUserData.getAboutMe());
                if (updatedUserData.getBirthday() != null) existingUser.setBirthday(updatedUserData.getBirthday());
                if (updatedUserData.getPhno() != null) existingUser.setPhno(updatedUserData.getPhno());
                if (updatedUserData.getBloodGroup() != null) existingUser.setBloodGroup(updatedUserData.getBloodGroup());
                if (updatedUserData.getGender() != null) existingUser.setGender(updatedUserData.getGender());
                if (updatedUserData.getCountry() != null) existingUser.setCountry(updatedUserData.getCountry());
                if (updatedUserData.getOccupation() != null) existingUser.setOccupation(updatedUserData.getOccupation());
                if (updatedUserData.getJoined() != null) existingUser.setJoined(updatedUserData.getJoined());
                if (updatedUserData.getEmail() != null) existingUser.setEmail(updatedUserData.getEmail());
                if (updatedUserData.getHobbies() != null) existingUser.setHobbies(updatedUserData.getHobbies());
                if (updatedUserData.getEducation() != null) existingUser.setEducation(updatedUserData.getEducation());
                // Update interests, workExperience, and socialMediaLinks if they are not null
                if (updatedUserData.getInterests() != null) existingUser.setInterests(updatedUserData.getInterests());
                if (updatedUserData.getWorkExperience() != null) existingUser.setWorkExperience(updatedUserData.getWorkExperience());
                if (updatedUserData.getSocialMediaLinks() != null) existingUser.setSocialMediaLinks(updatedUserData.getSocialMediaLinks());

            } catch (IOException e) {
                log.error("Error deserializing userJson", e);
                throw new IOException("Error deserializing userJson: " + e.getMessage(), e);
            }
        }

        // Update profile image
        if (userUpdateDTO.getProfileImage() != null && !userUpdateDTO.getProfileImage().isEmpty()) {
            try {
                String profileImagePath = fileUploadService.uploadFile(userUpdateDTO.getProfileImage());
                existingUser.setProfileImagePath(profileImagePath);
            } catch (IOException e) {
                log.error("Error uploading profile image", e);
                throw new IOException("Error uploading profile image: " + e.getMessage(), e);
            }
        }

        // Update banner image
        if (userUpdateDTO.getBannerImage() != null && !userUpdateDTO.getBannerImage().isEmpty()) {
            try {
                String bannerImagePath = fileUploadService.uploadFile(userUpdateDTO.getBannerImage());
                existingUser.setBannerImagePath(bannerImagePath);
            } catch (IOException e) {
                log.error("Error uploading banner image", e);
                throw new IOException("Error uploading banner image: " + e.getMessage(), e);
            }
        }

        try {
            User savedUser = userRepository.save(existingUser);
            log.info("Updated User: {}", savedUser);
            return savedUser;
        } catch (Exception e) {
            log.error("Unexpected error updating user", e);
            throw new RuntimeException("Unexpected error updating user: " + e.getMessage(), e);
        }
    }
}