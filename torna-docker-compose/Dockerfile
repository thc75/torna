FROM mysql:5.7
#定义会被容器自动执行的目录
ENV AUTO_RUN_DIR /docker-entrypoint-initdb.d
#定义初始化sql文件
ENV INIT_SQL mysql.sql
#把要执行的sql文件放到/docker-entrypoint-initdb.d/目录下，容器会自动执行这个sql
COPY $INIT_SQL $AUTO_RUN_DIR
#给执行文件增加可执行权限
RUN chmod a+x $AUTO_RUN_DIR/$INIT_SQL