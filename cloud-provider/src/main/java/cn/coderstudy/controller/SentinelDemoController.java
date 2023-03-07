package cn.coderstudy.controller;

import cn.coderstudy.service.SentinelDemoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liujun
 * @version 1.0
 * @description: SentinelDemoController
 * @date 2023/2/14 10:09 下午
 */
@RequestMapping("sentinel-demo")
@AllArgsConstructor
@RestController
public class SentinelDemoController {
    private final SentinelDemoService sentinelDemoService;


    @GetMapping("sphu")
    public String sphu(){
        return sentinelDemoService.sphu();
    }
}
