'use strict';

const createRouter = require('@arangodb/foxx/router');
const router = createRouter();

module.context.use(router);

router.get('/find-paths', getRequestProcessor('./findPaths'))
router.get('/merge-vertices', getRequestProcessor('./mergeVertices'))

function getRequestProcessor(controllerPath) {
    var controller = require(controllerPath)
    return (req, res) => {
        var result = controller.execute(req.queryParams)
        res.send(result)
    }
}
