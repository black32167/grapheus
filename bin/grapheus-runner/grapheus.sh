#!/bin/bash

dcompose() {
    HOST_DIR="${HOST_DIR}" docker-compose -f "${DOCKER_COMPOSE_FILE}" $@
}

DOCKER_COMPOSE_FILE="${BASH_SOURCE%/*}/docker-compose"

CMD="${1}"

export VERSION="${VERSION}"
echo "==== Grapheus v${VERSION} ===="
: ${HOST_DIR:=/tmp}


case "$CMD" in
    db-log)
        docker exec -it grapheus_arangodb less /var/log/arangodb3/arangod.log
        ;;
    pull)
        dcompose pull
        ;;
    start)
        SERVICE="${2}"
        CMD_TAIL=""
        [[ "${SERVICE}" == "" ]] || CMD_TAIL="--no-deps ${SERVICE}"
        dcompose up ${CMD_TAIL}
        ;;
    stop|rm)
        SERVICE="${2}"
        case "$CMD" in
            stop)
                dcompose stop "${SERVICE}"
                ;;
            rm)
                dcompose rm -fvs "${SERVICE}"
                ;;
        esac
        ;;
    down)
        dcompose down
        ;;
    show)
        docker ps -a | grep "grapheus_"
        ;;
    *)
        echo "Usage:"
        echo "    $0 pull"
        echo "    $0 down"
        echo "    $0 {start|stop|rm} [database|backend|frontend]"
        echo "    $0 show"
        echo "    $0 db-log"
        exit 1;
esac

