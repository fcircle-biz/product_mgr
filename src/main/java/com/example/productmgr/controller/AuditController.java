package com.example.productmgr.controller;

import com.example.productmgr.model.AuditLog;
import com.example.productmgr.service.AuditService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("")
    public String index(Model model) {
        List<AuditLog> recentLogs = auditService.getRecentLogs(100);
        model.addAttribute("logs", recentLogs);
        model.addAttribute("stats", auditService.getAuditStats());
        return "audit/dashboard";
    }

    @GetMapping("/logs")
    public String logs(
            Model model,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String commandTag,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        List<AuditLog> logs = auditService.searchLogs(userName, commandTag, startTime, endTime, page, size);
        model.addAttribute("logs", logs);
        model.addAttribute("userName", userName);
        model.addAttribute("commandTag", commandTag);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalCount", auditService.countLogs(userName, commandTag, startTime, endTime));
        
        return "audit/logs";
    }
}