const dfs = require("../utils/dfs")
const names = require("../utils/names")

exports.execute = function (params) {
	var sourceGraphId = params['sourceGraphId'];
	var newGraphId = params['newGraphId'];
	var groupingProperty = params['groupingProperty'];

    var sourceGraph = require('@arangodb/general-graph')._graph(sourceGraphId)
    var vcolSrc = eval('sourceGraph.'+names.verticesCollection(sourceGraphId))
    var ecolSrc = eval('sourceGraph.'+names.edgesCollection(sourceGraphId))

    var dstGraph = require('@arangodb/general-graph')._graph(newGraphId)
    var vcolDst = eval('dstGraph.'+names.verticesCollection(newGraphId))
    var ecolDst = eval('dstGraph.'+names.edgesCollection(newGraphId))

    function getVertexPropertyValue(vertex, propertyName) {
        var feature = vertex.semanticFeatures[propertyName]
        return (feature === undefined) ? "undefined" : feature.value;
    }

    // Create vertices
    var srcVertices = vcolSrc.all()
    while(srcVertices.hasNext()) {
        var vertex = srcVertices.next();

        var propertyValue = getVertexPropertyValue(vertex, groupingProperty)
        var mergedVKey = propertyValue //TODO: escape
        var mergedTitle = propertyValue

        var maybeDstVertex = vcolDst.firstExample({_key:mergedVKey})
        console.log('iterating ' + vertex._id + '/maybeDstVertex='+maybeDstVertex + "/propertyValue="+propertyValue)
        if(maybeDstVertex === null) {
            vcolDst.insert({
                _key: mergedVKey,
                title: mergedTitle,
                generativeValue: propertyValue
            });
        }
    }

    // Create edges
    var srcEdges = ecolSrc.all()
    while(srcEdges.hasNext()) {
        var edge = srcEdges.next()

        var vFrom = vcolSrc.document(edge._from)
        var fromGroupingValue = getVertexPropertyValue(vFrom, groupingProperty)
        var fromDstVertexId = names.vertexId(newGraphId, fromGroupingValue)
        var vTo = vcolSrc.document(edge._to)
        var toGroupingValue = getVertexPropertyValue(vTo, groupingProperty)
        var toDstVertexId = names.vertexId(newGraphId, toGroupingValue)

        var maybeDstEdge = ecolDst.firstExample({_from:fromDstVertexId, _to:toDstVertexId})
        if(maybeDstEdge === null) {
            ecolDst.insert({
                _from:fromDstVertexId,
                _to:toDstVertexId
            })
        }
    }

	return {}
}