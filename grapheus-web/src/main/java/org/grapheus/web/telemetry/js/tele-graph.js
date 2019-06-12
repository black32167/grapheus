
$(function() {
	$('.server').each(function() {
		var server = $(this)
		$(this).find('.chart').each(function() {
			var graphContainer = $(this)
			var graphIds = graphContainer.attr('graphs')
			var chartId = graphContainer.attr('id')
			var chartGroupTitle = graphContainer.text()
			var divider = graphContainer.attr('divider')
			var chartOptions = {
	                animationEnabled: false,
	                title:{
	                        text: chartGroupTitle
	                },
	                legend:{
	                        cursor: "pointer",
	                        fontSize: 8,
	                },
	                toolTip:{
	                        shared: true
	                },
	                data:[]
	        }
			graphIds.split(",").forEach(function(graphId) {
				var chartData = {
	                    name: graphId,
	                    type: "line",
	                    dataPoints: []
	                }
				chartOptions.data.push(chartData)
				server.find('.telemetry-item').each(function() {
					var value = parseFloat((parseInt($( this ).find('.' + graphId).text())/divider).toFixed(2))
					var time = new Date(parseInt($( this ).find('.timestamp').text()))
					
					chartData.dataPoints.push({y:value, x:time})
				})
			})
			graphContainer.CanvasJSChart(chartOptions);

		})
	})
	
})
