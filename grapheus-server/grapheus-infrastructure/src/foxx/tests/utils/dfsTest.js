
"use strict";
const { expect } = require("chai")

const create = require("../test-create-utils")

const dfs = require("../../utils/dfs")
const db = require("@arangodb").db
const graph_module = require("@arangodb/general-graph")
const assert = require("assert");

describe("a test suite", () => {
  var ctx = {}
  beforeEach(() => {
    ctx.graph = create.graph({
        graphId : 'graph',
        vColl   : 'vertices',
        eColl   : 'edges'
    })
  });
  afterEach(() => {
    //
  });

  it("Test transitive 3", () => {
    ctx.graph
        .edge('v1', 'v2')
        .edge('v2', 'v3')
        .edge('v1', 'v3')
    var visitedVertices = []
    dfs.run({
          "graphId" : 'graph',
          "startVertexId" : "vertices/v1",
          "edgesCollectionName" : 'edges',
          "verticesCollectionName" : 'vertices',
          "preVisitor" : (visitingVertexId) => {
               visitedVertices.push(visitingVertexId)
               return dfs.CONTINUE_EXPAND
           }
    })
    expect(visitedVertices) //
        .include("vertices/v1") //
        .include("vertices/v2") //
        .include("vertices/v3")
  });

  it("Test one", () => {
    ctx.graph.vertex('v1')
    var visitedVertices = []
    dfs.run({
          "graphId" : 'graph',
          "startVertexId" : "vertices/v1",
          "edgesCollectionName" : 'edges',
          "verticesCollectionName" : 'vertices',
          "preVisitor" : (visitingVertexId) => {
               visitedVertices.push(visitingVertexId)
               return dfs.CONTINUE_EXPAND
           }
    })
    expect(visitedVertices) //
        .include("vertices/v1")
  });
})


