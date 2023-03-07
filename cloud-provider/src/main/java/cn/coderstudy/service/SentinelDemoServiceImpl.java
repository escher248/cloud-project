package cn.coderstudy.service;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;

/**
 * @author liujun
 * @version 1.0
 * @description: SentinelDemoServiceImpl
 * @date 2023/2/14 10:11 下午
 */
@Service
public class SentinelDemoServiceImpl implements SentinelDemoService {

    @Override
    public String sphu() {
        Entry entry = null;

        try {
             entry = SphU.entry("test-sphu");
             return "success";
        }catch (BlockException ex){
            return "block";
        }finally {
            if (entry != null){
                entry.exit();
            }
        }
    }
}
