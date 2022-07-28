package sanchay.server.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SanchayDefaultController {
    @GetMapping("/home")
    public String getHome() {
        return "home.html";
    }
}