const DIRECTION = require('./directions')
const names = require("./names")
const graphModule = require('@arangodb/general-graph')
const VISITING = 1
const VISITED = 2

const STOP_FOUND = 1
const STOP_NOTFOUND = 2
const CONTINUE_EXPAND = 3

const DIR_OUTBOUND = "OUTBOUND"
const DIR_INBOUND = "INBOUND"
const DIR_ANY = "ANY"

exports.VISITING = VISITING
exports.VISITED = VISITED

exports.CONTINUE_EXPAND = CONTINUE_EXPAND
exports.STOP_FOUND = STOP_FOUND
exports.STOP_NOTFOUND = STOP_NOTFOUND

/**
 * Reusable implementation of the depth-first-search.
 */
exports.run = function(params) {
    var graphId = params['graphId']
    var startVertexId = params['startVertexId']
    var edgesCollectionName = params['edgesCollectionName'] || names.edgesCollection(graphId)
    var verticesCollectionName = params['verticesCollectionName'] || names.edgesCollection(graphId)
    var postVisitor = params['postVisitor'] || function(){} // PostVisitor function
    var preVisitor = params['preVisitor'] || function(){ return CONTINUE_EXPAND } // PreVisitor function
    var isSelected = params['isSelected'] || function(){}
    var direction = params['direction'] || DIR_OUTBOUND
    var verticesStatuses={}
    var pathSelectedVia={}

    var graph = graphModule._graph(graphId)
    var ecol = eval(`graph.${edgesCollectionName}`)
    var vcol = eval(`graph.${verticesCollectionName}`)

    function getTraversableEdges(vertexId) {
        if(direction === DIR_OUTBOUND) {
            return ecol.outEdges(vertexId)
        } else if(direction === DIR_INBOUND) {
            return ecol.inEdges(vertexId)
        }
        throw "Unknown direction:" + direction
    }
    function getDestinationVertexId(edge) {
        if(direction === DIR_OUTBOUND) {
            return edge._to
        } else if(direction === DIR_INBOUND) {
            return edge._from
        }
        throw "Unknown direction:" + direction
    }
    function dfs(visitingVertexId) {
        console.log("direction = " + direction)
        verticesStatuses[visitingVertexId] = VISITING
        var edges = getTraversableEdges(visitingVertexId)
        var isTerminal = (edges.length == 0)
        var preVisitDecision = preVisitor(visitingVertexId, isTerminal)
        var selectedPath = false
        var selectedEdges = []
        if(preVisitDecision === CONTINUE_EXPAND) {
            console.log("Expanding " + visitingVertexId + ", edges=" + edges.length)
            edges.forEach(e => {
                var dstVertexId = getDestinationVertexId(e)
                pathSelectedVia[dstVertexId] = (verticesStatuses[dstVertexId] == undefined) //
                    ? dfs(dstVertexId) //
                    : pathSelectedVia[dstVertexId] || isSelected(dstVertexId, verticesStatuses[dstVertexId])
                if(pathSelectedVia[dstVertexId]) {
                    selectedEdges.push(e)
                }
            })
            selectedPath = selectedEdges.length > 0
        } else {
            selectedPath = preVisitDecision === STOP_FOUND
        }

        postVisitor(visitingVertexId, selectedEdges, selectedPath)

        verticesStatuses[visitingVertexId] = VISITED
        return selectedPath
    }

    dfs(startVertexId)
}