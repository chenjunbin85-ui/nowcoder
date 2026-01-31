package com.nowcoder.community.Controller;

import com.nowcoder.community.config.AlphaConfig;
import com.nowcoder.community.service.AlphaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/community/alpha")
public class AlphaController {

    @Autowired
    AlphaConfig alphaConfig;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return new AlphaConfig().simpleDateFormat().format(new Date());
//        return simpleDateFormat.format(new Date())+" 注入依赖";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String findData() {
        return data;
    }

    private final String data;
    @Autowired
    public AlphaController(AlphaService alphaService) {
        this.data = alphaService.findAData();
    }

    @RequestMapping("/http")
    @ResponseBody
    public void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //请求的数据
        System.out.println(request.getRequestURI());
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.write("<h1>hello</h1>");
    }

    @RequestMapping(value = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String findStudents(
            @RequestParam(name="current",required = false,defaultValue = "1") int currentPage,
            @RequestParam(name="limit",required = false,defaultValue = "10") int limit) {
            return "students information";
    }

    @RequestMapping(path="/students/{id}",method = RequestMethod.GET)//浏览器从服务器获取数据
    @ResponseBody
    public String studentDetail(@PathVariable("id") int id) {
        return "No." + id +" student's detail";
    }

    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String addStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "add success";
    }

    //响应html数据
    //不加@ResponseBody注解默认返回html
    @RequestMapping(value = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","陈俊斌");
        modelAndView.addObject("age",21);
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    //响应json数据（异步请求）
    //通过 json字符串 实现Java对象和JS对象的兼容
    @RequestMapping("/employee")
    @ResponseBody
    public List getEmployee() {
        List<Map<String ,Object>> list = new ArrayList<>();

        Map hashMap = new HashMap<>();
        hashMap.put("name","邱淑贞");
        hashMap.put("age",21);
        hashMap.put("salary",10000);
        list.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name","关之琳");
        hashMap.put("age",25);
        hashMap.put("salary",12000);
        list.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name","张曼玉");
        hashMap.put("age",23);
        hashMap.put("salary",15000);
        list.add(hashMap);

        return list;
    }
}
