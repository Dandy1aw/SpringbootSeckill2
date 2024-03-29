package com.seckill.miaosha.dao;

import com.seckill.miaosha.domain.MiaoshaOrder;
import com.seckill.miaosha.domain.OrderInfo;
import com.seckill.miaosha.domain.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id = #{userId} and goods_id = #{goodsId};")
    MiaoshaOrder getMiaoshaOrderByUserAndGoods(@Param("userId") long userId, @Param("goodsId") long goodsId);


    @Insert("insert into order_info (user_id,goods_id,delivery_addr_id,goods_name,goods_count,goods_price,order_channel,status,create_date)" +
            "values(#{userId},#{goodsId},#{deliveryAddrId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate});")
    @SelectKey(keyColumn = "id",keyProperty = "id",resultType = long.class,before = false,statement = "select last_insert_id()")
    int insertOrder(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id,order_id,goods_id) values(#{userId},#{orderId},#{goodsId});")
    void insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);
}
