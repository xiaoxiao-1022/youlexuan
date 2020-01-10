//用户服务层
app.service('payService',function ($http) {
    this.createPayUrl = function () {
        return $http.get('../pay/createPayUrl.do');
    }
    this.queryOrderStatus = function (orderNo) {
        return $http.get('../pay/queryOrderStatus.do?orderNo='+orderNo);
    }
})