package com.flab.shoeauction.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApiGuideViewController {

    @GetMapping("docs")
    public String apiGuideDocs() {
        return "api-guide";
    }
}
