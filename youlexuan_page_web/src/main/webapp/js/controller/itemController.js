//定义品牌使用的controller
app.controller('itemController',function($scope,$http){

   //用户点击数量增加触发事件
   $scope.num=1;
   $scope.addNum=function(count){
   
		$scope.num=$scope.num+count;
		if($scope.num<1){
			$scope.num=1;
		}
		
   }
   //用户点击规格保存数据的事件方法 网络 移动3G
   $scope.specItem={};
   $scope.addItem=function(specName,specValue){
		$scope.specItem[specName]=specValue;
		$scope.isMatch();
   }
   
   //根据规格名和规格的值判断用户是否选择 如果匹配返回true
                           // 网络  移动4G 
						   // 网络  移动3G
    $scope.isSelect=function(specName,specValue){
	
	 return  $scope.specItem[specName]==specValue;
   }
   
   $scope.sku={};
   //定义一个方法用于获取sku列表的第一个json对象
   $scope.loadDefaultSku=function(){
		$scope.sku=skuList[0];
		 //避免对象引用同一个地址 采用赋值字符转换对象的方式
		 $scope.specItem=JSON.parse(JSON.stringify(skuList[0].spec));
   }
   
   //定义一个方法循环skuList 判断用户选择的规格是否匹配某一个sku 对象
   $scope.isMatch=function(){
	  for(var i=0;i<skuList.length;i++){
	    //{"网络":"联通3G","机身内存":"64G"}
		var map1 = skuList[i].spec;
		//将每个map1和 用户选择的  $scope.specItem={} map做一个对比 必须完全一致才可以
		var result1 = true;
		var result2 =true;
		for(k in map1){
			if(map1[k]!=$scope.specItem[k]){
				result1 =false;
				break;
			}
		}
		for(k in $scope.specItem){
			if(map1[k]!=$scope.specItem[k]){
				result2 =false;
				break;
			}
		}
		if(result1&&result2){
			$scope.sku=skuList[i];
		}
	  }
   
   }
   
   $scope.addItemToCartList = function () {
	   console.log($scope.sku.id+"===="+$scope.num);
	   $http.get
	   ('http://localhost:9107/cart/addItemToCartList.do'+
		   '?itemId='+$scope.sku.id+'&num='+$scope.num,{'withCredentials':true}).success(function (response) {
		   if (response.success){
		   	location.href="http://localhost:9107/cart.html";
		   }else{
		   	alert(response.message);
		   }
	   })
   }
   

})