FROM openjdk:8-jre-slim

# Copy CLI
COPY cli-dist /opt/grapheus-cli/
RUN mkdir /tmp/grapheus 

# Config for embedded CLI

ENV grapheus_server_baseURL="http://127.0.0.1:8080/grapheus"

ENTRYPOINT ["/opt/grapheus-cli/grapheus-cli.sh"]
