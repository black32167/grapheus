exports.execute = function (params) {
	var sourceGraph = params['sourceGraphId'];
	var newGraphName = params['newGraphId'];
	var adb = require("@arangodb")
	
    var graphSrc = require('@arangodb/general-graph')._graph(sourceGraph)
    var ecolSrc = eval('graphSrc.E_'+sourceGraph)    
    var vcolSrc = eval('graphSrc.V_'+sourceGraph)    
    var graphDst = require('@arangodb/general-graph')._graph(newGraphName)
    var vcolDstName = 'V_'+newGraphName
    var ecolDst = eval('graphDst.E_'+newGraphName)    
    var vcolDst = eval('graphDst.'+vcolDstName)   
    
    
    adb.print("Copying '" + sourceGraph + "' to '"+newGraphName + "'")
   	var allVerticesSrc=vcolSrc.all()
   	
   	while(allVerticesSrc.hasNext()) {
        var v = allVerticesSrc.next()
        adb.print("Saving '" + v._id + "'")
   		vcolDst.save(v);
   	}
	var allEdgesSrc=ecolSrc.all()
   	while(allEdgesSrc.hasNext()) {
        var e = allEdgesSrc.next()
        e._from = e._from.replace(/.*\//, vcolDstName+'/')
        e._to = e._to.replace(/.*\//, vcolDstName+'/')
        adb.print("Saving Edge '" + e._from + "' -> '" + e._to + "'")
        ecolDst.save(e);
   	}

   	return {}
}
