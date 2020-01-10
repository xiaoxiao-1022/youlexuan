package com.offcn.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.offcn.pojo.TbBrand;
import com.offcn.pojo.entity.Result;
import com.offcn.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: wyan
 * @Date: 2019-11-19 15:43
 * @Description:
 */
@RestController
@RequestMapping("brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    //接受请求返回brand品牌的json
    @RequestMapping("findAll")
    public List<TbBrand> findAll(){
       return  brandService.findAll();
    }
    @RequestMapping("findPage")
    public PageInfo<TbBrand> findPage(@RequestParam(defaultValue = "1")Integer pageNum,
                                      @RequestParam(defaultValue = "5")Integer pageSize){

        PageInfo<TbBrand> pageInfo = brandService.findPage(pageNum, pageSize);
        return  pageInfo;
    }
    //定义一个方法接收分页的参数和条件查询的对象 返回分页的封装对象
    //@RequestBody 是将浏览器传输的json字符串对象转换成java对象
    @RequestMapping("search")
    public PageInfo<TbBrand> search(@RequestParam(defaultValue = "1")Integer pageNum,
                                    @RequestParam(defaultValue = "5")Integer pageSize,
                                    @RequestBody TbBrand tbBrand){
        PageInfo<TbBrand> pageInfo = brandService.search(pageNum, pageSize,tbBrand);
        return  pageInfo;
    }
    @RequestMapping("add")
    public Result add(@RequestBody TbBrand tbBrand){
        Boolean  flag = brandService.add(tbBrand);
        if (flag) {
            return new Result(true, "添加成功");
        }else {
            return new Result(true,"添加失败");
        }
    }
    @RequestMapping("update")
    public Result update(@RequestBody TbBrand tbBrand){
        Boolean  flag = brandService.update(tbBrand);
        if (flag) {
            return new Result(true, "修改成功");
        }else {
            return new Result(true,"修改失败");
        }
    }
    @RequestMapping("findOne")
    public TbBrand findOne(Long id){
        return brandService.find(id);
    }
    @RequestMapping("dele")
    public Result dele(@RequestBody Long[] ids){
        boolean flag = brandService.dele(ids);
        if (flag) {
            return new Result(true, "删除成功");
        }else {
            return new Result(true,"删除失败");
        }
    }
}
