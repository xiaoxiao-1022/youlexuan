app.service('searchService',function($http){

    this.searchItemList=function(searchMap){
        return $http.post('search/itemList.do',searchMap);
    }
})