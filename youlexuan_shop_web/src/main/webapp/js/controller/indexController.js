//定义品牌使用的controller
app.controller('indexController',function($scope,loginService){

    //根据id得到后台的数据
    $scope.getName=function(){
        loginService.getName().success(function(response){

            $scope.username=response.name;
        })
    }

})