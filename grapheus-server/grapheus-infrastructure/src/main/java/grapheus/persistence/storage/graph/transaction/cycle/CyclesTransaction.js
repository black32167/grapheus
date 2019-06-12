function (graphName) {
    var adb = require("@arangodb")
    var agraph = require('@arangodb/general-graph')._graph(graphName)
    var ecol = eval('agraph.E_'+graphName)    
    var vcol = eval('agraph.V_'+graphName)    
    var allVertices=vcol.all()

    var status={}
  
    var count=0
    var cycles = []
    
    function toKey(aId) {
    	var n = aId.indexOf("/");
    	return aId=aId.substr(n+1);
    }
    function dfs(aId, path) {
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
        
    }
    
    while(allVertices.hasNext()) {
        var a = allVertices.next()
        dfs(a._id, [])
    }
    return cycles
    
}