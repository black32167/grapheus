const dfs = require("../utils/dfs")
const names = require("../utils/names")

exports.execute = function (params) {

    var graphId = params['graphId']
    var adb = require("@arangodb")
    var agraph = require('@arangodb/general-graph')._graph(graphId)
    var ecol = eval('agraph.'+names.edgesCollection(graphId))
    var vcol = eval('agraph.'+names.verticesCollection(graphId))
    var allVertices=vcol.all()

    var status={}
    var visited={}
    var count=0
    var cycles = []
    
    function toKey(aId) {
    	var n = aId.indexOf("/");
    	return aId=aId.substr(n+1);
    }

    var path = []
    /*function dfs(aId, path) {
        if(status[aId] != undefined) return
       // adb.print("Processing "+aId + "(" + status[aId] + ")")
        status[aId]=1
        
        path.push(aId)
        
        count++
      
        ecol.edges(aId).forEach(e=>{
            if(e._to != aId) {
    		    var targetVertexId=e._to
                if(status[targetVertexId] == undefined) {
                    dfs(targetVertexId, path)
                } else if(status[targetVertexId] == 1) {
                    // Cycle
                    adb.print("Found cycle including vertex "+aId)
                    var cycle = []
                    var i
                    for(i = path.length; i >= 0; i--) {
                        cycle.push(path[i])
                        if(path[i] == targetVertexId) {
                            break;
                        }
                    }
                    cycles.push(cycle)
                    adb.print("Found cycle: "+cycle);
                }
            }
        })
        
        path.pop()
        status[aId]=2
    }*/
    
    while(allVertices.hasNext()) {
        var a = allVertices.next()
        // dfs(a._id, [])
        console.log("running dfs for " + a._id)
        dfs.run({
          "graphId" : graphId,
          "startVertexId" : a._id,
          "edgesCollectionName" : names.edgesCollection(graphId),
          "verticesCollectionName" : names.verticesCollection(graphId),
          "preVisitor" : (visitingVertexId, isTerminal) => {
                if (visited[visitingVertexId]) {
                    return false
                }
                console.log(">visiting " + visitingVertexId)
                path.push(visitingVertexId)
                return true
            },
          "isSelected" : (vertexId, status) => {

                if(status == dfs.VISITING) {

                    var cycle = []
                    var i
                    for(i = path.length; i >= 0; i--) {
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