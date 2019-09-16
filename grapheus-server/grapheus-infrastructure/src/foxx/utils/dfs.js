const DIRECTION = require('./directions')
const graphModule = require('@arangodb/general-graph')
const VISITED = 1

/**
 * Reusable implementation of the depth-first-search.
 */
module.exports = function(params) {
    var graphId = params['graphId']
    var startVertexId = params['startVertexId']
    var edgesCollectionName = params['edgesCollectionName']
    var verticesCollectionName = params['verticesCollectionName']
    var postVisitor = params['postVisitor'] || function(){} // PostVisitor function
    var preVisitor = params['preVisitor'] || function(){} // PreVisitor function
    var isVisitedSelected = params['isVisitedSelected'] || function(){}
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
        verticesStatuses[visitingVertexId] = VISITED
        var edges = getTraversableEdges(visitingVertexId)
        var isTerminal = (edges.length == 0)
        var expand = preVisitor(visitingVertexId, isTerminal)
        if(expand) {
            var selectedEdges = []
            edges.forEach(e => {
                var dstVertexId = getDestinationVertexId(e)
                pathSelectedVia[dstVertexId] = (verticesStatuses[dstVertexId] == undefined) //
                    ? dfs(dstVertexId) //
                    : pathSelectedVia[dstVertexId] || isVisitedSelected(dstVertexId)
                if(pathSelectedVia[dstVertexId]) {
                    selectedEdges.push(e)
                }
            })
            if(selectedEdges.length > 0) {
                postVisitor(visitingVertexId, selectedEdges)
                return true;
            } else {
                return false;
            }
        } else {
            postVisitor(visitingVertexId, [])
            return true
        }

        return false
    }

    dfs(startVertexId)
}