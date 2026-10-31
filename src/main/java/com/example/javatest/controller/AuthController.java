package com.example.javatest.controller;

import com.example.javatest.model.LoginRequest;
import com.example.javatest.model.LoginResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Controller
public class AuthController {
    private final RestClient restClient;

    public AuthController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            LoginResponse data = restClient.post()
                    .uri("/auth/login")
                    .body(new LoginRequest(email, password))
                    .retrieve()
                    .body(LoginResponse.class);


            if (data != null && data.token() != null) {
                session.setAttribute("token", data.token());
                session.setAttribute("username", data.username());
                session.setAttribute("profilePictureSrc", toImageSrc(data.profilePicture()));
                return "redirect:/dashboard";
            }
            model.addAttribute("error", "Login failed. Please try again.");

        } catch (RestClientResponseException e) {
            // API answered with 4xx/5xx
            model.addAttribute("error", "Invalid email or password.");
        } catch (ResourceAccessException e) {
            // API on port 3005 is not reachable
            model.addAttribute("error", "Login service is unavailable.");
        }

        model.addAttribute("email", email);   // keep the typed email
        return "login";
    }

    // Accepts a base64 string, a data: URI or an http(s) URL and returns a value usable in <img src>
    private static String toImageSrc(String value) {
        if (value == null || value.isBlank()) return null;
        if (value.startsWith("data:") || value.startsWith("http")) return value;

        String mime = "image/png";
        if (value.startsWith("/9j/"))        mime = "image/jpeg";
        else if (value.startsWith("Qk"))     mime = "image/bmp";
        else if (value.startsWith("R0lGOD")) mime = "image/gif";

        return "data:" + mime + ";base64," + value;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}