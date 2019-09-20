const DIRECTION = require('./directions')
const graphModule = require('@arangodb/general-graph')
const VISITING = 1
const VISITED = 2

const STOP_FOUND = 1
const STOP_NOTFOUND = 2
const CONTINUE_EXPAND = 3

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
    var edgesCollectionName = params['edgesCollectionName']
    var verticesCollectionName = params['verticesCollectionName']
    var postVisitor = params['postVisitor'] || function(){} // PostVisitor function
    var preVisitor = params['preVisitor'] || function(){ return CONTINUE_EXPAND } // PreVisitor function
    var isSelected = params['isSelected'] || function(){}
    var expand = params['direction'] || 'out'
    var verticesStatuses={}
    var pathSelectedVia={}

    var graph = graphModule._graph(graphId)
    var ecol = eval(`graph.${edgesCollectionName}`)
    var vcol = eval(`graph.${verticesCollectionName}`)

    function getTraversableEdges(vertexId) {
        return ecol.outEdges(vertexId)
    }
    function getDestinationVertexId(edge) {
        return edge._to
    }
    function dfs(visitingVertexId) {
        verticesStatuses[visitingVertexId] = VISITING
        var edges = getTraversableEdges(visitingVertexId)
        var isTerminal = (edges.length == 0)
        var preVisitDecision = preVisitor(visitingVertexId, isTerminal)
        var selectedPath = false
        var selectedEdges = []
        if(preVisitDecision === CONTINUE_EXPAND) {
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

        postVisitor(visitingVertexId, selectedEdges)

        verticesStatuses[visitingVertexId] = VISITED
        return selectedPath
    }

    dfs(startVertexId)
}