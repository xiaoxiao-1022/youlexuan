package com.offcn.sellergoods.controller;

import com.offcn.fdfs.FastDFSClient;
import com.offcn.pojo.entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class uploadController {
    private static String SERVER_URL="http://192.168.188.146/";
    @RequestMapping("uploadFile")
    public Result upload(MultipartFile file){
        String name = file.getOriginalFilename();
        int index = name.lastIndexOf(".");
        String ext = name.substring(index+1);
        try {
            FastDFSClient client = new FastDFSClient();
            String url= client.uploadFile(file.getBytes(),ext);
            return new Result(true,SERVER_URL+url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
