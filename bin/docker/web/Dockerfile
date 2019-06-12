FROM openjdk:8-jre-slim

COPY web-dist /opt/grapheus-webapp/

ENV grapheus_base_uri_key="http://0.0.0.0:8080/grapheus"
EXPOSE 8080

CMD ["/opt/grapheus-webapp/grapheus-web.sh", "start"]