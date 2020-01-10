app.controller('brandController',function($scope,brandService,$controller,$http) {
    $controller('baseController',{$scope:$scope})

    //定义一个分页查询的js函数
    $scope.search = function(pageNum, pageSize, entity) {
        brandService.search(pageNum,pageSize, entity).success(function (response) {
            //将后台返回的json数据赋予给list集合
            $scope.list = response.list;
            $scope.paginationConf.totalItems = response.total;
        })
    }
//定义一个对象用于搜索列表
    $scope.searchEntity = {};
//定义一个add添加对象
    $scope.add = function () {
        //判断当前scope中的entity是否存在id
        if (null != $scope.entity.id) {
            brandService.update($scope.entity).success(function (response) {
                if (response.success) {
                    //如果返回的属性success为true
                    alert(response.message);
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            })
        }else{
           brandService.add($scope.entity).success(function (response) {
               if (response.success) {
                   //如果返回的属性success为true
                   alert(response.message);
                   $scope.reloadList();
               } else {
                   alert(response.message);
               }
           })
        }

    }
//根据id得到后台的数据
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        })
    }
//定义根据id数组删除对象
    $scope.dele = function () {
        if ($scope.selectIds.length > 0) {
            brandService.dele($scope.selectIds).success(function (response) {
                $scope.selectIds = [];
                $scope.reloadList();
            })
        } else {
            alert("请选择需要删除的数据..");
        }

    }
})