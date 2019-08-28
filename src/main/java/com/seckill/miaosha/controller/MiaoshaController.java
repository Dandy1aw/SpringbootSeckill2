package com.seckill.miaosha.controller;

import com.seckill.miaosha.domain.MiaoshaOrder;
import com.seckill.miaosha.domain.MiaoshaUser;
import com.seckill.miaosha.domain.OrderInfo;
import com.seckill.miaosha.result.CodeMsg;
import com.seckill.miaosha.service.GoodsService;
import com.seckill.miaosha.service.MiaoshaService;
import com.seckill.miaosha.service.OrderService;
import com.seckill.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    /**秒杀：
     * 逻辑：
     * 1.获取用户 若未登录跳转登陆界面
     * 2.判断库存
     * 3.判断是否重复秒杀
     * 4.执行秒杀 service
     * 5.得到订单对象 进入订单页面
     * */
    @RequestMapping("/do_miaosha")
    public String Miaosha(MiaoshaUser user,Model model,
                          @RequestParam("goodsId")long goodsId){
        /*判断是否登陆*/
        if (user == null){
            return "login";
        }
        GoodsVo goodsVo = goodsService.getGoodsById(goodsId);
        int stock  = goodsVo.getStockCount();
        if (stock <= 0){ // 小于等于0 不能是==0 单线程没有问题
            model.addAttribute("errMsg",CodeMsg.MIAO_SHA_NO_STOCK.getMsg());
            return "miaosha_fail";/*返回到秒杀失败页面*/
        }
        /*判断是否重复秒杀*/
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaByUserAndGood(user.getId(),goodsId);
        if (miaoshaOrder != null){
            model.addAttribute("errMsg",CodeMsg.MIAO_SHA_REPEAT.getMsg());
            return "miaosha_fail";
        }
        /*都过了.执行秒杀*/
        /**执行秒杀： 事务 用 秒杀service 完成
         *
         * */
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsVo);
        model.addAttribute("orderInfo",orderInfo);/* 将订单 信息写入 域中*/
        model.addAttribute("goods",goodsVo);/*商品信息 也写入*/
        return "order_detail";
    }
}
