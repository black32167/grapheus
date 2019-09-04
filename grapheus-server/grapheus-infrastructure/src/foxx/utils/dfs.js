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
    var expand = params['direction'] || 'out'
    var verticesStatuses={}

    var graph = graphModule._graph(graphId)
    var ecol = eval(`graph.${edgesCollectionName}`)
    var vcol = eval(`graph.${verticesCollectionName}`)

    function getTraversableEdges(vertexId) {
        return ecol.outEdges(vertexId)
    }
    function getNeighbors(vertexId) {
        return graph._neighbors(vertexId)
    }
    function dfs(visitingVertexId) {
        preVisitor(visitingVertexId)

        verticesStatuses[visitingVertexId] = VISITED

        getNeighbors(visitingVertexId).forEach(dstVertexId => {
            if(verticesStatuses[dstVertexId] !== VISITED) {
                dfs(dstVertexId)
            }
        })

        postVisitor(visitingVertexId)
    }

    dfs(startVertexId)
}