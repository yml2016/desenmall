package com.desen.desenmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.desen.common.valid.AddGroup;
import com.desen.common.valid.ListValue;
import com.desen.common.valid.UpdateGroup;
import com.desen.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author yangminglin
 * @email 240662308@qq.com
 * @date 2021-03-21 17:56:46
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @NotNull(message = "修改必须指定Id", groups = {UpdateGroup.class})
    @Null(message = "新增不能指定Id", groups = {AddGroup.class})
    @TableId
    private Long brandId;
    /**
     * 品牌名
     */
    @NotBlank
    @NotBlank(message = "品牌名不能为空", groups = {AddGroup.class})
    private String name;
    /**
     * 品牌logo地址
     */
    @NotNull(message = "品牌logo不能为空", groups = {AddGroup.class})
    @URL(message = "logo地址必须是合法的URL地址", groups = {AddGroup.class,UpdateGroup.class})
    private String logo;
    /**
     * 介绍
     */
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    //@TableLogic//(value="1", delval = "0")
    @NotNull(groups = {AddGroup.class, UpdateStatusGroup.class})
    @ListValue(vals = {0,1}, groups = {AddGroup.class, UpdateStatusGroup.class})
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @NotNull(message = "检索首字母不能为空", groups = {AddGroup.class})
    @Pattern(regexp = "^[a-zA-Z]$", message = "必须为一个首字母", groups = {AddGroup.class,UpdateGroup.class})
    private String firstLetter;
    /**
     * 排序
     */
    @NotNull(message = "排序字段不能为空", groups = {AddGroup.class})
    @Min(value = 0, message = "排序字段必须大于等于0的整数", groups = {AddGroup.class,UpdateGroup.class})
    private Integer sort;

}
