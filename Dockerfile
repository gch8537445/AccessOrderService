#FROM adoptopenjdk/openjdk11:alpine-jre
# FROM apache/skywalking-java-agent:8.16.0-java11
FROM dkimages.imtpath.net/common/openjdk11:alpine-jre
VOLUME /tmp
COPY target/order-flow-service-0.0.2-SNAPSHOT.jar order-flow-service.jar
EXPOSE 8052

# 如果使用4G堆内存，则docker run 参数传入 -e JAVA_OPTS='-server -Xmx4g -Xms4g'
# ENV JAVA_OPTS="-server -Xmx1g -Xms128m -javaagent:/skywalking/agent/skywalking-agent.jar"
ENTRYPOINT java ${JAVA_OPTS} -jar order-flow-service.jar
