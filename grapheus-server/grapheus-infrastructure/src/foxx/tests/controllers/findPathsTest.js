
"use strict";
const { expect } = require("chai")

const create = require("../test-create-utils")

const pathFinder = require("../../controllers/findPaths")
const db = require("@arangodb").db
const graph_module = require("@arangodb/general-graph")
const assert = require("assert");

describe("a test suite", () => {

  it("Find path", () => {
    create.graph({
      graphId : 'srcGraph',
      vColl   : 'V_srcGraph',
      eColl   : 'E_srcGraph'
    })
    .edge('v1', 'v2')
    .edge('v2', 'v3')
    .edge('v1', 'v3')

    create.graph({
      graphId : 'dstGraph',
      vColl   : 'V_dstGraph',
      eColl   : 'E_dstGraph'
    })

    var visitedVertices = []
    pathFinder.execute({
          "graphId" : 'srcGraph',
          "newGraphId" : "dstGraph",
          "boundaryVerticesIds" : 'v1',
          "preVisitor" : (visitingVertexId) => { visitedVertices.push(visitingVertexId) }
    })

    var newGraph = graph_module._graph('dstGraph')
    console.log("dstColl="+newGraph.V_dstGraph.count())
    expect(newGraph.V_dstGraph.count()).gt(0) //
  //  expect(newGraph.V_srcGraph.size()).eq(3) //
//        .include("vertices/v1") //
//        .include("vertices/v2") //
//        .include("vertices/v3")
  });

})


