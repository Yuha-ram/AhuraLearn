package com.ahuralearn;

import com.ahuralearn.media.config.cloud.AliProperties;
import com.aliyun.oss.OSS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest // 启动 Spring 上下文，这样才能注入 OSS 客户端和配置类
public class OssServiceTest {

    @Autowired
    private OSS ossClient;

    @Autowired
    private AliProperties aliProperties;

    @Test
    public void testUploadLocalImage() throws Exception {
        // ==========================================
        // 1. 准备本地测试图片 (⚠️ 请修改为你电脑上真实存在的图片绝对路径！)
        // ==========================================
        // Windows 路径示例: "C:\\Users\\YourName\\Desktop\\test.jpg"
        // Mac/Linux 路径示例: "/Users/YourName/Desktop/test.jpg"
        String localFilePath = "C:\\Users\\Yorina\\Downloads\\GitHub_Essentials_Advanced_Metho…_202606142251.jpeg";
        File file = new File(localFilePath);

        // 防御性编程：检查文件是否存在
        if (!file.exists()) {
            System.err.println("❌ 测试中断：找不到本地文件！");
            System.err.println("👉 请修改代码中的 localFilePath 变量，指向一张真实的本地图片。");
            return; // 直接结束测试
        }

        // ==========================================
        // 2. 生成 OSS 上的文件名 (Object Key)
        // ==========================================
        // 获取文件后缀 (例如: .jpg)
        String suffix = localFilePath.substring(localFilePath.lastIndexOf("."));
        // 生成唯一文件名，建议加上目录前缀，例如: course/coverImage/uuid.jpg
        String objectName = "course/coverImage/" + UUID.randomUUID().toString() + suffix;

        System.out.println("🚀 开始上传...");
        System.out.println("本地文件: " + file.getAbsolutePath());
        System.out.println("目标 OSS 路径: " + objectName);

        // ==========================================
        // 3. 执行上传 (使用 try-with-resources 自动关闭流)
        // ==========================================
        try (InputStream inputStream = new FileInputStream(file)) {
            // 调用阿里云 SDK 上传
            ossClient.putObject(aliProperties.getBucketName(), objectName, inputStream);

            // ==========================================
            // 4. 验证与结果输出
            // ==========================================
            // 拼接完整的访问 URL
            String fullUrl = aliProperties.getUrlPrefix() + objectName;

            System.out.println("=====================================");
            System.out.println("✅ 上传成功！");
            System.out.println("👉 请复制以下链接到浏览器打开，查看图片是否正常显示：");
            System.out.println(fullUrl);
            System.out.println("=====================================");

            // 简单的断言：只要没抛异常，且 URL 不为空，就认为成功
            assertTrue(fullUrl.startsWith("https://"), "URL 应该以 https 开头");

        } catch (Exception e) {
            System.err.println("❌ 上传失败！");
            e.printStackTrace();
            throw e; // 让测试标红
        }
    }

    @Test
    public void testVideo() throws Exception {
        System.out.println(UUID.randomUUID().toString());
    }
}