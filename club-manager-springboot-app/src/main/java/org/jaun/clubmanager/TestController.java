package org.jaun.clubmanager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/greeting")
    public String test() {
        return "hello";
    }
}
