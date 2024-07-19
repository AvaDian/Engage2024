# README

## 要求

允许使用AI工具，但不允许使用第三方库。语言不限，实现一个多线程下载器。下载一个文件，将其分块以多线程下载。下一个文件就行。写成命令行都行。不需要做图形界面。不需要做多任务管理。就只下载一个文件。

> 实现了一个硬编码的、无断点续传、无重复文件检测与重命名、没有考虑内存与网络瓶颈等的简陋多线程下载器。

## 使用方法

已经打包成 jar 包，存放在 out 目录下。

使用 `java -jar <jar包名称> <下载链接>` 即可使用。

例如：`java -jar SimpleDownloader.jar https://videos.aiursoft.cn/media/original/user/anduin/5552db90ed7b494b9850f918e24ba872.mmexport1678851452849.mp4`

默认下载路径为 用户/下载 文件夹中。

![img.png](fig%2Fimg.png)

