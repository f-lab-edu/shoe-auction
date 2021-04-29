package com.flab.shoeauction.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentViewController {

    @GetMapping("/points/charge")
    public String paymentPage() {
        return "payment_page";
    }
}
