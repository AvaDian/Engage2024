package org.example;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

class DownloadTask implements Runnable {
    private String fileUrl;
    private String savePath;
    private int startByte;
    private int endByte;
    private CountDownLatch latch;

    public DownloadTask(String fileUrl, String savePath, int startByte, int endByte, CountDownLatch latch) {
        this.fileUrl = fileUrl;
        this.savePath = savePath;
        this.startByte = startByte;
        this.endByte = endByte;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {


            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);

            byte[] buffer = new byte[1024];
            int bytesRead;

            try (RandomAccessFile file = new RandomAccessFile(savePath, "rw")) {
                file.seek(startByte);

                while ((bytesRead = connection.getInputStream().read(buffer)) != -1) {
                    file.write(buffer, 0, bytesRead);
                }
            }

            connection.disconnect();
            latch.countDown();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
