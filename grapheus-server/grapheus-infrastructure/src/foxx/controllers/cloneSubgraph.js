const dfs = require("../utils/dfs")
const names = require("../utils/names")

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

    var status={}

    function toKey(aId) {
    	var n = aId.indexOf("/");
    	return aId=aId.substr(n+1);
    }

    function postVisitor(vertexId, selectedEdges) {
      console.log('selected edges=' + selectedEdges.length)
      selectedEdges.forEach(e => {
        // Connect vertexId->e._to in graph new_graph
        e._from = e._from.replace(/.*\//, vcolDstName+'/')
        e._to = e._to.replace(/.*\//, vcolDstName+'/')
        console.log("Saving edge " + e._from + "->" + e._to);
        try {
            ecolDst.save(e);
        } catch (e) {
            console.log("[Error] error saving edge " + e._from + "->" + e._to + ":" + e.message);
        }
      })
    }

    function preVisitor(visitingVertexId, isTerminal) {
        console.log("Processing "+visitingVertexId)
        var v = vcolSrc.document(visitingVertexId)
        vcolDst.save(v)
        return isTerminal ? dfs.STOP_FOUND : dfs.CONTINUE_EXPAND
    }


    dfs.run({
      "graphId" : sourceGraphId,
      "startVertexId" : names.vertexId(sourceGraphId, startVertex),
      "direction" : traversalDirection,
      "preVisitor" : preVisitor,
      "postVisitor" : postVisitor
    })

    return {
    	"success":true
    }
}
