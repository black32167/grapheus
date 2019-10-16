exports.execute = function (params) {
	var graphId = params['graphId']
	var verticesIds = params['verticesIds'].split(',')
	var newVertexName = params['newVertexName']

	console.info("verticesIds=" + verticesIds)
	
	var adb = require("@arangodb")

    var graph = require('@arangodb/general-graph')._graph(graphId)
    var ecol = eval('graph.E_'+graphId)    
    var vcol = eval('graph.V_'+graphId)    

    var vcolName = 'V_'+graphId
   
    // Create grouping vertex
    var targetVertexId
    var targetVertex
    try {
    	targetVertex = vcol.insert({title:newVertexName, description:"", updatedTimestamp:Date.now()})
    	targetVertexId = targetVertex._id
    }  catch (e) {
        adb.print("[ERROR] error creating new grouping vertex:" + e.message)
        throw "[ERROR] error creating new grouping vertex:" + e.message
    }

    // Connecting specified vertices to created ones
    var targetDescription = ""
    
    for(var i = 0; i < verticesIds.length; i++) {
    	
        var sourceVertexId = verticesIds[i];
        adb.print("[INFO] processing vertex=" + sourceVertexId)
        var sourceVertex
        try {
            sourceVertex = vcol.document(sourceVertexId)
        }  catch (e) {
            adb.print("[ERROR] error reading document '" + sourceVertexId + "':" + e.message)
            continue
        }
        //adb.print("targetVertex title1 = "+targetVertex.title)
        
        if(typeof sourceVertex.description != undefined && sourceVertex.description != null) {
            targetDescription += '\n'+sourceVertex.description
        }

        ecol.outEdges(vcolName+'/'+sourceVertexId).forEach(e=>{
        	adb.print("saving out e= "+e._from+"->"+e._to +"/->"+(targetVertexId))
            try {
                var existingEdge = ecol.firstExample({_from:targetVertexId, _to:e._to})
        	
            	if(existingEdge != null || e._to == targetVertexId) {
            		ecol.remove(e._key)
            	} else {
            		ecol.update(e._key, {_from:targetVertexId})
            	}
        	} catch (err) {
        	    adb.print("[ERROR] Error processing outedge " + e._key + ":" + err.message)
        	}
        })
        ecol.inEdges(vcolName+'/'+sourceVertexId).forEach(e=>{
        	adb.print("saving in e= "+e._from+"->"+e._to + "/->"+(targetVertexId))
            try {
            	//adb.print("[INFO] finding edge=" + e._from + "->" + targetVertexId)
                var existingEdge = ecol.firstExample({_from:e._from, _to:targetVertexId})
                
            	if(existingEdge !=null || e._from == targetVertexId) {
            		adb.print("[INFO] removing=" + e._from)
            		ecol.remove(e._key)
            	} else {
            		//adb.print("[INFO] updating=" + e._id)
            		ecol.update(e._key, {_to:targetVertexId})
            	}
        	} catch (err) {
                adb.print("[ERROR] Error processing inedge " + e._key + ":" + err.message)
            }
        })
        
        try {
            vcol.remove(sourceVertex._key)
        } catch (e4) {
            adb.print("[ERROR4] Error removing inedge " + sourceVertex._key + ":" + e4.message)
        }
    }
    
    vcol.update(targetVertex._key, {description:targetDescription})

    return {
        'newVertexKey': targetVertex._key
    }
}