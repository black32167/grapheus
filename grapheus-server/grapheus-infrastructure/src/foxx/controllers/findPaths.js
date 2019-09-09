const dfs = require("../utils/dfs")

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
    console.info("newGraphId = " + newGraphId)
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
    var copiedVertices = {}

    adb.print("=== Starting path search ... ");

	//console.debug('console log', '?!?!?')
    function copyVertex(targetVertexId) {
        if(copiedVertices[targetVertexId]) {
            return
        }
        copiedVertices[targetVertexId] = true

	    try {
	        console.log("\tcloning "+targetVertexId);
	        var vertexDocument = vcol.document(targetVertexId)
	        new_vcol.save(vertexDocument);
	    }  catch (e) {
	        console.log("[ERROR] error copying vertex '" + targetVertexId + "':" + e.message)
	    }
	}
/*
    function dfs(rootVertexId, vertexId, visited) {
    	adb.print("visiting "+vertexId+"/"+visited[vertexId]);
	    if(visited[vertexId] != undefined) {
	    	adb.print("already visited "+vertexId);
	        return visited[vertexId] == 2 || boundaryVerticesIds.length == 1
	    }

	    var outEdges =  ecol.outEdges(vertexId)
	    var shortVertexId = vertexId.replace(/.*\//,'')

        // Visit
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

        // Post-visit
	    adb.print("/Visited vertex: "+vertexId);

	    return visited[vertexId] == 2
	}
*/
    function preVisitor(vertexId, rootVertexId, isTerminal) {

        //console.log("Visiting "+ vertexId + ", terminal=" + isTerminal)
	    var shortVertexId = vertexId.replace(/.*\//,'')

        // Visit
	    if((boundaryVerticesIds.length > 1 && boundaryVerticesIds.includes(shortVertexId))
	    		|| (boundaryVerticesIds.length == 1 && isTerminal)) {
	    	console.log("Terminal vertex: "+vertexId);

	        // Copy vertex
	       // copyVertex(vertexId)
	        if(rootVertexId != vertexId) {
	        	return false; //Not expand
	        }
	    }
	    return true // Expand
    }

    function postVisitor(vertexId, selectedEdges) {
        if(selectedEdges.length > 0) {
            copyVertex(vertexId)

            selectedEdges.forEach(e => {
                // Connect vertexId->e._to in graph new_graph
                e._from = e._from.replace(/.*\//, new_vcol_name+'/')
                e._to = e._to.replace(/.*\//, new_vcol_name+'/')
                console.log("Saving edge " + e._from + "->" + e._to);
                try {
                    new_ecol.save(e);
                } catch (e) {
                    adb.print("[Error] error saving edge " + e._from + "->" + e._to + ":" + e.message);
                }
            })
        }
    }

    boundaryVerticesIds.forEach(vId=>{
        console.log(">>> starting search from " + vId);
        dfs({
          "graphId" : graphId,
          "startVertexId" : vcol_name + '/' + vId,
          "edgesCollectionName" : ecol_name,
          "verticesCollectionName" : vcol_name,
          "preVisitor" : (visitingVertexId, isTerminal) => {
            return preVisitor(visitingVertexId, vcol_name + '/' + vId, isTerminal)
          },
          "postVisitor" : postVisitor
        })
    })

	adb.print("Done! ");
    return true
}