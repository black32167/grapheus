const dfs = require("../utils/dfs")
const names = require("../utils/names")

exports.execute = function (params) {
    var graphId = params['graphId']
    var newGraphId = params['newGraphId']
    var boundaryVerticesIdsSpec = params['boundaryVerticesIds']
    var boundaryVerticesIds = boundaryVerticesIdsSpec.split(',')

    var adb = require("@arangodb")
    var db = adb.db
    console.info("graphId = " + graphId)
    console.info("newGraphId = " + newGraphId)
    console.info("boundaryVerticesIds=" + boundaryVerticesIds)

	var adb = require("@arangodb")
	var db = adb.db

	var graphModule = require('@arangodb/general-graph')
    var graph = graphModule._graph(graphId)
    var ecol_name = names.edgesCollection(graphId)
    var ecol = eval('graph.'+ecol_name)
    var vcol_name = names.verticesCollection(graphId)
    var vcol = eval('graph.'+vcol_name)

    var new_graph = graphModule._graph(newGraphId)

    var new_ecol = eval('new_graph.E_'+newGraphId)
    var new_vcol = eval('new_graph.V_'+newGraphId)
    var new_vcol_name = 'V_'+newGraphId
    var new_ecol_name = 'E_'+newGraphId
    var copiedVertices = {}

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
    function preVisitor(vertexId, rootVertexId, isTerminal) {

        //console.log("Visiting "+ vertexId + ", terminal=" + isTerminal)
	    var shortVertexId = vertexId.replace(/.*\//,'')

        // Visit
	    if((boundaryVerticesIds.length > 1 && boundaryVerticesIds.includes(shortVertexId))
	    		|| (boundaryVerticesIds.length == 1 && isTerminal)) {
	        if(rootVertexId != vertexId) {
	            console.log("Fonud terminal vertex: "+vertexId);
	        	return dfs.STOP_FOUND; //Not expand
	        }
	    }
	    return dfs.CONTINUE_EXPAND // Expand
    }

    function postVisitor(vertexId, selectedEdges, selectedPath) {
        if(selectedPath) {
            copyVertex(vertexId)
            selectedEdges.forEach(e => {
                // Connect vertexId->e._to in graph new_graph
                e._from = e._from.replace(/.*\//, new_vcol_name+'/')
                e._to = e._to.replace(/.*\//, new_vcol_name+'/')
                console.log("Saving edge " + e._from + "->" + e._to);
                try {
                    new_ecol.save(e);
                } catch (e) {
                    console.log("[Error] error saving edge " + e._from + "->" + e._to + ":" + e.message);
                }
            })
        }
    }

    boundaryVerticesIds.forEach(vId=>{
        console.log(">>> starting search from " + vId);
        dfs.run({
          "graphId" : graphId,
          "startVertexId" : vcol_name + '/' + vId,
          "edgesCollectionName" : ecol_name,
          "verticesCollectionName" : vcol_name,
          "preVisitor" : (visitingVertexId, isTerminal) => {
            return preVisitor(visitingVertexId, vcol_name + '/' + vId, isTerminal)
          },
          "postVisitor" : postVisitor,
          "isSelected" : (vertexId) => {
            return boundaryVerticesIds.length == 1
          }
        })
    })

    return true
}