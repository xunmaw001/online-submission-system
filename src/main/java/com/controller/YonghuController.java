package com.controller;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

import com.annotation.IgnoreAuth;
import com.entity.BianjiEntity;
import com.entity.UserEntity;
import com.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;

import com.entity.YonghuEntity;

import com.service.YonghuService;
import com.utils.PageUtils;
import com.utils.R;

/**
 * 用户
 * 后端接口
 * @author
 * @email
 * @date 2021-05-08
*/
@RestController
@Controller
@RequestMapping("/yonghu")
public class YonghuController {
    private static final Logger logger = LoggerFactory.getLogger(YonghuController.class);

    @Autowired
    private YonghuService yonghuService;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录
     */
    @IgnoreAuth
    @RequestMapping(value = "/login")
    public R login(String username, String password, String captcha, HttpServletRequest request) {
        YonghuEntity user = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("username", username));
        if(user==null || !user.getPassword().equals(password)) {
            return R.error("账号或密码不正确");
        }
        String token = tokenService.generateToken(user.getId(),username, "users", "用户");
        return R.ok().put("token", token);
    }

    /**
     * 退出
     */
    @RequestMapping(value = "logout")
    public R logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return R.ok("退出成功");
    }


    /**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request){
        Integer id = (Integer)request.getSession().getAttribute("userId");
        YonghuEntity user = yonghuService.selectById(id);
        return R.ok().put("data", user);
    }
    /**
     * 注册
     */
    @IgnoreAuth
    @RequestMapping(value = "/register")
    public R register(@RequestBody YonghuEntity user){
//    	ValidatorUtils.validateEntity(user);
        if(yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("username", user.getUsername())) !=null) {
            return R.error("用户已存在");
        }
        yonghuService.insert(user);
        return R.ok();
    }


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params){
        logger.debug("Controller:"+this.getClass().getName()+",page方法");
        PageUtils page = yonghuService.queryPage(params);
        return R.ok().put("data", page);
    }
    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        logger.debug("Controller:"+this.getClass().getName()+",info方法");
        YonghuEntity yonghu = yonghuService.selectById(id);
        if(yonghu!=null){
            return R.ok().put("data", yonghu);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody YonghuEntity yonghu, HttpServletRequest request){
        logger.debug("Controller:"+this.getClass().getName()+",save");
        Wrapper<YonghuEntity> queryWrapper = new EntityWrapper<YonghuEntity>()
            .eq("username", yonghu.getUsername())
            .eq("password", yonghu.getPassword())
            .eq("yonghu_name", yonghu.getYonghuName())
            .eq("sex_types", yonghu.getSexTypes())
            .eq("yonghu_number", yonghu.getYonghuNumber())
            .eq("yonghu_phone", yonghu.getYonghuPhone())
            .eq("yonghu_email", yonghu.getYonghuEmail())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        YonghuEntity yonghuEntity = yonghuService.selectOne(queryWrapper);
        if("".equals(yonghu.getYonghuPhoto()) || "null".equals(yonghu.getYonghuPhoto())){
            yonghu.setYonghuPhoto(null);
        }
        if(yonghuEntity==null){
            yonghuService.insert(yonghu);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody YonghuEntity yonghu, HttpServletRequest request){
        logger.debug("Controller:"+this.getClass().getName()+",update");
        //根据字段查询是否有相同数据
        Wrapper<YonghuEntity> queryWrapper = new EntityWrapper<YonghuEntity>()
            .notIn("id",yonghu.getId())
            .eq("username", yonghu.getUsername())
            .eq("password", yonghu.getPassword())
            .eq("yonghu_name", yonghu.getYonghuName())
            .eq("sex_types", yonghu.getSexTypes())
            .eq("yonghu_number", yonghu.getYonghuNumber())
            .eq("yonghu_phone", yonghu.getYonghuPhone())
            .eq("yonghu_email", yonghu.getYonghuEmail())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        YonghuEntity yonghuEntity = yonghuService.selectOne(queryWrapper);
        if("".equals(yonghu.getYonghuPhoto()) || "null".equals(yonghu.getYonghuPhoto())){
                yonghu.setYonghuPhoto(null);
        }
        if(yonghuEntity==null){
            yonghuService.updateById(yonghu);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }


    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        logger.debug("Controller:"+this.getClass().getName()+",delete");
        yonghuService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
}

