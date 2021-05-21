package com.flab.shoeauction.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentViewController {

    @GetMapping("/view/points/charge")
    public String paymentPage() {
        return "payment_page";
    }
}
