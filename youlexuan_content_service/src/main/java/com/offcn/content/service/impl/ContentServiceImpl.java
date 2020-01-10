package com.offcn.content.service.impl;

import java.util.List;

import javassist.tools.reflect.CannotCreateException;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.mapper.TbContentMapper;
import com.offcn.pojo.TbContent;
import com.offcn.pojo.TbContentExample;
import com.offcn.pojo.TbContentExample.Criteria;
import com.offcn.content.service.ContentService;
import com.github.pagehelper.PageInfo;
import org.springframework.data.redis.core.RedisTemplate;

import javax.security.auth.login.CredentialException;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbContent> findAll() {
        return contentMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageInfo findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TbContent> list = contentMapper.selectByExample(null);
        return new PageInfo(list);
    }

    /**
     * 增加
     */
    @Override
    public void add(TbContent content) {
        //添加的广告 content有所属的分类的id
        //需要根据分类id删除缓存中的content map中的对应广告集合
        redisTemplate.boundHashOps("content").delete(content.getCategoryId() + "");
        contentMapper.insert(content);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbContent content) {
        //获取数据原始数据的分类id
        TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
        Long cateId = tbContent.getCategoryId();
        redisTemplate.boundHashOps("content").delete(content.getCategoryId() + "");
        //注意将包装类变成数字做比较
        if (cateId.longValue() != content.getCategoryId().longValue()) {
            redisTemplate.boundHashOps("content").delete(cateId + "");
        }
        contentMapper.updateByPrimaryKey(content);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbContent findOne(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //先通过被删除的广告id得到对象,获取分类id
            TbContent content = contentMapper.selectByPrimaryKey(id);
            redisTemplate.boundHashOps("content").delete(content.getCategoryId() + "");
            contentMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageInfo findPage(TbContent content, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbContentExample example = new TbContentExample();
        Criteria criteria = example.createCriteria();

        if (content != null) {
            if (content.getTitle() != null && content.getTitle().length() > 0) {
                criteria.andTitleLike("%" + content.getTitle() + "%");
            }
            if (content.getUrl() != null && content.getUrl().length() > 0) {
                criteria.andUrlLike("%" + content.getUrl() + "%");
            }
            if (content.getPic() != null && content.getPic().length() > 0) {
                criteria.andPicLike("%" + content.getPic() + "%");
            }
            if (content.getStatus() != null && content.getStatus().length() > 0) {
                criteria.andStatusLike("%" + content.getStatus() + "%");
            }
        }

        List<TbContent> list = contentMapper.selectByExample(example);
        return new PageInfo(list);
    }

    @Override
    public List<TbContent> findBannerByCategoryId(Long cateId) {
        //查询分类下所有的广告之前判断缓存是否存在,如果存在直接返回存储的数据即可
        List<TbContent> list = (List<TbContent>) redisTemplate.boundHashOps("content").get(cateId + "");
        if (null == list || list.size() == 0) {
            TbContentExample example = new TbContentExample();
            Criteria criteria = example.createCriteria();
            criteria.andCategoryIdEqualTo(cateId);
            criteria.andStatusEqualTo("1");
            example.setOrderByClause("sort_order");
            list = contentMapper.selectByExample(example);
            //使用模板存储数据库查询得到的list集合
            redisTemplate.boundHashOps("content").put(cateId + "", list);
        } else {
            System.out.println("==========from cache read  data========== ");
        }
        return list;
    }

}
