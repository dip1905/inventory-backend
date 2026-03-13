package com.inventory.controller;

import com.inventory.model.User;
import com.inventory.repository.UserRepository;
import com.inventory.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://inventory-management-4j4h.onrender.com")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty() ||
            !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        String token = jwtUtil.generateToken(email);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("email", email);
        response.put("name", userOpt.get().getName());
        return ResponseEntity.ok(response);
    }
    
    //@PostMapping("/generate-password")
    //public String generatePassword(@RequestBody Map<String, String> body) {
      //  return passwordEncoder.encode(body.get("password"));
    //}
}
