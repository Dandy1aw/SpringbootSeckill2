package com.seckill.miaosha.controller;

import com.seckill.miaosha.domain.MiaoshaUser;
import com.seckill.miaosha.redis.RedisService;
import com.seckill.miaosha.service.GoodsService;
import com.seckill.miaosha.service.MiaoshaUserService;
import com.seckill.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by syw on 2018/6/23.
 */
@Controller
@RequestMapping("/goods")
public class GoodsContorller {
    @Autowired
    private MiaoshaUserService miaoshaUserService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsService goodsService;
    /*跳转商品列表页 并携带 session信息*/
    @RequestMapping("/to_list")
    public String to_list(Model model,MiaoshaUser user){//为了方便手机端 移动端，将参数放在请求中
            /**这一部分本来是用来从request 和cookie 中获取到session 中的 user 数据
             * 由于每次都要传入 数据并做判断 再获取 user 对象
             * 造成代码 冗余
             * 可以直接 重写SpringMvc 配置中 参数解析 AddArguementResolver()方法，让其遍历 参数
             * 并注入 到 controller 中（我们的user 参数）
             * */
//        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken))
//        {/*如果 cookie 中都没有值 返回 登陆界面*/
//            return "login";
//        }
//        /*有限从paramToken 中取出 cookie值 若没有从 cookieToken 中取*/
//        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
//        MiaoshaUser user = miaoshaUserService.getByToken(response,token);
        model.addAttribute("user",user);
        List<GoodsVo> goodsList = goodsService.getGoodsList();
        model.addAttribute("goodsList",goodsList);
        return "goods_list";
    }


    @RequestMapping("/to_detail/{goodsId}") // 前端传入的参数 goodsId
    public String detail(Model model,MiaoshaUser user,
                         @PathVariable("goodsId") Long goodsId){//通过注解@PathVariable获取路径参数
        /*先将user 传进去 用来判断是否登录*/
        model.addAttribute("user",user);
        /*根据传入的Id 通过service 拿到对应的Good信息*/
        GoodsVo goods = goodsService.getGoodsById(goodsId);
        model.addAttribute("goods",goods);

        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long nowTime = System.currentTimeMillis();/* 拿到现在时间的毫秒值*/
        /**这里要做一个秒杀时间的判断 秒杀开始 秒杀结束 秒杀进行
         * */
        int miaoshaStatus = 0;/*用该变量来表示 秒杀的状态 0 表示秒杀未开始 1 开始 2 结束*/
        int remainSeconds = 0; /*表示剩余时间 距离秒杀开始的时间*/
        if (nowTime<startTime){//秒杀未开始
            miaoshaStatus = 0;
            remainSeconds = (int)((startTime-nowTime)/1000);//注意此时是 毫秒值 要除以1000
        }else if (endTime<nowTime){//秒杀结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        return "goods_detail";
    }
}
