function (params) {
	var sourceGraph = params['sourceGraph'];
	var newGraphName = params['newGraphName'];
	var startVertex = params['startVertexId'];
	var traversalDirection = params['traversalDirection'];
	
	var adb = require("@arangodb")
	
    var graphSrc = require('@arangodb/general-graph')._graph(sourceGraph)
    var ecolSrc = eval('graphSrc.E_'+sourceGraph)    
    var vcolSrc = eval('graphSrc.V_'+sourceGraph)    
    var graphDst = require('@arangodb/general-graph')._graph(newGraphName)
    var vcolSrcName = 'V_'+sourceGraph
    var vcolDstName = 'V_'+newGraphName
    var ecolDst = eval('graphDst.E_'+newGraphName)    
    var vcolDst = eval('graphDst.'+vcolDstName)

    adb.print("---- Traversing '" + sourceGraph + "'/creating '"+newGraphName + "' direction " + traversalDirection)
    var status={}

    function toKey(aId) {
    	var n = aId.indexOf("/");
    	return aId=aId.substr(n+1);
    }
	
    function dfs(aId) {
    	
        if(status[aId] != undefined) {
        	return
        }
        status[aId]=1
        
        // Create vertex
        adb.print("Processing "+aId)
        var v = vcolSrc.document(aId)
        vcolDst.save(v)
        
      
        ecolSrc.edges(aId).forEach(e=>{
        	adb.print("Processing edge '" + e._from + "' -> '" + e._to + "'")
        	var selectedEdge = (traversalDirection == "OUTBOUND") ? e._to != aId : e._from != aId
			 
            if(selectedEdge) {
            	var targetVertexId = (traversalDirection == "OUTBOUND") ? e._to : e._from
                dfs(targetVertexId)
    		    
    		    // Create  edge
    		    e._from = e._from.replace(/.*\//, vcolDstName+'/')
    	        e._to = e._to.replace(/.*\//, vcolDstName+'/')
    	        adb.print("Saving Edge '" + e._from + "' -> '" + e._to + "'")
    	        ecolDst.save(e);
    		    
            }
        })
        
        status[aId]=2
    }
    

    dfs(vcolSrcName + '/' + startVertex)

    return {
    	"success":true
    }
}
