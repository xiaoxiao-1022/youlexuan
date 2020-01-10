//用户控制层
app.controller('payController', function ($scope, $controller, payService) {
    $controller('baseController', {$scope: $scope});//继承
   //请求后台生成payURL
    $scope.createPayUrl= function () {
        payService.createPayUrl().success(function (response) {
            console.log(response);
            if (response!=null){
                //获取返回的三种数据
                var qrCode = response.qrCode;
                $scope.orderNo = response.orderNo;
                $scope.money = response.money;
                var qr = new QRious({
                    level :'H',
                    element: document.getElementById('qrcode'),
                    value: qrCode,
                    size:250
                });
                $scope.queryOrderStatus($scope.orderNo);
            } else{
                alert("申请交易失败");
            }
        })
    }
    $scope.queryOrderStatus =function (orderNo) {
        payService.queryOrderStatus(orderNo).success(function (response) {
            if (response.success){
                location.href = "paysuccess.html#?money="+$scope.money;
            } else{
                if (response.message=='支付超时'){
                    document.getElementById("timeout").innerHTML="二维码已过期,刷新页面重新二维码"
                }else{
                    location.href="fail.html";
                }
            }
        })
    }
})