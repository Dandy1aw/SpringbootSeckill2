package com.seckill.miaosha.service;

import com.seckill.miaosha.domain.MiaoshaGoods;
import com.seckill.miaosha.domain.MiaoshaOrder;
import com.seckill.miaosha.domain.MiaoshaUser;
import com.seckill.miaosha.domain.OrderInfo;
import com.seckill.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {

    /*自己的service一般用自己的dao,当用到别人的dao 可以引入service*/
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    /**
     * 重点：秒杀方法 利用事务
     * 1.减库存 2.下订单！
     * @param user
     * @param goodsVo
     * @return
     */
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo) {
        /*减库存*/
        //该方法返回一个int型数值
        goodsService.reduceStock(goodsVo);

        /*下订单*/
        return orderService.insertOrder(user,goodsVo);

    }
}
