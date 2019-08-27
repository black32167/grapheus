exports.execute = function (params) {
	const STATE_VISITING=1
	const STATE_VISITED=2

	var graphName = params['graphId']
	
    var adb = require("@arangodb")
    var agraph = require('@arangodb/general-graph')._graph(graphName)
    var ecol = eval('agraph.E_'+graphName)    
    var vcol = eval('agraph.V_'+graphName)    
    var allVertices=vcol.all()

    var order = 0
    var status = {} //1,2
    
	var cycleFound = false;
	
	function mark(vId, order) {
		vcol.update(vId, {order:order})
		adb.print("/Visited " + vId)
	}
	
    function dfs(startVertexId) {
		if(status[startVertexId] == STATE_VISITED) {
    		return
    	}

    	var vstack = []
    	vstack.push(startVertexId)
    	while(vstack.length > 0) {
    		var rootId = vstack[vstack.length-1]
    		var childrenEdges = ecol.outEdges(rootId)
    		if(status[rootId] == undefined) {
    			//adb.print("total children for vertex " + rootId + " is " + childrenEdges.length)
    		}
    		
    		status[rootId] = STATE_VISITING

			var undiscoveredChildId = childrenEdges.map(e=>e._to).find(cId=>{
				if(status[cId] == undefined) {
					return true
				}
				if(status[cId] == STATE_VISITING) {
					adb.print("[ERROR] cycle detected at " + undiscoveredChildId)
					cycleFound = true
		    	}
				return false
			})
			if(undiscoveredChildId != undefined) {
	    		adb.print("Descending " + rootId + "->" + undiscoveredChildId)
	    		vstack.push(undiscoveredChildId)
			} else {
    			mark(rootId, order++)
    			status[rootId] = STATE_VISITED
    			vstack.pop()
    		}

    	}
    	
    }
    adb.print("--------------------")
    while(allVertices.hasNext()) {
        var v = allVertices.next()
       
        dfs(v._id)
    }
    
    return {
        'cycleFound' : cycleFound
    }
    
}