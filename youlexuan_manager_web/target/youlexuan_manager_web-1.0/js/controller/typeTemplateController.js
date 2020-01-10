 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller   ,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.list;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//将获取得到的entity中的brandIds 和specIds两个json串
				//转换成select2下拉需要的 json数组
				$scope.entity.brandIds = JSON.parse(response.brandIds);
				$scope.entity.specIds = JSON.parse(response.specIds);
				$scope.entity.customAttributeItems = JSON.parse(response.customAttributeItems);
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
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
		typeTemplateService.dele( $scope.selectIds ).success(
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
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.list;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    //格式转化
	$scope.jsonToStr=function(jsonStr,key){
		//将字符串转换成jsono对象
		var jsonArr = JSON.parse(jsonStr);
		var value="";
		for(var i=0;i<jsonArr.length;i++){
			if(i==jsonArr.length-1){
				value+=jsonArr[i][key];
			}else{
				value+=jsonArr[i][key]+",";
			}
		}
		return value;
	}
	//定义一个规格的数据
	$scope.specList={data:[{id:1,text:'网络'},{id:2,text:'内存'}]};
	$scope.findSpecList=function(){
		specificationService.findSpecOptions().success(function (response) {
			$scope.specList.data=response;
		})
	}
	//定义一个品牌的数据
	$scope.brandList={data:[{id:1,text:'联想'},{id:2,text:'华为'},{id:3,text:'小米'}]};
	$scope.findBrandList=function(){
		brandService.findBrandOptions().success(function (response) {
			$scope.brandList.data=response;
		})
	}
	//定义初始化两个下拉的方法
	$scope.initSelect=function(){
		$scope.findBrandList();
		$scope.findSpecList();
	}
	$scope.entity={customAttributeItems:[]};
	//添加行和删除行
	$scope.addRow=function(){
		$scope.entity.customAttributeItems.push({});
	}
	$scope.deleRow=function(index){
		$scope.entity.customAttributeItems.splice(index,1);
	}
});	