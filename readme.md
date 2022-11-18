# Torna

English | [简体中文](readme_CN.md)

Enterprise API document solution, the goal is to make the document management become more convenient, fast. Torna takes a collaborative approach to managing and maintaining project API documents, incorporating different forms of documents into a unified maintenance approach.

Torna makes up for the shortcomings of traditional document generation tools such as Swagger by enriching and enhancing some useful functionality while maintaining the original functionality.

<img src="./front/public/static/images/arc2.png" width="80%" height="80%" />


## Usage

### Method 1:download zip

- Prepare

  - Java8+
  - MySQL,need 5.6.5+,before 5.6.5:[Support low version for MySQL](http://torna.cn/dev/mysql-lower-version.html)

Go [Release page](https://github.com/torna-group/torna/releases) ,download latest version,unzip

Import database,execute [mysql.sql](./mysql.sql)

Open`application.properties`,modify database config

Run`sh startup.sh` to startup.(Windows run `startup.bat`)

Visit:`http://ip:7700`

- Login account:

username:`admin`，password:`123456`

- About upgrade

Override `torna.jar` file and `dist` folder,and startup.

### Method 2: run in docker

Import database,run [mysql.sql](./mysql.sql)

Download image

`docker pull tanghc2020/torna:1.18.1`

Create an empty file `application.properties` to store config：

`mkdir /etc/torna && touch /etc/torna/application.properties`

`vim /etc/torna/application.properties`

entry config below:

```properties
# server port
server.port=7700

# MySQL address
mysql.host=<mysql_ip>:3306
# Database name
mysql.schema=torna
# MySQL account, make sure can run DDL
mysql.username=<username>
mysql.password=<password>
```

Run docker：

```
docker run --name torna --restart=always \
  -p 7700:7700 \
  -e JAVA_OPTS="-server -Xms512m -Xmx512m" \
  -v /etc/torna/application.properties:/torna/config/application.properties \
  -d tanghc2020/torna:1.18.1
```

Browser visit:`http://<ip>:7700`

### docker-compose deploy
[docker-compose deploy](https://gitee.com/durcframework/torna/tree/master/torna-docker-compose)

### kubernetes deploy
[kubernetes deploy](https://gitee.com/durcframework/torna/tree/master/torna-on-kubernetes)

## Recommended combination

**smart-doc + Torna**

If you use Java,we recommended you use `smart-doc + Torna`

[smart-doc](https://github.com/smart-doc-group/smart-doc) + Torna form an industry-leading document generation and management solution, using smart-Doc non-intrusive Java source code and annotation extraction to generate API documents, automatically push documents to Torna enterprise interface document management platform.
Through this combination you can achieve: only need to write Java annotation can push the API information to the Torna platform, so as to achieve interface preview, interface debugging.

Push content:`API name/author/path param/header/body/response/dict/error code`

If you use other language, you can use the form page to edit the above content, and then you can also preview and debug the API.

## Development and deployment

See:[Development document](http://torna.cn/dev/)

## Other resource

- [torna-example](https://gitee.com/durcframework/torna-example) ,show usage of swagger-plugin

## Changelog

[changelog](./changelog.md)


## Page preview

![API management](./front/public/static/images/table.png "table.png")

![Edit API](./front/public/static/images/edit.png "edit.png")

![Preview](./front/public/static/images/view.png "view.png")

![Debug API](./front/public/static/images/debug.png "debug.png")

## Acknowledgements
Thanks to [JetBrains SoftWare](https://www.jetbrains.com) for providing free Open Source license for this open source project.
<img src="https://raw.githubusercontent.com/shalousun/smart-doc/dev/images/jetbrains-variant-3.png" width="260px" height="220px"/>