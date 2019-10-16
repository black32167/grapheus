var VRAD = 10; 
var MARGIN = 10; 
var UNSELECTED_PATH_COLOR = '#ccc'
var SELECTED_PATH_COLOR = 'red'

/**
 * Entry point, invoked when page is loaded.
 */
function drawGraph(parameters) {
	var rootId = getRootVertexId()
	if (typeof rootId == "undefined") {
		return;
	}

    var nodesElements = buildNodes($("#vertices").children())
    var knownVIds = getAllVerticesIds(nodesElements)
	var edgesElements = buildEdges($("#edges").children(), knownVIds)

	var cy = window.cy = cytoscape({

		  container: document.getElementById('graphCanvas1'),
		  boxSelectionEnabled: false,
		  autounselectify: true,
		 // zoom:0.1,
		 // panningEnabled: false,
		  wheelSensitivity:0.2,

		  elements: {
                        nodes: nodesElements,
                        edges: edgesElements
                    },

		  pixelRatio:2,

          style: [ // the stylesheet for the graph
                      {
                          selector: 'node',
                          style: {
                              'font-size':20,
                              'height': 10,
                              'width': 10,
                              //'border-width':2,
                              'border-color': 'data(border_color)',
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
                        padding: 5,
                        avoidOverlap:true,
                        spacingFactor:0.5,
                        grid:false,
                        maximal: true,
                        nodeDimensionsIncludeLabels:true,
                        fit:true
                  }

	});

	setupGraphListeners(cy, parameters)
	setupMenu(cy, parameters)

    // TODO: can it be invoked on VicinityControlPanel side when one is rendered?

    cy.ready(()=>{
        updateNodeColors($('.verticesTagsSelector').val())
        updateEdgeColors($('.edgesTagsSelector').val())

        var levels = []

        cy.elements().bfs({
            root: "#"+rootId,
            visit: function(v, e, u, i, depth) {
                if(levels.length < depth+1) {
                    levels.push([])
                }
                levels[depth].push(v)
            }
        })

        for(var d = 0; d < levels.length; d++) {
            levels[d].sort((v1, v2) => {return v1.position().x - v2.position().x})
            for(var idx = 0; idx < levels[d].length; idx++) {
                var pos = levels[d][idx].position()
                var _idx = parameters.isCircleLayout ? d : idx
                if((_idx % 2) == 0) {
                    levels[d][idx].position({x:pos.x, y:pos.y+25})
                }
            }
        }

        cy.fit()
    })

}


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

function getRootVertexId() {
    return toValidId($(".rootVertex").attr("vertexId"))
}


function getAllVerticesIds(nodesElements) {
    return nodesElements.map(e => e.data.id)
}

function buildEdges(edges, knownVIds) {
	var edgesElements = []
	var knownEdges = [];
	edges.each(function() {
		var jV = $(this)
		var from = toValidId(jV.attr('from'));
		var to = toValidId(jV.attr('to'));
		var tags = jV.attr('tags').split(",");
		var edgeKey = from+":"+to;
		if(knownVIds.includes(from) && knownVIds.includes(to) && !knownEdges.includes(edgeKey)) {
			edgesElements.push({data:{
			    source:from,
			    target:to,
			    tags:tags
            }})
			knownEdges.push(edgeKey)
		}
	});
	return edgesElements
}

function buildNodes(vertices) {
	var nodesElements = []
	var rootId = getRootVertexId()
	vertices.each(function() {
		var jV = $(this)
		var originalVertexId = jV.attr('vertexId');
		var tags = jV.attr('tags').split(",");
		var properties = jV.attr('properties').split(",");

		var vertexId = toValidId(originalVertexId);


        if(rootId == vertexId) {
            tags.push("root")
        }

		nodesElements.push({data:{
				id: toValidId(vertexId),
				name: cutRight(jV.attr('name'), 30),
				color: 'gray',
				border_color : 'gray',
				selectedVertex: (rootId == vertexId),
				tags: tags,
				properties: properties,
				originalId:originalVertexId
			}})
	});

	return nodesElements
}

/**
 * Highlights nodes marked with specified tag.
 * Part of javascript API for backend
 */
function updateNodeColors(selectedTag) {
    cy.nodes().forEach(nodeEle=> {
        var newColor = "gray"
        var newBGColor = "gray"
        var nodeData = nodeEle.data()
        if(nodeData.tags.includes(selectedTag)) {
            newColor = 'green'
        }
        nodeEle.data({
            color: newColor,
            border_color : newBGColor})
    })
}


/**
 * Highlights edges marked with specified tag.
 * Part of javascript API for backend
 */
function updateEdgeColors(selectedTag) {
    cy.edges().forEach(edgeEle=> {
        var newColor = "gray"
        var edgeData = edgeEle.data()
        if(edgeData.tags.includes(selectedTag)) {
            newColor = 'green'
        }
        edgeEle.style({'line-color':newColor,'target-arrow-color':newColor})
    })
}

function setupMenu(cy, parameters) {
	// Menu:
	var options = {
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
		        },
		        {
                    id: 'generateCollapsedGraph',
                    content: 'Collapse...',
                    tooltipText: 'Collapse by property',
                    selector: 'node',
                    onClickFunction: function (event) {
                        var target = event.target || event.cyTarget;
                        Wicket.Ajax.get({ u: parameters.generateCollapsedGraphURL+'&vertexId=' + target.data().originalId });
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
function findCycle(cy, rootNode, pathColor) {
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

function setupGraphListeners(cy, settings) {
	cy.on('mousedown', 'node', function(evt) {
		cy.downstart = Date.now()
	});
	cy.on('free', 'node', function(evt) {
		var draggedNodePos = evt.target.position()
		console.log("dropped "+evt.target.id()+ " to " + draggedNodePos.x)
		var rootId = getRootVertexId()
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
	cy.on('mouseover', 'node', function(evt) {
	    var pos = evt.target.renderedPosition()

        var tags = evt.target.data().tags
        var properties = evt.target.data().properties.join('\n')


	    console.log('Pos:'+pos.x+','+pos.y + '/' + this.tags);
	    $('#tooltip').css({top: pos.y, left: pos.x})
	    $('#tooltip').text(tags)
        $('#tooltip').show();
	})
	cy.on('mouseout', 'node', function(evt) {
        $('#tooltip').hide();
    })
	cy.on('mouseup', 'node', function(evt) {
		var downduration = Date.now() - cy.downstart
		if (downduration > 500) {

			findCycle(cy, cy.lastCycleNode, UNSELECTED_PATH_COLOR)
			cy.lastCycleNode = this
			findCycle(cy, this, SELECTED_PATH_COLOR)
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


