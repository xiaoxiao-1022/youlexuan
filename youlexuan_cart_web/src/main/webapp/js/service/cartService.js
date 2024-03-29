//用户表服务层
app.service('cartService',function ($http) {
    this.findCartList = function () {
        return $http.get('../cart/findCartList.do');
    }
    this.addItemToCartList = function (itemId,num) {
        return $http.get('../cart/addItemToCartList.do?itemId='+itemId+"&num="+num);
    }
    this.findAddressByLoginUser = function () {
        return $http.get('../address/findAddressByLoginUser.do')
    }
    this.submitOrder=function (order) {
        return $http.post('../order/submitOrder.do',order);
    }
})