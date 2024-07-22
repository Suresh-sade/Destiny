package com.Sadetechno.jwt_module.Controller;

import com.Sadetechno.jwt_module.Service.UsersManagementService;
import com.Sadetechno.jwt_module.model.ReqRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class UserManagementController {

    @Autowired
    private UsersManagementService usersManagementService;

    @PostMapping("/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reg) {
        return ResponseEntity.ok(usersManagementService.register(reg));
    }

    @PostMapping("/login")
    @CrossOrigin("http://localhost:4200")
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
        ReqRes response = usersManagementService.verifyOtpAndResetPassword(email, otp, newPassword);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
