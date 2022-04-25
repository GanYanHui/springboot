package com.gyh.springboot.controller;


import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyh.springboot.common.Constants;
import com.gyh.springboot.mapper.ImageMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.net.URLEncoder;
import java.sql.Blob;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gyh.springboot.common.Result;

import com.gyh.springboot.service.IImageService;
import com.gyh.springboot.entity.Image;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 甘延辉
 * @since 2022-04-23
 */
@RestController
@RequestMapping("/image")
public class ImageController {

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Value("${server.ip}")
    private String serverIp;

    @Resource
    private ImageMapper imageMapper;

    @Resource
    private IImageService imageService;

    //新增或者更新
    @PostMapping
    public Result save(@RequestBody Image image){
        return Result.success(imageService.saveOrUpdate(image));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        imageService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        imageService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(imageService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(imageService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        QueryWrapper<Image> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", name);
        queryWrapper.orderByDesc("id");
        return Result.success(imageService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file) throws IOException {

        String originalFilename = file.getOriginalFilename();//得到文件名
        String type = FileUtil.extName(originalFilename);//得到文件后缀名
        long size = file.getSize();

        try{
            //存储到数据库
            Image saveImage = new Image();
            saveImage.setName(originalFilename);
            saveImage.setType(type);
            saveImage.setPresize(size/1024);
            saveImage.setNextsize(size/1024);

            //将图片压缩为字节流，再加密
            Blob blob = new SerialBlob(file.getBytes());
            saveImage.setImg(blob);

            return Result.success(imageMapper.insert(saveImage));
        }catch (Exception e){
            return Result.error(Constants.CODE_500, e.toString());
        }

    }


    /**
     * 图片下载接口  http://localhost:9090/image/download/{id}
     * @param id
     * @param response
     * @throws IOException
     */
    @GetMapping("/download/{id}")
    public void download(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        //根据图片id获取文件
        Image image = imageService.getById(id);

        //设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(image.getName() ,"UTF-8"));
        response.setContentType("application/octet-stream");

        //读取文件的字节流
        os.write((byte[])image.getImg());
        os.flush();
        os.close();
    }


}

