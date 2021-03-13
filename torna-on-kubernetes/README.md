torna在k8s上的部署操作
# 环境要求
- docker 17+
- kubernetes 1.13.4+

# 部署操作
官方的模板主要是跑在k8s 1.17+的版本上，不同版本的k8s之间可能存在资源对象API的差异。因此官方的模板仅供参考。
实际部署过程中还需要根据自己的k8s版本来实际调整。参考模板如下：
```
# Example usage: kubectl apply -f <this_file>

# ------------------- config -------------------- #
apiVersion: v1
kind: ConfigMap
metadata:
  name: torna-config
  namespace: default
  labels:
    k8s-app: torna
data:
  application.properties: |-
    server.port=7700
    mysql.host=192.168.130.1:3306
    mysql.username=root
    mysql.password=root
---
# --------------------------- Service ---------------------- #
apiVersion: v1
kind: Service
metadata:
  name: torna-svc
  namespace: default
spec:
 type: NodePort
 ports:
 - port: 7700
   targetPort: 7700
   nodePort: 30011
 selector:
  app: torna
  tier: backend
  version: 1.0.0
---
# --------------------------- Deployment ------------------- #
apiVersion: apps/v1
kind: Deployment
metadata:
  name: torna-deployment
  namespace: default
spec:
  selector:
    matchLabels:
      app: torna
      tier: backend
      version: 1.0.0
  replicas: 1
  template:
    metadata:
      labels:
        app: torna
        tier: backend
        version: 1.0.0
    spec:
      containers:
        - name: torna
          image: tanghc2020/torna:latest
          imagePullPolicy: Always
          env:
              - name:  JAVA_OPTS
              # Override the jvm param setting in dockerfile
                value: "-server -Xmx512m -Xms512m -Djava.awt.headless=true"
          volumeMounts:
          - name: torna-config
            mountPath: /torna/config/application.properties
            readOnly: true
            subPath: application.properties
          ports:
            - containerPort: 7700
      terminationGracePeriodSeconds: 30
      volumes:
      - name: torna-config
        configMap:
          defaultMode: 0600
          name: torna-config
      imagePullSecrets:
      - name: harbor-key
---
```
主要是修改模本中的数据库链接信息，当然这个模板中我们通过k8s service的`NodePort`方式暴露了`30011`。如果你不想通过这种方式的话，
也可以通过`ingress`暴露，不过由于torna没有独特的上下文，因为采用ingress暴露的话可能需要配置一个域名。
还是根据自己的实际情况来处理。
# 访问
如果你采用官方默认的模板来部署，则部署完成后访问地址为: http://{node server}:30011
>登录账号相关信息直接查看项目的顶层readme文档。

# 官方模板下载
```
wget https://gitee.com/durcframework/torna/raw/master/torna-on-kubernetes/torna-deployment.yaml
```
