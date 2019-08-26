
exports.execute = function (params) {
   //console.log('targetVerticesIds');
    var graphId = params['graphId']
    var newGraphId = params['newGraphId']
    var boundaryVerticesIdsSpec = params['boundaryVerticesIds']
    var boundaryVerticesIds = boundaryVerticesIdsSpec.split(',')

    //var console = require('console')
    var adb = require("@arangodb")
    var db = adb.db
    console.info("graphId = " + graphId)
    console.info("boundaryVerticesIds=" + boundaryVerticesIds)

	//var console = require('console')
	var adb = require("@arangodb")
	var db = adb.db
	// TODO:console.error("Msg")


	var graphModule = require('@arangodb/general-graph')
    var graph = graphModule._graph(graphId)
    var ecol_name = 'E_'+graphId
    var ecol = eval('graph.'+ecol_name)
    var vcol_name = 'V_'+graphId
    var vcol = eval('graph.'+vcol_name)

    var new_graph = graphModule._graph(newGraphId)

    var new_ecol = eval('new_graph.E_'+newGraphId)
    var new_vcol = eval('new_graph.V_'+newGraphId)
    var new_vcol_name = 'V_'+newGraphId
    var new_ecol_name = 'E_'+newGraphId
    adb.print("=== Starting path search ... ");
	//console.debug('console log', '?!?!?')
    function copyVertex(targetVertexId) {
	    try {
	        adb.print("\tcloning "+targetVertexId);
	        var vertexDocument = vcol.document(targetVertexId)
	        new_vcol.save(vertexDocument);
	    }  catch (e) {
	        adb.print("[ERROR] error copying vertex '" + targetVertexId + "':" + e.message)
	    }
	}


    function dfs(rootVertexId, vertexId, visited) {
    	adb.print("visiting "+vertexId+"/"+visited[vertexId]);
	    if(visited[vertexId] != undefined) {
	    	adb.print("already visited "+vertexId);
	        return visited[vertexId] == 2 || boundaryVerticesIds.length == 1
	    }

	    var outEdges =  ecol.outEdges(vertexId)
	    var shortVertexId = vertexId.replace(/.*\//,'')

	    if((boundaryVerticesIds.length > 1 && boundaryVerticesIds.includes(shortVertexId))
	    		|| (boundaryVerticesIds.length == 1 && outEdges.length == 0)) {
	    	adb.print("Terminal vertex: "+vertexId+"/" + visited.length);
	        visited[vertexId] = 2
	        // Copy vertex
	        copyVertex(vertexId)
	        if(rootVertexId != vertexId) {
	        	return true;
	        }
	    } else {
	    	visited[vertexId] = 1
	    }

	    outEdges.forEach(e=>{
	    	var dstVertexId = e._to
	        adb.print("go  "+ dstVertexId + " from " + vertexId);

	        if(dfs(rootVertexId, dstVertexId, visited)) {
	        	if(visited[vertexId] != 2) {
		        	visited[vertexId] = 2
	                // Copy vertex #vertexId to new_graph
                	copyVertex(vertexId)
	        	}

	            adb.print("saving edge " + e._from + "->" + e._to);
	            // Connect vertexId->e._to in graph new_graph
	            e._from = e._from.replace(/.*\//, new_vcol_name+'/')
	            e._to = e._to.replace(/.*\//, new_vcol_name+'/')
	            try {
	            	new_ecol.save(e);
	            } catch (e) {
	            	adb.print("[Error] error saving edge " + e._from + "->" + e._to + ":" + e.message);
	            }
	        }
	    })

	    adb.print("/Visited vertex: "+vertexId);


	    return visited[vertexId] == 2
	}



	try {
		boundaryVerticesIds.forEach(vId=>{
			var rootvId = vcol_name + '/' + vId
			var visited = {}
			adb.print(">>> starting search from " + rootvId);
	        dfs(rootvId, rootvId, visited)
		})
	} catch (e) {
		adb.print("[Error] " + e.stack);
	}

	adb.print("Done! ");
    return true
}