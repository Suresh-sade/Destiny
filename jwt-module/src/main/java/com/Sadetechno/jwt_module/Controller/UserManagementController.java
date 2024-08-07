package com.Sadetechno.jwt_module.Controller;

import com.Sadetechno.jwt_module.Service.UsersManagementService;
import com.Sadetechno.jwt_module.model.OtpEntity;
import com.Sadetechno.jwt_module.model.OurUsers;
import com.Sadetechno.jwt_module.model.ReqRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class UserManagementController {

    @Autowired
    private UsersManagementService usersManagementService;

    @PostMapping("/register")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reg) {
        return ResponseEntity.ok(usersManagementService.register(reg));
    }
    @PostMapping("/verifyOtp")
    public ResponseEntity<ReqRes> verifyOtpAndRegister(@RequestBody ReqRes verificationRequest) {
        ReqRes response = usersManagementService.verifyOtpAndRegister(verificationRequest);

        HttpStatus status;
        switch (response.getStatusCode()) {
            case 201:
                status = HttpStatus.CREATED;
                break;
            case 400:
                status = HttpStatus.BAD_REQUEST;
                break;
            case 500:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
            default:
                status = HttpStatus.OK;
                break;
        }

        return new ResponseEntity<>(response, status);
    }


    @PostMapping("/login")
    @CrossOrigin("http://localhost:3000")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req) {
        return ResponseEntity.ok(usersManagementService.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req) {
        return ResponseEntity.ok(usersManagementService.refreshToken(req));
    }

    @GetMapping("/adminuser/get-profile")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<ReqRes> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes response = usersManagementService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/products/admin/count")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Long> getTotalUsers() {
        long totalUsers = usersManagementService.getTotalUsers();
        return ResponseEntity.ok(totalUsers);
    }

    @PostMapping("/forgot-password")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<ReqRes> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        ReqRes response = usersManagementService.requestPasswordReset(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<ReqRes> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");
        ReqRes response = usersManagementService.verifyOtpAndResetPassword(email, otp, newPassword,confirmPassword);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @PostMapping("/login-with-otp")
    @CrossOrigin("http://localhost:3000")
    public ResponseEntity<ReqRes> loginWithOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        ReqRes response = usersManagementService.loginWithOtp(email, otp);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/users/descending")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<OurUsers>> getUsersDescending() {
        List<OurUsers> users = usersManagementService.getAllUsersDescending();
        return ResponseEntity.ok(users);
    }
    @GetMapping("/otps/descending")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<OtpEntity>> getOtpsDescending() {
        List<OtpEntity> otps = usersManagementService.getAllOtpsDescending();
        return ResponseEntity.ok(otps);
    }

}
