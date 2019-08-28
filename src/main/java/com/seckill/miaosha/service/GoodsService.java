package com.seckill.miaosha.service;

import com.seckill.miaosha.dao.GoodsDao;
import com.seckill.miaosha.domain.MiaoshaGoods;
import com.seckill.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by syw on 2018/6/26.
 */

@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    public List<GoodsVo> getGoodsList(){
        return goodsDao.getGoodsList();
    }

    public GoodsVo getGoodsById(Long goodsId){
        return goodsDao.getGoodsById(goodsId);
    }

    //减库存
    public int reduceStock(GoodsVo goodsVo) {
        MiaoshaGoods goods = new MiaoshaGoods();
        goods.setGoodsId(goodsVo.getId());
        return goodsDao.reduceStock(goods);
    }
}
