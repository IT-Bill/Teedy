package com.sismics.util;

import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Test of the image utilities.
 * 
 * @author bgamard
 */
public class TestImageUtil {

    @Test
    public void computeGravatarTest() {
        Assert.assertEquals("0bc83cb571cd1c50ba6f3e8a78ef1346", ImageUtil.computeGravatar("MyEmailAddress@example.com "));
    }
    
    @Test
    public void writeJpegTest() throws IOException {
        // 创建一张带有 Alpha 通道的图片（ARGB 类型）
        int width = 100;
        int height = 100;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        // 用半透明红色填充整个图片
        g2d.setColor(new Color(255, 0, 0, 128));
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        
        // 将图片写入到 ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageUtil.writeJpeg(image, baos);
        byte[] jpegBytes = baos.toByteArray();
        
        // 验证生成的 JPEG 数据不为空
        Assert.assertTrue("生成的 JPEG 字节数组不应为空", jpegBytes.length > 0);
        
        // 通过 ImageIO 读取生成的 JPEG 图片
        ByteArrayInputStream bais = new ByteArrayInputStream(jpegBytes);
        BufferedImage outputImage = ImageIO.read(bais);
        Assert.assertNotNull("读取后的图片不应为 null", outputImage);
        
        // 验证图片尺寸正确
        Assert.assertEquals("图片宽度应为 100", width, outputImage.getWidth());
        Assert.assertEquals("图片高度应为 100", height, outputImage.getHeight());
        
        // JPEG 不支持 Alpha 通道，输出的图片应该没有 Alpha
        Assert.assertFalse("JPEG 图片不应包含 Alpha 通道", outputImage.getColorModel().hasAlpha());
    }
}
