package org.athena.test.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
@RequestMapping("/template")
public class TemplatePageController {

    @GetMapping("/test")
    public String testPage(Model model) {
        model.addAttribute("title", "Athena Security UAT Console");
        model.addAttribute("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        model.addAttribute("users", Map.of(
            "admin", "admin123",
            "operator", "op123",
            "guest", "guest123"
        ));
        model.addAttribute("description", "Use this page to validate authn/authz/menu/user-management behavior.");
        return "template-test";
    }
}
