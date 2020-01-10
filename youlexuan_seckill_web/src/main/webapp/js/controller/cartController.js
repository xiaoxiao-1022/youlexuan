 //用户表控制层 
app.controller('cartController' ,function($scope,$controller ,$location  ,cartService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    $scope.findCartList=function(){
        cartService.findCartList().success(function (response) {
			$scope.cartList=response;
            $scope.sumData($scope.cartList);
        })
	}
	//数量改变的触发事件 目的请求 addItemToCarList.do?itemId=738388&num=1
    $scope.addItemToCarList=function(itemId,num){
        cartService.addItemToCarList(itemId,num).success(function (response) {
           if(response.success){
               $scope.findCartList();
           }
        })
    }
    //定义一个方法用于得到cartList中每个对象的itemList 循环计算总金额和总价钱
    $scope.totalData={totalNum:0,totalMoney:0.00}
    $scope.sumData=function(cartList){
        for(var i=0;i<cartList.length;i++){
            var cart = cartList[i];
            var itemList = cart.itemList;
            for(var j=0;j<itemList.length;j++){
                $scope.totalData.totalMoney+=itemList[j].totalFee;
                $scope.totalData.totalNum+=itemList[j].num;
            }
        }
    }
    $scope.address={};
    //添加一个方法用于根据登陆用户获取地址列表
    $scope.findAddressByLoginUser=function () {
        cartService.findAddressByLoginUser().success(function (response) {
            $scope.addressList = response;
            for(var i=0;i<$scope.addressList.length;i++){
                if($scope.addressList[i].isDefault==1){
                    $scope.address=$scope.addressList[i];
                    break;
                }
            }
        })
    }
    //定义一个方法用于点击更改选中的地址对象
    $scope.selectAddress=function(address){
        $scope.address=address;
    }
    //判断地址是否为被选中的地址对象
    $scope.isSelectAddress=function(address){

       return $scope.address==address;
    }
    //定义一个订单对象用于保存
    $scope.order={paymentType:1};
    //触发提交订单的方法
    $scope.submitOrder=function () {
        //前端传递的数据
        $scope.order.receiverAreaName=$scope.address.address;
        $scope.order.receiverMobile=$scope.address.mobile;
        $scope.order.receiver=$scope.address.contact;
        $scope.order.sourceType ="2";
        cartService.submitOrder($scope.order).success(function (response) {
            //如果成功提交那么跳转付款页面
            if(response.success){
                location.href="pay.html";
            }else{
                alert(response.message);
            }
        })
    }

    $scope.showMoney=function(){
        $scope.money=$location.search()['money'];
    }
});	