"use strict";

const db = require("@arangodb").db
const graph_module = require("@arangodb/general-graph")

function GraphContext(graph, eCollName, vCollName) {
    this._graph = graph
    this.eColl = eval(`graph.${eCollName}`)
    this.vColl = eval(`graph.${vCollName}`)
    this.vertex = function(vertexName) {
        try {
            this.vColl.save({_key:vertexName})
        } catch(e) {
        }
        return this
    }
    this.edge = function (fromV, toV) {
        this.vertex(fromV)
        this.vertex(toV)
        this.eColl.save(`${vCollName}/${fromV}`, `${vCollName}/${toV}`, {})
        return this
    }
}
exports.graph = function(p) {
    try { graph_module._drop(p.graphId, true) } catch(e) {}

    var rel = graph_module._relation(p.eColl, p.vColl, p.vColl)
    var graph = graph_module._create(p.graphId, [rel]);
    return new GraphContext(graph, p.eColl, p.vColl)
}
