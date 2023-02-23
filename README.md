# wchatgpt

  使用chatgpt和turing对接微信公众号

1. 打包命令:

```bash
 mvn package -Dmaven.test.skip=true  
```

2. 使用前请配置conf/application.yml

3.启动命令

```bash
sh start.sh
```

4、docker镜像命令

```bash
sh docker-build.sh
```

5、使用docker-compose
```yaml
version: '3.6'

services:
  data:
    image: dockerhub.yonyougov.top/public/wechatgpt:1.0-SNAPSHOT
    container_name: wchatgpt
    restart: always
    #environment:
    #   api_key: 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
    #   wechat: true
    #   wechat_keyword: tmd
    ports:
      - "18080:8080"
    volumes:
      - ./data/conf:/app/wchatgpt/conf
      - ./data/logs:/app/wchatgpt/logs
```
