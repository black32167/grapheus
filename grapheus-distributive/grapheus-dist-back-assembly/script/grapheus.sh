#!/bin/bash
#set -x
cd $(dirname $0)
CMD=$1

echo Executing command "$CMD"
echo JAVA_OPTS="${JAVA_OPTS}"
export SH_SQS_QUEUE_URL="${SQS_RSHSQSQUEUE_QUEUE_URL}"
export SH_REGION="${SH_REGION:-ap-southeast-2}"


case "${CMD}" in
   wait)
       #Waiting for server startup
       STATUS="Unknown"
       CNT=1
       while [[ !("${STATUS}" == "started") ]]; do
           echo "Waiting for server startup, retry $CNT, status=$STATUS..."
           STATUS=$(java ${JAVA_OPTS} -cp "./lib/*" grapheus.runner.Grapheus status)
           sleep 1

           if [[ $CNT == 100 ]]; then
               >&2 echo Error starting grapheus
               java ${JAVA_OPTS} -cp "./lib/*" grapheus.runner.Grapheus status -v
               exit 1
           fi

           CNT=$((CNT+1))
       done
       ;;
    start-bg)
        java ${JAVA_OPTS} -cp "./lib/*" grapheus.runner.Grapheus start &
        ;;
    start)
        java ${JAVA_OPTS} -cp "./lib/*" grapheus.runner.Grapheus "${CMD}"
        ;;
esac
