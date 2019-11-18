
var parameters = {
	navigateCallbackURL : "${navigateCallbackURL}",
	deleteEdgeURL: "${deleteEdgeURL}",
	deleteVertexURL: "${deleteVertexURL}",
	generateCollapsedGraphURL: "${generateCollapsedGraphURL}",
	isCircleLayout: "${layout}" == "radial",
	sourceGraphURL: "${sourceGraphURL}"
}
// 
drawGraph(parameters)
