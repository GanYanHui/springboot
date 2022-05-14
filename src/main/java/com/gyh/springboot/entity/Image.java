package com.gyh.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author 甘延辉
 * @since 2022-04-23
 */
@Getter
@Setter
@TableName("sys_image")
  @ApiModel(value = "Image对象", description = "")
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("ID")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("文件名称")
      private String name;

      @ApiModelProperty("文件类型")
      private String type;

      @ApiModelProperty("原文件大小(KB)")
      private Long presize;

      @ApiModelProperty("压缩后文件大小(KB)")
      private Long nextsize;

      @ApiModelProperty("加密后文件大小(KB)")
      private Long encriptsize;

      @ApiModelProperty("图片字节流")
      private Object img;

      @ApiModelProperty("文件的md5")
      private String md5;

      @ApiModelProperty("上传时间")
      private Date uploadTime;

      @ApiModelProperty("是否加密")
      private Boolean isEncript;

      @ApiModelProperty("用哪个医生的密钥加密了")
      private Integer doctorId;
}
