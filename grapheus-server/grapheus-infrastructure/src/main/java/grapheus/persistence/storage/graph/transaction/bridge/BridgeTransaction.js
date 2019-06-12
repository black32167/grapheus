function (graphName) {
    var adb = require("@arangodb")
    var agraph = require('@arangodb/general-graph')._graph(graphName)
    var ecol = eval('agraph.E_'+graphName)    
    var vcol = eval('agraph.V_'+graphName)    
    var allVertices=vcol.all()

    var visited={}
    var pre={}
    var low={}
    var count=0
    var parent={}
    var bridges = []
    
    function toKey(aId) {
    	var n = aId.indexOf("/");
    	return aId=aId.substr(n+1);
    }
    function dfs(aId) {
        if(visited[aId]) return
        visited[aId]=true
        
        count++
        pre[aId]=count
        low[aId]=count
        
        ecol.edges(aId).forEach(e=>{
            var targetVertexId=e._to != aId ? e._to : e._from
    		
            if(!visited[targetVertexId]) {
            	adb.print("tedge "+targetVertexId)
                parent[targetVertexId]=aId
                dfs(targetVertexId)
                if(low[targetVertexId] < low[aId]) {
                    low[aId] = low[targetVertexId]
                } else if(low[targetVertexId] > pre[aId] /*&& 
                        ecol.edges(targetVertexId).length > 1 &&
                        ecol.edges(aId).length > 1*/) {
                	//adb.print("Bridge!"+targetVertexId+"<->"+aId)
                    bridges.push({from:toKey(e._from), to:toKey(e._to)})
                    
                }
            } else if(parent[aId] != targetVertexId) {
                if(pre[targetVertexId] < low[aId]) {
                    low[aId] = pre[targetVertexId]
                }
            }
        })
        
        //adb.print("after "+aId+" low is "+low[aId])
        
    }
    
    while(allVertices.hasNext()) {
        var a = allVertices.next()
        dfs(a._id)
    }
    return {
        bridges:bridges
    }
    
}