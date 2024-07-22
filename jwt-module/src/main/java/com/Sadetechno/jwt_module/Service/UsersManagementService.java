package com.Sadetechno.jwt_module.Service;

import com.Sadetechno.jwt_module.Repository.OtpRepository;
import com.Sadetechno.jwt_module.Repository.UsersRepo;
import com.Sadetechno.jwt_module.model.OtpEntity;
import com.Sadetechno.jwt_module.model.OurUsers;
import com.Sadetechno.jwt_module.model.ReqRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

@Service
public class UsersManagementService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public ReqRes register(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();

        if (!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())) {
            resp.setStatusCode(400);
            resp.setMessage("Password and Confirm Password do not match");
            return resp;
        }

        try {
            OurUsers ourUser = new OurUsers();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setConfirmPassword(registrationRequest.getConfirmPassword());
            ourUser.setRole(registrationRequest.getRole());
            ourUser.setName(registrationRequest.getName());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            // Handle null phone number
            ourUser.setPhoneNumber(Optional.ofNullable(registrationRequest.getPhoneNumber()).orElse(""));

            OurUsers ourUsersResult = usersRepo.save(ourUser);
            if (ourUsersResult.getId() > 0) {
                resp.setOurUsers((ourUsersResult));
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes login(ReqRes loginRequest) {
        ReqRes response = new ReqRes();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public ReqRes refreshToken(ReqRes refreshTokenReqiest) {
        ReqRes response = new ReqRes();
        try {
            String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
            OurUsers users = usersRepo.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenReqiest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    public ReqRes getMyInfo(String email) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setOurUsers(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;
    }

    public long getTotalUsers() {
        return usersRepo.count();
    }


//    public ReqRes requestPasswordReset(String email) {
//        ReqRes response = new ReqRes();
//        try {
//            Optional<OurUsers> userOptional = usersRepo.findByEmail(email);
//            if (userOptional.isPresent()) {
//                String otp = generateOtp();
//                otpRepository.save(new OtpEntity(email, otp, LocalDateTime.now()));
//                emailService.sendOtpEmail(email, otp);
//
//                response.setStatusCode(200);
//                response.setMessage("OTP sent successfully");
//            } else {
//                response.setStatusCode(404);
//                response.setMessage("User not found");
//            }
//        } catch (Exception e) {
//            response.setStatusCode(500);
//            response.setMessage("Error occurred while processing request: " + e.getMessage());
//        }
//        return response;
//    }
//
//    public ReqRes verifyOtpAndResetPassword(String email, String otp, String newPassword) {
//        ReqRes response = new ReqRes();
//        try {
//            Optional<OtpEntity> otpEntity = otpRepository.findByEmailAndOtp(email, otp);
//            if (otpEntity.isPresent()) {
//                LocalDateTime otpCreatedAt = otpEntity.get().getCreatedAt();
//                // Check if OTP is expired (e.g., 15 minutes expiry)
//                if (LocalDateTime.now().isBefore(otpCreatedAt.plusMinutes(15))) {
//                    // Proceed with password reset
//                    Optional<OurUsers> userOptional = usersRepo.findByEmail(email);
//                    if (userOptional.isPresent()) {
//                        OurUsers user = userOptional.get();
//                        user.setPassword(newPassword); // Hash the password before saving
//                        usersRepo.save(user);
//
//                        // Optionally, remove OTP from database
//                        otpRepository.delete(otpEntity.get());
//
//                        response.setStatusCode(200);
//                        response.setMessage("Password reset successfully");
//                    } else {
//                        response.setStatusCode(404);
//                        response.setMessage("User not found");
//                    }
//                } else {
//                    response.setStatusCode(400);
//                    response.setMessage("OTP has expired");
//                }
//            } else {
//                response.setStatusCode(400);
//                response.setMessage("Invalid OTP");
//            }
//        } catch (Exception e) {
//            response.setStatusCode(200);
//            response.setMessage("reset code send : " + e.getMessage());
//        }
//        return response;
//    }
//
//    private String generateOtp() {
//        int otp = 1000 + new Random().nextInt(9000);
//        return String.valueOf(otp);
//    }
public ReqRes requestPasswordReset(String email) {
    ReqRes response = new ReqRes();
    try {
        Optional<OurUsers> userOptional = usersRepo.findByEmail(email);
        if (userOptional.isPresent()) {
            String otp = generateOtp();
            otpRepository.save(new OtpEntity(email, otp, LocalDateTime.now()));
            emailService.sendOtpEmail(email, otp);

            response.setStatusCode(200);
            response.setMessage("OTP sent successfully");
        } else {
            response.setStatusCode(404);
            response.setMessage("User not found");
        }
    } catch (Exception e) {
        response.setStatusCode(500);
        response.setMessage("Error occurred while processing request: " + e.getMessage());
    }
    return response;
}

    public ReqRes verifyOtpAndResetPassword(String email, String otp, String newPassword) {
        ReqRes response = new ReqRes();
        try {
            Optional<OtpEntity> otpEntity = otpRepository.findByEmailAndOtp(email, otp);
            if (otpEntity.isPresent()) {
                LocalDateTime otpCreatedAt = otpEntity.get().getCreatedAt();
                if (LocalDateTime.now().isBefore(otpCreatedAt.plusMinutes(15))) {
                    Optional<OurUsers> userOptional = usersRepo.findByEmail(email);
                    if (userOptional.isPresent()) {
                        OurUsers user = userOptional.get();
                        user.setPassword(passwordEncoder.encode(newPassword));
                        usersRepo.save(user);

                        otpRepository.delete(otpEntity.get());

                        response.setStatusCode(200);
                        response.setMessage("Password reset successfully");
                    } else {
                        response.setStatusCode(404);
                        response.setMessage("User not found");
                    }
                } else {
                    response.setStatusCode(400);
                    response.setMessage("OTP has expired");
                }
            } else {
                response.setStatusCode(400);
                response.setMessage("Invalid OTP");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred: " + e.getMessage());
        }
        return response;
    }

    private String generateOtp() {
        return String.format("%04d", new Random().nextInt(10000));
    }

}
