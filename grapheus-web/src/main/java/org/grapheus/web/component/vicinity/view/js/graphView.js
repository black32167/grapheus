var VRAD = 10; 
var MARGIN = 10; 
var UNSELECTED_PATH_COLOR = '#ccc'
var SELECTED_PATH_COLOR = 'red'
function toValidId(artifactId) {
    if(typeof artifactId == "undefined") {
    	return undefined;
    }
	return artifactId.replace(/.*:/i, '');
}
function handleResize() {
    cy.resize()
    cy.fit()
}
function cutRight(text, maxLen) {
	if(text.length > maxLen) {
		return '...' + text.substring(text.length-maxLen, text.length)
	}
	return text
}
function drawGraph(parameters) {
	var rootId = toValidId($(".rootVertex").attr("vertexId"));
	if (typeof rootId == "undefined") {
		return;
	}

	var vertices = $("#vertices").children();
	
	var knownVIds = [];
	var nodesElements = []
	vertices.each(function() {
		var jV = $(this)
		var originalVertexId = jV.attr('vertexId');
		var vertexId = toValidId(originalVertexId);
		nodesElements.push({data:{
				id:toValidId(vertexId),
				name:cutRight(jV.attr('name'), 30),
				color:(rootId == vertexId ? 'red' : 'gray'),
				originalId:originalVertexId
			}})

		knownVIds.push(vertexId)
	});
	var knownEdges = [];
	var edges = $("#edges").children();
	var edgesElements = []
	edges.each(function() {
		var jV = $(this)
		var from = toValidId(jV.attr('from'));
		var to = toValidId(jV.attr('to'));
		var edgeKey = from+":"+to;
		if(knownVIds.includes(from) && knownVIds.includes(to) && !knownEdges.includes(edgeKey)) {
			edgesElements.push({data:{source:from,target:to}})
			knownEdges.push(edgeKey)
			
		}
	});
	
	var elements = {
			nodes: nodesElements,
			edges: edgesElements
	}
	
	var cy = window.cy = cytoscape({

		  container: document.getElementById('graphCanvas1'),
		  boxSelectionEnabled: false,
		  autounselectify: true,
		 // zoom:0.1,
		 // panningEnabled: false,
		  wheelSensitivity:0.2,
		  elements: elements,
		  pixelRatio:2,
	
          style: [ // the stylesheet for the graph
              {
                  selector: 'node',
                  style: {
                	  'font-size':10,
            	      'height': 10,
            	      'width': 10,
                      'background-color': 'data(color)',
                      'label': 'data(name)'
                  }
              },

              {
	              selector: 'edge',
	              style: {

		              'width': 0.8,

		              'curve-style': 'bezier',
		              'line-color': UNSELECTED_PATH_COLOR,
		              'target-arrow-color': UNSELECTED_PATH_COLOR,
		              'target-arrow-shape': 'vee',
	            	  'arrow-scale':1.5
	              }
              }
            ],

            layout: {
            	name: 'breadthfirst',
          	    circle : parameters.isCircleLayout, 
              // directed: true,
                roots: "#"+rootId,
                padding: 10,
                avoidOverlap:true,
                spacingFactor:0.5,
                nodeDimensionsIncludeLabels:true,
                fit:true

            }

		});

	setupGraphListeners(parameters)
	setupMenu(cy, parameters)
	
	
}

function setupMenu(cy, parameters) {
	// Menu:
	var options = {
		    // List of initial menu items
		    menuItems: [
		    	{
		            id: 'deleteEdge',
		            content: 'Delete',
		            tooltipText: 'Delete edge',
		            selector: 'edge',
		            onClickFunction: function (event) {
		            	var target = event.target || event.cyTarget;
		            	var data = target.data()
		            	Wicket.Ajax.get({ u: parameters.deleteEdgeURL+'&sourceId=' + data.source+"&targetId="+data.target });
		            },
		            disabled: false
		        },
		        {
		            id: 'deleteVertex',
		            content: 'Delete',
		            tooltipText: 'Delete vertex',
		            selector: 'node',
		            onClickFunction: function (event) {
		            	var target = event.target || event.cyTarget;
		            	Wicket.Ajax.get({ u: parameters.deleteVertexURL+'&vertexId=' + target.data().originalId });
		            },
		            disabled: false
		        }
	    	]
	}
    var instance = window.cy.contextMenus( options );
}

function goToNode(node, callbackUrl) {
	var nodeId = node.attr('originalId');
	
	Wicket.Ajax.get({ u: callbackUrl+'&targetVertextId=' + nodeId });
}

function joinNodes(mergingVId, mergingToVId, callbackUrl) {
	console.log('Merging ' + mergingVId + '->' + mergingToVId)
	Wicket.Ajax.get({ u: callbackUrl+'&mergingVId='+mergingVId+'&mergingToVId='+mergingToVId });
}
function findCycle(rootNode, pathColor) {
	if(rootNode == undefined) {
		return
	}
	var status = {}
	var node = cy.$('#'+rootNode.id());
	var rootNodeId = rootNode.id()
	var nodeId = toValidId(rootNode.attr('originalId'));
	var cycle = {}
	function dfs(node) {
		if(status[node.id()] == 2) {
			return false
		}
		//if(status[node.id()] == 1) {
		if(status[node.id()] == 1) {
			return (rootNodeId == node.id())
	
		}
		status[node.id()] = 1
		
		var edges = node.connectedEdges()
		
		var isCycle = false
		edges.forEach(function(edge) {
			var toNode = edge.target()
			if(toNode.id() != node.id()) {
				var _isCycle = dfs(toNode)
		
				if(_isCycle) {
					isCycle = true
					console.log('Vertex of ' + toNode.id() + ' is part of the cycle')
					edge.style({'line-color':pathColor,'target-arrow-color':pathColor})
				}
			}
		})
		
		status[node.id()] = 2
		return isCycle
	}
	
	dfs(rootNode)

}

function setupGraphListeners(settings) {
	cy.on('mousedown', 'node', function(evt) {
		cy.downstart = Date.now()
	});
	cy.on('free', 'node', function(evt) {
		var draggedNodePos = evt.target.position()
		console.log("dropped "+evt.target.id()+ " to " +		draggedNodePos.x)
		var rootId = toValidId($(".rootVertex").attr("vertexId"))
		cy.nodes("#"+rootId).forEach(ele => {
			if(ele.id() != evt.target.id()) {
				var pos = ele.position()
				var dist = Math.sqrt(
						Math.abs(draggedNodePos.x*draggedNodePos.x-pos.x*pos.x) +
						Math.abs(draggedNodePos.y*draggedNodePos.y-pos.y*pos.y))
						
				if(dist < 100) {
//					console.log("merging "+ele.attr('originalId')+"-> "+evt.target.attr('originalId'))
//					joinNodes(evt.target.attr('originalId'), ele.attr('originalId'), settings.mergingCallbackURL)
				}
			}
		})
	})
	cy.on('mouseup', 'node', function(evt) {
		var downduration = Date.now() - cy.downstart
		if (downduration > 500) {

			findCycle(cy.lastCycleNode, UNSELECTED_PATH_COLOR)
			cy.lastCycleNode = this
			findCycle(this, SELECTED_PATH_COLOR)
		} else {
			goToNode(this, settings.navigateCallbackURL)
		}
	});
}

$(window).load(() => {
    window.onLayoutResizeCallbacks.push(function() {
        handleResize()

        console.log("layout resized!");
        
    })
})

$(window).resize(() => {

    setTimeout(()=> {
        handleResize()
    }, 500);

	//console.log("width=" + $('#graphCanvas1').parent().width());
	console.log("resized!");


})


