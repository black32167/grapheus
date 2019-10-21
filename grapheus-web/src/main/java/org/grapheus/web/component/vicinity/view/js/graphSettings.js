
var parameters = {
	navigateCallbackURL : "${navigateCallbackURL}",
	deleteEdgeURL: "${deleteEdgeURL}",
	deleteVertexURL: "${deleteVertexURL}",
	generateCollapsedGraphURL: "${generateCollapsedGraphURL}",
	filterByPropertyURL: "${filterByPropertyURL}",
	isCircleLayout: "${layout}" == "radial"
}
// 
drawGraph(parameters)
