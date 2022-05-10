package com.mszlu.shop.sso.controller;

import com.mszlu.shop.common.security.Token;
import com.mszlu.shop.common.vo.Result;
import com.mszlu.shop.model.buyer.eums.VerificationEnums;
import com.mszlu.shop.sso.service.MemberService;
import com.mszlu.shop.sso.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MembersController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private VerificationService verificationService;

    @PostMapping("/userLogin")
    public Result<Token> userLogin(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestHeader String uuid) {
        if (verificationService.check(uuid, VerificationEnums.LOGIN)) {
            return this.memberService.usernameLogin(username, password);
        } else {
            return Result.fail(-999,"请重新验证");
        }
    }
}
