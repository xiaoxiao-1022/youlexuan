package com.offcn.pojo.group;

import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * @Auther: wyan
 * @Date: 2019-11-21 10:13
 * @Description:包含规格和它选项列表的包装类
 */
public class SpecificationGroup implements Serializable {

    private TbSpecification specification;

    private List<TbSpecificationOption> optionList;

    public SpecificationGroup() {
    }

    public SpecificationGroup(TbSpecification specification, List<TbSpecificationOption> optionList) {
        this.specification = specification;
        this.optionList = optionList;
    }

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<TbSpecificationOption> optionList) {
        this.optionList = optionList;
    }
}
