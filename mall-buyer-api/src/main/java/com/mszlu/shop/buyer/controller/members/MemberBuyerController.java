package com.mszlu.shop.buyer.controller.members;


import com.mszlu.shop.buyer.service.members.MemberBuyerService;
import com.mszlu.shop.common.vo.Result;
import com.mszlu.shop.model.buyer.vo.member.MemberVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
public class MemberBuyerController {

    @Autowired
    private MemberBuyerService memberBuyerService;

    @GetMapping
    public Result<MemberVo> getUserInfo() {

        return memberBuyerService.getUserInfo();
    }

}
