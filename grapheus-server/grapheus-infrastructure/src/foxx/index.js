'use strict';

const createRouter = require('@arangodb/foxx/router');
const router = createRouter();

module.context.use(router);

router.get('/find-paths', getRequestProcessor('findPaths'))
router.get('/merge-vertices', getRequestProcessor('mergeVertices'))
router.get('/find-cycles', getRequestProcessor('findCycles'))
router.get('/clone', getRequestProcessor('clone'))
router.get('/find-bridges', getRequestProcessor('findBridges'))

function getRequestProcessor(controllerPath) {
    var controller = require(`./controllers/${controllerPath}`)
    return (req, res) => {
        var result = controller.execute(req.queryParams)
        res.json(result)
    }
}
