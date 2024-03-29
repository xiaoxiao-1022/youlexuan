//服务层
app.service('seckillGoodsService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../seckillGoods/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../seckillGoods/findPage.do?pageNum='+page+'&pageSize='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../seckillGoods/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../seckillGoods/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../seckillGoods/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../seckillGoods/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../seckillGoods/search.do?pageNum='+page+"&pageSize="+rows, searchEntity);
	}
	//提交订单
	this.submitOrder = function (id) {
		return $http.get('../seckillOrder/submitOrder.do?seckillId='+id)
	}
});