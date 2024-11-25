# 文件批量重命名

1. 第一种类型：将某文件夹下所有文件和目录中含有的某些字符串替换为另外对应的某些字符串

启动后直接访问地址 ip:8080/api/fileRename/rename

2. 第一种类型：将某文件夹下所有文件和目录中含有的某些字符串替换为另外对应的某些字符串

启动后直接访问地址 ip:8080/api/fileRename/renameFileByFileName

# 使用方法

在application.yml中配置

1. `ini文件位置:`iniFilePath  指向具体文件

2. `需要重命名文件夹位置:`targetFolderPath  指向文件夹

3. `日志存放位置:`logPath  指向具体文件 如没有文件会自动创建，但是文件夹需要存在

直接启动浏览器访问地址即可，后台存在输出可以查看重命名情况

# config文件说明

config.ini  文件为第二种类型的配置文件

1. oldFileName:旧文件名 与新文件名对应位置的文件名一一对应

2. newFileName:新文件名 与旧文件名对应位置的文件名一一对应

3. oldDirectoryFileName:旧文件夹名， 与新文件夹名对应位置的文件名一一对应 

4. newDirectoryFileName:新文件夹名， 与旧文件夹名对应位置的文件名一一对应

# config1文件说明

config1.ini  文件为第一种类型的配置文件

1. oldName:原文件名需要替换的字符串，与newName一一对应

2. newName:新文件名需要换进的字符串，与oldName一一对应