package com.offcn.user.controller;
import java.sql.SQLOutput;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.offcn.utils.PhoneFormatCheckUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbUser;
import com.offcn.user.service.UserService;
import com.github.pagehelper.PageInfo;
import com.offcn.pojo.entity.Result;
/**
 * 用户表controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageInfo  findPage(int pageNum,int pageSize){
		return userService.findPage(pageNum, pageSize);
	}
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user, String code){
		System.out.println(JSON.toJSONString(user));
		try {
			//通过客户端传递的验证对比缓存中的验证码
			if(userService.checkCode(user.getPhone(),code)){
				//用户注册时间
				user.setCreated(new Date());
				user.setUpdated(new Date());
				//密码加密 BCrypt MD5加密方式
				user.setPassword(DigestUtils.md5Hex(user.getPassword()));
				System.out.println(JSON.toJSONString(user));
				userService.add(user);
				return new Result(true, "增加成功");
			}else{
				return new Result(false, "验证码失败");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}

	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param user
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/search")
	public PageInfo search(@RequestBody TbUser user, int pageNum, int pageSize  ){
		return userService.findPage(user, pageNum, pageSize);
	}
	@RequestMapping("sendCode")
	public Result sendCode(String phone){
		//验证手机号是否合法
		if (PhoneFormatCheckUtils.isChinaPhoneLegal(phone)){
			userService.sendCode(phone);
			return new Result(true,"验证码已经发送");
		}else{
			return  new Result(false,"手机号格式不正确");
		}
	}
	
}
