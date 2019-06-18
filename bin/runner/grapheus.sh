#!/bin/bash

dcompose() {
    HOST_DIR="${HOST_DIR}" docker-compose -f "${DOCKER_COMPOSE_FILE}" $@
}

DOCKER_COMPOSE_FILE="${BASH_SOURCE%/*}/docker-compose"

CMD="${1}"

echo "==== Running version ${VERSION} ===="
: ${HOST_DIR:=/tmp}
case "$CMD" in
    pull)
        dcompose pull
        ;;
    start)
        SERVICE="${2}"
        CMD_TAIL=""
        [[ "${SERVICE}" == "" ]] || CMD_TAIL="--no-deps ${SERVICE}"
        dcompose up ${CMD_TAIL}
        ;;
    stop|down)
        SERVICE="${2}"
        case "$CMD" in
            stop)
                dcompose stop "${SERVICE}"
                ;;
            down)
                dcompose down "${SERVICE}"
                ;;
        esac
        ;;
    show)
        docker ps -a | grep "grapheus_"
        ;;
    *)
        echo "Usage:"
        echo "    $0 pull"
        echo "    $0 {start|stop|down} [arangodb|grapheus-back|grapheus-web]"
        echo "    $0 show"
        exit 1;
esac

