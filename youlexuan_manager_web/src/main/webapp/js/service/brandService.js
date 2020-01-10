//定义品牌使用的controller
app.service('brandService',function($http){

    //定义一个分页查询的js函数
    this.search=function(pageNum,pageSize,entity){
        return  $http.post('../brand/search.do?pageNum='+pageNum+"&pageSize="+pageSize,entity);
    }
    //定义一个add添加对象
    this.add=function(entity){
        return $http.post('../brand/add.do',entity);
    }
    //定义一个update添加对象
    this.update=function(entity){
        return $http.post('../brand/update.do',entity);
    }
    //根据id得到后台的数据
    this.findOne=function(id){
        return $http.get('../brand/findOne.do?id='+id);
    }
    //定义根据id数组删除对象
    this.dele=function(ids){
        if(ids.length>0){
            return $http.post('../brand/dele.do',ids);
        }else{
            alert("请选择需要删除的数据..");
        }
    }
    this.findBrandOptions=function(){
        return $http.get('../brand/findBrandOptions.do');
    }
})
