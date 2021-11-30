# neu-community

#### 介绍
基于 SpringBoot 实现的 NEU 社区。

#### 软件架构
* Spring Boot 2.4.4
* MyBatis 2.0.1
* Spring 5.3.5
* Spring MVC 5.3.5
* Spring Security 5.3.0.4.RELEASE
* Redis 3.2.100
* Caffeine 2.8.8
* Kafka 2.13-2.8.0
* ElasticSearch 7.9.3
* Quartz 2.4.4
* Tomcat 9

#### 部署教程
以下部署步骤都是在 CentOS 7 上进行。

1.  首先在待部署主机上安装好所有的环境；

2.  在项目根路径下将项目打包：
    ```shell
    mvn clean package -Dmaven.test.skip=true
    ```
    打包完成会在项目的 `target` 目录下生成 `ROOT.war` 文件；

3.  将生成的文件移动到 Tomcat 目录下的  `webapp` 目录下，然后删除该目录下的 `ROOT` 文件夹；

4.  启动 Kafka。

    进入 kafka 的根目录下。首先启动 zookeeper：
    ```shell
    bin/zookeeper-server-start.sh -daemon config/zookeeper.properties
    ```
    然后启动 kafka：
    ```shell
    nohup bin/kafka-server-start.sh config/server.properties 1>/dev/null 2>&1 &
    ```
5.  启动 ElasticSearch。
 
    由于 ElasticSearch 不能以 root 用户运行，所以需要添加一个新用户：
    ```shell
    groupadd es
    useradd es1 -p 123456 -g es
    chown -R es1:es ./elasticsearch/*
    chown -R es1:es /tmp/*
    ```
    然后进入 ElasticSearch 的 `bin` 目录运行 ElasticSearch：
    ```shell
    ./elasticsearch -d
    ```
    
6.  启动 Tomcat：
    
    进入 Tomcat 的 `bin` 目录，运行启动脚本：
    ```shell
    ./startup.sh
    ```
