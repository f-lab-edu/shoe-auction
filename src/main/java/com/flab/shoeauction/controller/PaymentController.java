package com.flab.shoeauction.controller;

import com.flab.shoeauction.common.annotation.CurrentUser;
import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.domain.users.common.Payment;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final UserService userService;

    @GetMapping
    public String paymentPage(Model model) {
        String customerId = UUID.randomUUID().toString();
        model.addAttribute("customerId", customerId);
        return "payment_page";
    }

    @LoginCheck(authority = UserLevel.AUTH)
    @PostMapping
    public @ResponseBody
    void paymentRegistration(@CurrentUser String email, @RequestBody Payment payment) {
        userService.addPaymentMethod(email, payment);
    }
}