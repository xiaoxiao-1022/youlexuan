 //控制层 
app.controller('seckillGoodsController' ,function($scope,$controller,$location,$interval   ,seckillGoodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		seckillGoodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		seckillGoodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.list;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id= $location.search()['id'];
		seckillGoodsService.findOne(id).success(
			function(response){
				$scope.entity= response;

				//获取得到结束时间对象对应的毫秒值
				//获取当前时间对象的毫秒值
				var endDate = new Date($scope.entity.endTime);

				var currentDate = new Date();
				$scope.allSecond = Math.floor((endDate.getTime()-currentDate.getTime())/1000);
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=seckillGoodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=seckillGoodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		seckillGoodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		seckillGoodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.list;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    var timer = $interval(function () {
		if($scope.allSecond > 0){
			$scope.allSecond = $scope.allSecond-1;
			$scope.formDateStr($scope.allSecond);
		}else {
			$interval.cancel(timer);
		}
	},1000)
	$scope.formDateStr = function (allSecond) {
		$scope.dataStr='';
		//通过当前的描述计算得到天
		var days= Math.floor(allSecond/(24*60*60));

		//去除的天数的秒剩余 用于得到小时
		var hours = Math.floor((allSecond-days*24*60*60)/(60*60));
		//去除天和小时剩余计算分钟
		var minutes = Math.floor((allSecond-days*24*60*60-hours*60*60)/60);
		var seconds = allSecond - days*24*60*60-hours*60*60-minutes*60;
		//拼接显示的字符串
		if(days>0){
			$scope.dataStr = $scope.dataStr+days+"天";
		}
		$scope.dataStr = $scope.dataStr+hours+":"+minutes+":"+seconds;
	}
	$scope.submitOrder = function (id) {
		seckillGoodsService.submitOrder(id).success(function (response) {
			if (response.success){
				location.href="pay.html";
			} else {
				if (response.message =='用户未登录'){
					location.href="login.html";
				}
			}
		})
	}
});	