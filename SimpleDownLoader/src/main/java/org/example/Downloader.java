package org.example;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Downloader {
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("请输入下载链接！");
            return;
        }
        String fileUrl = args[0];
        //String fileUrl = "https://videos.aiursoft.cn/media/original/user/anduin/5552db90ed7b494b9850f918e24ba872.mmexport1678851452849.mp4";
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        String savePath = getDefaultDownloadPath() + File.separator + fileName;

        int numThreads = 4; // 定义线程数
        int blockSize = 1024 * 1024; // 定义块大小（1MB）

        downloadFile(fileUrl, savePath, numThreads, blockSize);
    }

    public static void downloadFile(String fileUrl, String savePath, int numThreads, int blockSize) {
        try {
            System.out.println("尝试从链接下载文件: " + fileUrl);
            long startTime = System.currentTimeMillis();

            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int fileSize = connection.getContentLength();
            connection.disconnect();
            System.out.println("链接打开成功！文件保存在：" + savePath);
            int numBlocks = (int) Math.ceil((double) fileSize / blockSize);
            RandomAccessFile file = new RandomAccessFile(savePath, "rw");
            file.setLength(fileSize);
            file.close();

            CountDownLatch latch = new CountDownLatch(numBlocks);
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            for (int i = 0; i < numBlocks; i++) {
                int startByte = i * blockSize;
                int endByte = Math.min(startByte + blockSize - 1, fileSize - 1);

                executor.execute(new DownloadTask(fileUrl, savePath, startByte, endByte, latch));
            }

            latch.await(); // 等待所有块下载完成
            executor.shutdown();

            System.out.println("文件下载完成！");

            long endTime = System.currentTimeMillis();

            long elapsedTime = endTime - startTime;
            double downloadSpeed = (double) fileSize / elapsedTime * 1000 / 1024 / 1024; // 下载速度（每秒下载的MB数）

            DecimalFormat df = new DecimalFormat("#.##");
            String formattedSpeed = df.format(downloadSpeed);

            System.out.println("总耗时：" + elapsedTime / 1000.0 + " 秒");
            System.out.println("平均下载速度：" + formattedSpeed + " MB/s");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static String getDefaultDownloadPath() {
        String homeDir = System.getProperty("user.home");
        String defaultDownloadPath = homeDir + File.separator + "Downloads";
        return defaultDownloadPath;
    }


    public static String generateUniqueFilePath(String directory, String fileName) {
        File file = new File(directory, fileName);
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        int count = 1;
        while (file.exists()) {
            String uniqueFileName = baseName + "(" + count + ")" + extension;
            file = new File(directory, uniqueFileName);
            count++;
        }
        return file.getAbsolutePath();
    }
}

