package com.project.back_end.mvc;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.project.back_end.services.Service;

@Controller
public class DashboardController {

    @Autowired
    private Service service;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        Map<String, String> map = service.validateToken(token, "admin").getBody();
        if (map == null || map.isEmpty()) return "redirect:/";
    
        return "admin/adminDashboard";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        Map<String, String> map = service.validateToken(token, "doctor").getBody();
        if (map == null || map.isEmpty()) return "redirect:/";

        return "doctor/doctorDashboard";
    }
}