/**
 * Traverses source graph starting from specified vertex and builds corresponding subgraph in the destination graph.
 */
exports.execute = function (params) {
	var sourceGraphId = params['sourceGraph'];
	var destinationGraphId = params['newGraphName'];
	var startVertex = params['startVertexId'];
	var traversalDirection = params['traversalDirection'];
	
	var adb = require("@arangodb")
	
    var graphSrc = require('@arangodb/general-graph')._graph(sourceGraphId)
    var ecolSrc = eval('graphSrc.E_'+sourceGraphId)
    var vcolSrc = eval('graphSrc.V_'+sourceGraphId)
    var graphDst = require('@arangodb/general-graph')._graph(destinationGraphId)
    var vcolSrcName = 'V_'+sourceGraphId
    var vcolDstName = 'V_'+destinationGraphId
    var ecolDst = eval('graphDst.E_'+destinationGraphId)
    var vcolDst = eval('graphDst.'+vcolDstName)

    adb.print("---- Traversing '" + sourceGraphId + "'/creating '"+destinationGraphId + "' direction " + traversalDirection)
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
