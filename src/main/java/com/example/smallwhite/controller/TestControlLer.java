package com.example.smallwhite.controller;

import com.example.smallwhite.entity.BabyImage;
import com.example.smallwhite.entity.DemoData;
import com.example.smallwhite.entity.testClass;
import com.example.smallwhite.service.TestClass;
import com.example.smallwhite.utils.BaseBusinessException;
import com.example.smallwhite.utils.CheckUtil;
import com.example.smallwhite.utils.ResultData;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author: yangqiang
 * @create: 2020-03-24 19:53
 */
@Slf4j
@RestController
public class TestControlLer {
    @Value("${server.port:}")
    private String port;
    public StringBuffer stringBuffer = new StringBuffer();
    public StringBuilder stringBuilder = new StringBuilder();
    @GetMapping("/verify_wx_token")
    public void login(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
                out.write(echostr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
    @GetMapping("/1")
    public ResultData test1() throws InterruptedException {
        Thread.sleep(1000);
        return ResultData.ok().message(port);
    }
    @GetMapping("/2")
    public ResultData test2(){
        try {
            int i = 1/0;
        }catch (Exception e){
            throw new BaseBusinessException(300,e.getMessage());

        }
        System.out.println("22");
        return ResultData.ok();
    }
    @PostMapping("/3")
    public void create( @Valid BabyImage babyimage){

        System.out.println(babyimage.toString());
    }
    @ResponseExcel(name = "lengleng", sheet = "demoList")
    @GetMapping("/e1")
    public List<DemoData> e1() {
        List<DemoData> dataList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            DemoData data = new DemoData();
            data.setUsername("tr1" + i);
            data.setPassword("tr2" + i);
            dataList.add(data);
        }
        return dataList;
    }
    @RequestMapping("/testSync")
    public void testSync(){
        stringBuffer.append("1");
        stringBuilder.append("1");
        System.out.println(stringBuffer.length()+"--"+stringBuilder.length());
    }
//    @Autowired
//    testClass clazz;
    /**
     * 测试多线程下的bean是否是线程安全的
     * singleton:bean的作用域默认是单例情况下 同一个bean的多次请求是共享对象的 所以这时候线程不安全
     * prototype:作用域改成原型模式后 每一次请求都重新获得一个对象 这时候是线程安全的
     * */
    @GetMapping("/testBean")
    public void testBean(){
//        clazz.setKey(1);
//        clazz.setName("yangqiang");
//        System.out.println(clazz.toString());
    }

}
