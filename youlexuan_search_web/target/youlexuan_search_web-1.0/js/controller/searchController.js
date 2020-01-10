app.controller('searchController',function($scope,$location,$controller,searchService){

    $controller('baseController',{$scope:$scope});

    $scope.searchMap={keyWords:'',brand:'',category:'',spec:{},price:'',pageNo:1,pageSize:30,sortField:'price',sortOrder:'ASC'};
   $scope.total=1;
   $scope.totalPage=1;
    $scope.searchItemList=function(){
        //查询分类为轮播图的广告列表
        searchService.searchItemList($scope.searchMap).success(function(response){
            $scope.itemList=response.itemList;
            $scope.total=response.total;
            $scope.totalPage=response.totalPage
            //分类集合的获取
            $scope.cateList=response.cateList;
            //品牌列表
            $scope.brandList=response.brandList;
            //规格列表
            $scope.specList=response.specList;

            //初始化分页的方法
            $scope.initPageLabel();
        })
    }

    //添加查询条件的方法
    $scope.addSearchItem=function(key,value){
        if(key=='category'||key=='brand'||key=='price'){

            $scope.searchMap[key]=value;
        }else{
            //处理规格的属性添加
            $scope.searchMap.spec[key]=value;
        }
        //调用查询方法
        $scope.searchItemList();
    }

    //移除组装的属性
    $scope.removeSearchItem=function(key){
        if(key=='category'||key=='brand'||key=='price'){

            $scope.searchMap[key]='';
        }else{
            //处理规格的属性移除 通过angular js的delete 标签可以移除对象的属性
            delete $scope.searchMap.spec[key];
        }
        //调用查询方法
        $scope.searchItemList();
    }
    //初始化分页控件
    $scope.initPageLabel = function () {
        //存储页码数字的数组
        $scope.pageList= [];
        //定义两个变量作为起始页码和结束页码
        var firstPage = 1;
        var lastPage = $scope.totalPage;
        //如果lastPage大于五的情况
        if(lastPage>5){
            if($scope.searchMap.pageNo<3){
                lastPage = 5;
            }else if($scope.searchMap.pageNo>lastPage-2){
                firstPage = lastPage -4;
            }else{
                firstPage = $scope.searchMap.pageNo-2;
                lastPage = $scope.searchMap.pageNo+2
            }
        }
        //如果lastPage小于五,数组直接存储
        for(var i=firstPage;i<lastPage;i++){
            $scope.pageList.push(i);
        }
    }
    //跳转和上下页实现
    $scope.doSearch=function (pageNo) {
        //传进来的当前页码
        $scope.searchMap.pageNo = pageNo;
        //提交查询
        $scope.searchItemList();
    }
    //跳转和上下页实现
    $scope.doToPage=function (pageNo) {
        //将字符串转换成int
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo)
      if ($scope.searchMap.pageNo<1){
          $scope.searchMap.pageNo=1;
      }
        if ($scope.searchMap.pageNo>$scope.totalPage){
            $scope.searchMap.pageNo=$scope.totalPage;
        }
        //提交查询
        $scope.searchItemList();
    }
    //排序查询的触发事件
    $scope.doSort = function (field,sortOrder) {
        $scope.searchMap.sortOrder=sortOrder;
        $scope.searchMap.sortField = field;
        //提交查询
        $scope.searchItemList();
    }
    //定义一个方法拥有判断品牌的包含结果
    $scope.isSearchBrand=function(){
        //获取到搜索的关键字
        var searchStr =$scope.searchMap.keyWords;
        //获取所有的品牌集合 循环遍历
        var brandList = $scope.brandList;
        for(var i=0;i<brandList.length;i++){
            console.log(searchStr+"====="+brandList[i].text);
            console.log(searchStr.indexOf(brandList[i].text));
            if(searchStr.indexOf(brandList[i].text)>=0){
                console.log('返回为true');
                return true;
            }
        }
        console.log('返回为false');
        return false;
    }
    //定义一个方法接收传递的参数
    $scope.loadKeyWords = function () {
        var keyWords = $location.search()['keyWords'];
        $scope.searchMap.keyWords = keyWords;
        //提交查询
        $scope.searchItemList();
    }
})