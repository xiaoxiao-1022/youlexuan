app.controller('indexController',function ($scope,$controller,indexService) {

    $controller('baseController',{$scope:$scope});
    $scope.findBannerByCategoryId=function () {
        //查询分为轮播图的广告列表
        indexService.findBannerByCategoryId(1).success(function (response) {
            $scope.bannerList = response;
        })
        //c触发事件传递参数到搜索的网站页面
        $scope.keyWords='';
        $scope.doSearch = function () {
            //跳转页面
            location.href= "http://localhost:9104/search.html#?keyWords="+$scope.keyWords;
        }
    }
})