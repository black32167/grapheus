FROM openjdk:8-jre-slim

ARG RX_EXTRA_JAVA_OPTS
# Copy server
COPY back-dist /opt/grapheus-server/

# Config for server listening URL
ENV grapheus_base_uri_key="http://0.0.0.0:8080/grapheus"

# Config for embedded CLI
ENV JAVA_OPTS="-Dgrapheus.server.baseURL=http://127.0.0.1:8080/grapheus ${RX_EXTRA_JAVA_OPTS}"

EXPOSE 8080

CMD ["/opt/grapheus-server/grapheus.sh", "start"]