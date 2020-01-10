app.service('indexService',function ($http) {
    this.findBannerByCategoryId= function (id) {
        return $http.get('content/findBannerByCategoryId.do?cateId='+id);
    }
})