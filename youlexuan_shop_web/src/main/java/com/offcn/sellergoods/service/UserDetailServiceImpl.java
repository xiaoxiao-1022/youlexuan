package com.offcn.sellergoods.service;

import com.offcn.pojo.TbSeller;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService {

    private SellerService sellerService;
    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }



    @Override
    public UserDetails loadUserByUsername(String sellerId) throws UsernameNotFoundException {
        List<GrantedAuthority> authorities =
                new ArrayList<GrantedAuthority>();
        //创建一个权限的对象 存储进去
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //查询数据库的商家对象
        TbSeller seller = sellerService.findOne(sellerId);
        System.out.println("========");
        System.out.println(seller.getStatus());
        if(!seller.getStatus().equals("1")){
            return null;
        }
        return new User(seller.getSellerId(),seller.getPassword(),authorities);
    }

}
