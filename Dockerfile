FROM dockerhub.yonyougov.top/public/openjdk:17.0.2-slim-buster-aliyun
WORKDIR /app
ADD entrypoint.sh /app/
COPY wchatgpt /app/wchatgpt
ENTRYPOINT ["sh","-c","/app/entrypoint.sh"]
