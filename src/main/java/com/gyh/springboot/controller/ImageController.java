package com.gyh.springboot.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyh.springboot.common.Constants;
import com.gyh.springboot.entity.User;
import com.gyh.springboot.mapper.ImageMapper;
import com.gyh.springboot.service.IUserService;
import com.gyh.springboot.utils.ImageUtil;
import com.gyh.springboot.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
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

    @Resource
    private IUserService iUserService;

    //新增或者更新或加密
    @PostMapping
    public Result save(@RequestBody Image image) throws IOException {

        //1.是否被加密了，加密了无需在加密，未加密则判断2
        //2.判断传来的数据中是否包括医生id，如果有就用该医生的公钥进行加密
        Boolean isEncript = image.getIsEncript();
        Integer id = image.getId();
        Integer doctorId = image.getDoctorId();
        if(!isEncript && doctorId != null){//未被加密且传来的数据中包括医生id
            try{
                //获取图像字节数组
                byte[] datas = (byte[])imageService.getById(id).getImg();
                // 获取图像的md5
                String md5 = SecureUtil.md5().digestHex(datas);

                //根据doctorId得到该医生的公钥，对图像进行加密
                User doctor = iUserService.getById(doctorId);
                String publicKeyStr = doctor.getPublicKeyStr();
                RSAPublicKey publicKey = RSAUtil.getPublicKey(publicKeyStr);
                byte[] newDatas = RSAUtil.publicEncrypt(datas, publicKey);

                Blob blob = new SerialBlob(newDatas);
                long encriptSize = blob.length();
                image.setImg(blob);
                image.setMd5(md5);
                image.setEncriptsize(encriptSize/1024);
                image.setIsEncript(true);
                image.setDoctorId(doctorId);

            }catch (Exception e){
                return Result.error(Constants.CODE_500, e.toString());
            }
        }
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
        return Result.success(imageService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file) throws IOException {

        String originalFilename = file.getOriginalFilename();//得到文件名
        String type = FileUtil.extName(originalFilename);//得到文件后缀名
        long size = file.getSize();
        System.out.println("原文件大小size = " + size + " B = " + size/1024 + " KB");

        try{
            //存储到数据库
            Image saveImage = new Image();
            saveImage.setName(originalFilename);
            saveImage.setType(type);
            saveImage.setPresize(size/1024);
            saveImage.setIsEncript(false);

            //将图片压缩
            InputStream inputStream = file.getInputStream();
            //如果是图片文件就进行压缩
            if(ImageUtil.isImage(originalFilename)){
                BufferedImage compressedImage = ImageUtil.compress(ImageIO.read(inputStream));

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(compressedImage, type, out);

                byte[] datas = out.toByteArray();

                Blob blob = new SerialBlob(datas);
                long newSize = blob.length();
                saveImage.setImg(blob);
                saveImage.setNextsize(newSize/1024);
            }else{
                return Result.error(Constants.CODE_400, "只能上传图片");
            }

            return Result.success(imageMapper.insert(saveImage));
        }catch (Exception e){
            return Result.error(Constants.CODE_500, e.toString());
        }

    }


    /**
     * 图片下载接口  http://localhost:9090/image/download/{id}&{userId}
     * @param id
     * @param response
     * @throws IOException
     */
    @GetMapping("/download/{id}&{userId}")
    public void download(@PathVariable Integer id, @PathVariable Integer userId, HttpServletResponse response) throws IOException {
        //根据图像id获取文件
        Image image = imageService.getById(id);

        //设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(image.getName() ,"UTF-8"));
        response.setContentType("application/octet-stream");

        //获取图像字节数组
        byte[] datas = (byte[])image.getImg();
        String md5 = image.getMd5();

        try {
            //1.判断图像是否被加密，已加密再进行2，未被加密则直接下载
            //2.判断加密的doctorId与userId是否相同,相同则获取对应的私钥，不同则下载的图像无法显示
            if(image.getIsEncript()){//已加密
                Integer doctorId = image.getDoctorId();
                if(doctorId.equals(userId)){//相同
                    //获取医生的私钥解密图像
                    User doctor = iUserService.getById(doctorId);//得到医生对象
                    String privateKeyStr = doctor.getPrivateKeyStr();//获取医生的私钥字符串
                    RSAPrivateKey privateKey = RSAUtil.getPrivateKey(privateKeyStr);//根据字符串得到私钥
                    byte[] newDatas = RSAUtil.privateDecrypt(datas, privateKey);//用私钥解密图像
                    datas = newDatas;
                    String md5_a = SecureUtil.md5().digestHex(datas);

                    System.out.println("根据解密的图像得到的md5_a = " + md5_a);
                    System.out.println("---------------原md5_b = " + md5);

                    if(md5_a.equals(md5))
                        os.write(datas);
                }else{
                    //已加密，但是用别的医生的公钥加密的，提示公钥私钥不匹配，不下载
                    System.out.println(Result.error(Constants.CODE_600, "公钥私钥不匹配"));
                }
            }else{
                //未被加密则直接下载
                //读取文件的字节流
                System.out.println(Result.success());
                os.write(datas);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            os.flush();
            os.close();
        }
    }


}

