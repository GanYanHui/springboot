package com.gyh.springboot.service.impl;

import com.gyh.springboot.entity.Image;
import com.gyh.springboot.mapper.ImageMapper;
import com.gyh.springboot.service.IImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 甘延辉
 * @since 2022-04-23
 */
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements IImageService {

}
