Smart Sample for Eclipse

运行环境

1. 数据库：MySQL 5.5/MariaDB 10.0
2. Web 服务器：Tomcat 7.0

使用方法

1. 修改 src/main/resources/config.properties 文件，指定数据库相关信息。
2. 在 MySQL 中创建数据库。
3. 执行 doc/sample.sql 脚本。
4. 配置 Tomcat，并运行该应用。
5. 访问应用：http://[host]:[port]/smart-sample/