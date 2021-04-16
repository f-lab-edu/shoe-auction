package com.flab.shoeauction.controller;

import com.flab.shoeauction.common.annotation.CurrentUser;
import com.flab.shoeauction.controller.dto.PointDto;
import com.flab.shoeauction.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public String paymentPage() {
        return "payment_page";
    }

    @PostMapping
    @ResponseBody
    public void payment(@CurrentUser String email, @RequestBody PointDto requestDto) {
        System.out.println(email);
        System.out.println(requestDto.getPoint());
        paymentService.charging(email, requestDto);
    }
}
