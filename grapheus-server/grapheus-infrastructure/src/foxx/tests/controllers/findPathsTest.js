
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

    pathFinder.execute({
          "graphId" : 'srcGraph',
          "newGraphId" : "dstGraph",
          "boundaryVerticesIds" : 'v1'
    })

    var newGraph = graph_module._graph('dstGraph')
    console.log("dstColl="+newGraph.V_dstGraph.count())
    expect(newGraph.V_dstGraph.count()).gt(0)
  });

})


