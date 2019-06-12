# About
Grapheus is a tool for _graphs exploration_. It provides _interactive navigation_ through your graphs and helps to _refine the data_ according specific criteria. 

To work with current repository, you will need:
* Java 8
* Maven 3x
* Docker
* [Docker-Compose](https://docs.docker.com/compose/)

# Start Grapheus
You can run version published on docker hub by typing

```
grapheus$ ./bin/grapheus.sh start
```

Once docker-compose starts containers, just navigate to [http://127.0.0.1:8000/app/](http://127.0.0.1:8000/app/) in your browser:
 
<img src="grapheus_screenshot.png" alt="Grapheus screenshot" width="1024px">


# Build Grapheus docker images from sources
If you made a change or just want to have latest version from master you can build everything required including docker images using the command:

```
grapheus$ ./bin/build.sh all
```



