package com.seckill.miaosha.service;

import com.seckill.miaosha.dao.MiaoshaUserDao;
import com.seckill.miaosha.domain.MiaoshaUser;
import com.seckill.miaosha.exception.GlobalException;
import com.seckill.miaosha.redis.MiaoshaUserKey;
import com.seckill.miaosha.redis.RedisService;
import com.seckill.miaosha.result.CodeMsg;
import com.seckill.miaosha.util.Md5Util;
import com.seckill.miaosha.util.UUIDUtill;
import com.seckill.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by syw on 2018/6/20.
 */

/** 秒杀 User Service 层
 * */
@Service
public class MiaoshaUserService {

    public static final  String COOKIE_TOKEN_NAME = "token";
    @Autowired
    private MiaoshaUserDao miaoshaUserDao;

    @Autowired
    private RedisService redisService;

    public MiaoshaUser getById(Long id){
        return miaoshaUserDao.getById(id);
    }


    /** 登陆验证方法
     * @param loginVo
     * @return
     */
    public String login(HttpServletResponse response, LoginVo loginVo) {
        /*判断 数据对象是否存在*/
        if (loginVo == null )
            throw  new GlobalException(CodeMsg.SERVER_ERROR);
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //判断手机号 是否能查到对象
        MiaoshaUser miaoshaUser = getById(Long.valueOf(mobile));
        if (miaoshaUser == null){
            throw new GlobalException(CodeMsg.LOGIN_ERROR_USER_NOT_ERROR);
        }
        //若手机号存在,对象存在
        //验证密码
        /**通过 传入的密码 和 数据库获取的盐值 进行 MD5 拼接
         * 再和 数据中的密码比较 若相等的 登陆成功
         * */
        String dbPass = miaoshaUser.getPassword();
        String dbsalt = miaoshaUser.getSalt();
        String formPass = Md5Util.formPassToDBPass(password,dbsalt);
        if (!formPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.LOGIN_ERROR_PASS_ERROR);
        }
        /*到达这里说明登陆成功
        * 需要保存 相关 session信息写入 cookie 用于登陆*/
        /*封装一个addCookie 方法 方便 重用*/
        String token = UUIDUtill.uuid();
        addCookie(miaoshaUser,token,response);
        return token;

    }

    /**
     *  根据Token 取出 对象信息
     * @param token
     * @return
     */
    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        /*根据token 和前缀  去redis 中取出 对应的 用户信息值*/
        MiaoshaUser user =  redisService.get(MiaoshaUserKey.token,token,MiaoshaUser.class);
        /*延长有效期*/
        if (user!=null){
            addCookie(user,token,response);
        }
        return user;
    }

    private void addCookie(MiaoshaUser miaoshaUser,String token,HttpServletResponse response){
//        String token = UUIDUtill.uuid();  当我们延长有效期的时候 没有必要每次都生成新的token 直接延用 老的就可以了

        /*将用户sessionId和用户信息 以键值形式 保存到 redis 中*/
        redisService.set(MiaoshaUserKey.token,token,miaoshaUser);
        /*cookie 键值对都是 String类型*/
        Cookie cookie = new Cookie(COOKIE_TOKEN_NAME,token);
        /*设置cookie的过期时间*/
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        /*cookie 保存路径*/
        cookie.setPath("/");
        /*需要一个 response 对象 将cookie 返回给 客户端*/
        response.addCookie(cookie);
    }
}
