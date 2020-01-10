//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.list;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        //获取列表页面传递过来的参数
        //search方法的返回值就是一个json格式对象 id=123
        var id = $location.search()['id'];
        alert(id);
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //向富文本编辑器添加商品介绍
                editor.html($scope.entity.goodsDesc.introduction);
                //显示图片列表
                $scope.entity.goodsDesc.itemImages =
                    JSON.parse($scope.entity.goodsDesc.itemImages);
                //显示扩展属性
                $scope.entity.goodsDesc.customAttributeItems
                    = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                $scope.entity.goodsDesc.specificationItems
                    = JSON.parse($scope.entity.goodsDesc.specificationItems);
                //遍历循环entity的itemList 将每条记录的spec字符串转成jsaon数组
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    }
    //根据规格名称和选项名称返回是否被勾选
    $scope.checkAttributeValue=function(name,value){
        var items =  $scope.entity.goodsDesc.specificationItems;
        for(var i=0;i<items.length;i++){
            //根据name判断列表是否有匹配的规格名称
            var object =  $scope.searchObjectByName(items,name);
            if(object!=null){
                //判断object中的values值是否有传递参数value
                if(object.attributeValue.indexOf(value)>=0){
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }
    }

    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}, itemList: []};
    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        var introduction = editor.html();
        $scope.entity.goodsDesc.introduction = introduction;

        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.entity = {goods: {}, goodsDesc: {}}
                    editor.html('');
                    alert("添加成功....等待审核")
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }
    $scope.status = ['未审核', '已审核', '审核未通过', '关闭']
    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.list;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }
    //定义一个数组存储所有的分类
    $scope.allCategory = [];
    $scope.selectAllCategory = function () {
        itemCatService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i++) {
                $scope.allCategory[response[i].id] = response[i].name;
            }
        })
    }
    //定义一个对象用于存储上传的图片对象,颜色和URL路径
    $scope.image_entity = {};
    //上传图片到后台得到响应后的地址
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {
                $scope.image_entity.url = response.message;
            }
        })
    }
    //触发保存图片对象到goodDesc的属性集合中s
    $scope.saveImage = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    //删除上面图片数组中的对象
    $scope.deleImage = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1)
    }
    $scope.catList1 = [];
    //定义一个方法获取第一级分类的方法
    $scope.findCatList = function (parentId) {
        itemCatService.findByParentId(parentId).success(function (response) {
            $scope.catList1 = response;
        })
    }
    //定义方法监听第一层级的值是否变化
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        //执行逻辑
        if (newValue) {
            itemCatService.findByParentId(newValue).success(function (response) {
                $scope.catList2 = response;
            })
        }
    })
    //定义方法监听第二层级的值是否变化
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        //执行逻辑
        if (newValue) {
            itemCatService.findByParentId(newValue).success(function (response) {
                $scope.catList3 = response;
            })
        }
    })
    //监听第三层及id变化得到所属的分类对象
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        //执行逻辑
        if (newValue) {
            itemCatService.findOne(newValue).success(function (response) {
                $scope.catEntity = response;
            })
        }
    })
    //监视模板id变化得到模板对象
    $scope.$watch('catEntity.typeId', function (newValue, oldValue) {
        //执行逻辑
        if (newValue) {
            //调回模板的service得到findOne的对象
            typeTemplateService.findOne(newValue).success(function (response) {
                $scope.typeEntity = response;
                //通过parse将字符串json格式变成数组对象
                $scope.brandIds = JSON.parse($scope.typeEntity.brandIds);
                //将模板对象的自定义属性json串转换成数组
                if (null == $location.search()['id']) {
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeEntity.customAttributeItems);
                }
            })
            //通过newValue的模板id执行规格列表的数据初始化操作
            typeTemplateService.findSpeclist(newValue).success(function (response) {
                $scope.specList = response;
            })
        }
    })
    //定义一个方法 用于判断list中是否由规格的对象
    //   [{"attributeName":"网络","attributeValue":["移动3G"]},
    //	 {"attributeName":"机身内存","attributeValue":["16G"]}
    //	 ]
    $scope.searchObjectByName = function (list, name) {

        for (var i = 0; i < list.length; i++) {
            if (list[i].attributeName == name) {
                return list[i];
            }
        }
        return null;
    }
    //点击选项的复选框触发事件 更新goodsDesc中的属性 $scope.entity.goodsDesc.specificationItems
    $scope.updateSpecAttribute = function (event, name, value) {
        var object = $scope.searchObjectByName($scope.entity.goodsDesc.specificationItems, name);
        if (null != object) {
            //如果事件触发时候复选框状态为选中才push
            if (event.target.checked) {
                object.attributeValue.push(value);
            } else {
                //移出原始数组的value
                var index = object.attributeValue.indexOf(value);
                object.attributeValue.splice(index, 1);
                //移出后判断数组中是否为空,如果为空当前对象从list中移除掉
                if (object.attributeValue.length == 0) {
                    var index = $scope.entity.goodsDesc.specificationItems.indexOf(object);
                    $scope.entity.goodsDesc.specificationItems.splice(index, 1);
                }
            }
        } else {
            $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }
    }
    //点击触发创建sku item的列表
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0.0, num: 0, status: '0', isDefined: '0'}];
        //先获取上面组装的规格list中有几个对象
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            //将拼接后的itemList返回赋予sku列表
            $scope.entity.itemList = $scope.addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);

        }
    }
    //定义一个添加列的方法  网络 [2g,3g]
    $scope.addColumn = function (list, columnName, columnValues) {
        var newList = [];
        for (var i = 0; i < list.length; i++) {
            //得到每一行的数据
            var oldRow = list[i];
            for (var j = 0; j < columnValues.length; j++) {
                var oldRowStr = JSON.stringify(oldRow);
                var newRow = JSON.parse(oldRowStr);
                newRow.spec[columnName] = columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }
});	