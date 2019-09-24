exports.edgesCollection = function(graphId) {
    return 'E_'+graphId
}

exports.verticesCollection = function(graphId) {
    return 'V_'+graphId
}

exports.vertexId = function(graphId, vertexKey) {
    return exports.verticesCollection(graphId) + '/' + vertexKey
}