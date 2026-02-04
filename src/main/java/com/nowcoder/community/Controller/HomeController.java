package com.nowcoder.community.Controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @RequestMapping(path="/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //方法调用栈，SpringMVC会自动实例化Model和Page,并将Page注入Model
        page.setRows(discussPostService.selectDiscussPostRows(0));//userId==0 表示当前首页展示discussPost
        page.setPath("/index");
        int total=page.getTotal();
        List<DiscussPost> posts = discussPostService.selectDiscussPost(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (posts != null && !posts.isEmpty()){
            for (DiscussPost post : posts) {
                Map<String,Object> map = new HashMap<>();
                User user=userService.findUserById(post.getUserId());//user_id 是外键
                map.put("post",post);
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);

        return "index";
    }

    // 新增处理/debug/show的方法
    @RequestMapping(path="/debug/show",method = RequestMethod.GET)
    public String getDeBugPage(Model model){
        List<DiscussPost> posts = discussPostService.selectDiscussPost(0,0,10);
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (posts != null && !posts.isEmpty()){
            for (DiscussPost post : posts) {
                Map<String,Object> map = new HashMap<>();
                User user=userService.findUserById(post.getUserId());//user_id 是外键
                map.put("post",post);
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "debug/debug";
    }

}
