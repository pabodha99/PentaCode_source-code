package com.pentacode.pentacode_middleware;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/test")
public class TestController {

    private static Logger audit = LogManager.getLogger("audit-log");

    @GetMapping("/sayHello")
    public String testController() {
        audit.info(",testController");
        return "Hello unsecured pentacode";}

    @GetMapping("/sayHello2")
    public String testController2() {
        audit.info(",testController2");
        return "Hello secured pentacode";
    }
}
