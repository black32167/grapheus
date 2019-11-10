const dfs = require("../utils/dfs")
const names = require("../utils/names")

exports.execute = function (params) {
    var graphId = params['graphId']
    var agraph = require('@arangodb/general-graph')._graph(graphId)
    var vcol = eval('agraph.'+names.verticesCollection(graphId))
    var allVertices=vcol.all()

    var visited={}
    var cycles = []

    var path = []
    
    while(allVertices.hasNext()) {
        var a = allVertices.next()

        console.log("running dfs for " + a._id)
        dfs.run({
          "graphId" : graphId,
          "startVertexId" : a._id,
          "edgesCollectionName" : names.edgesCollection(graphId),
          "verticesCollectionName" : names.verticesCollection(graphId),
          "preVisitor" : (visitingVertexId, isTerminal) => {
                if (visited[visitingVertexId]) {
                    return dfs.STOP_FOUND
                }
                console.log(">visiting " + visitingVertexId)
                path.push(visitingVertexId)
                return dfs.CONTINUE_EXPAND
            },
          "isSelected" : (vertexId, status) => {
                if(status == dfs.VISITING) {
                    var cycle = []
                    var i
                    for(i = path.length-1; i >= 0; i--) {
                        cycle.push(path[i])
                        if(path[i] == vertexId) {
                            break;
                        }
                    }
                    cycles.push(cycle)
                    console.log("Found cycle via " + vertexId + ":" + cycle);
                }
                return status == dfs.VISITING
          },
          "postVisitor" : (vId) => {
              visited[vId] = true
              console.log("Popping up");
              path.pop()
          }
        })
    }
    return {
        'cycles' : cycles
    }
}