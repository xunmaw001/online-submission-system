package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import com.utils.PageUtils;
import com.utils.Query;

import com.dao.GaojianDao;
import com.entity.GaojianEntity;
import com.service.GaojianService;
import com.entity.view.GaojianView;

/**
 * 稿件信息 服务实现类
 * @since 2021-05-08
 */
@Service("gaojianService")
@Transactional
public class GaojianServiceImpl extends ServiceImpl<GaojianDao, GaojianEntity> implements GaojianService {

    @Override
    public PageUtils queryPage(Map<String,Object> params) {
        if(params != null && (params.get("limit") == null || params.get("page") == null)){
            params.put("page","1");
            params.put("limit","10");
        }
        Page<GaojianView> page =new Query<GaojianView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page,params));
        return new PageUtils(page);
    }

}
