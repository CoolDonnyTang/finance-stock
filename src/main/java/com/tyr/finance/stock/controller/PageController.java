package com.tyr.finance.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping(value = {"/index","/"})
    public String index() {
        return "dashboard";
    }

    @RequestMapping("/dataSync")
    public String dashboard() {
        return "data-sync";
    }

    @RequestMapping("/syncAllData")
    public String syncAll() {
        return "data-sync-all";
    }
}
