package org.jaun.clubmanager;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class SecuredUsersController {

    @PreAuthorize("hasRole('ROLE_USER') || hasRole('ROLE_M2M')")
    @GetMapping("/members")
    public Map<String, String> members() {
        Map<String, String> map = new HashMap<>();
        map.put("hello", "members");
        return map;
    }

    @PreAuthorize("hasRole('ROLE_USER') || hasRole('ROLE_M2M')")
    @GetMapping("/contacts")
    public Map<String, String> contacts() {
        Map<String, String> map = new HashMap<>();
        map.put("hello", "contacts");
        return map;
    }
}