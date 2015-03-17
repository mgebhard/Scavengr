// A REST API to access the MongoDB

Router.route('/users', function() {
    this.response.statusCode = 200;
    this.response.setHeader("Content-Type", "application/json");
    this.response.setHeader("Access-Control-Allow-Origin", "*");
    this.response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

    if(this.request.method == 'GET') {
        this.response.end(JSON.stringify(Users.find({}, { _id: 1 }).fetch().map(function(x) { return { id: x._id._str, name: x.name }})));
    } else if(this.request.method == 'POST') {
        this.response.end(JSON.stringify({ id: Users.insert(this.request.body)._str }));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "POST, GET, OPTIONS");
        this.response.end("OPTIONS Response");
    } else {
        this.response.statusCode = 405;
        this.response.end("Not Allowed");
    }
}, { where: 'server' });

Router.route('/users/byName/:name', function() {
    this.response.statusCode = 200;
    this.response.setHeader("Content-Type", "application/json");
    this.response.setHeader("Access-Control-Allow-Origin", "*");
    this.response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

    if(this.request.method == 'GET') {
        this.response.end(JSON.stringify(Users.find({ name: this.params.name }).map(function(x) {
            return { id: x._id._str };
        })));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "GET, OPTIONS");
        this.response.end("OPTIONS Response");
    } else {
        this.response.statusCode = 405;
        this.response.end("Not Allowed");
    }
}, { where: 'server' });

Router.route('/users/:userId', function() {
    this.response.statusCode = 200;
    this.response.setHeader("Content-Type", "application/json");
    this.response.setHeader("Access-Control-Allow-Origin", "*");
    this.response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

    if(this.request.method == 'GET') {
        this.response.end(JSON.stringify(Users.findOne({ _id: new Meteor.Collection.ObjectID(this.params.userId) })));
    } else if(this.request.method == 'PUT') {
        this.response.end(JSON.stringify(Users.update({ _id: new Meteor.Collection.ObjectID(this.params.userId) },
                { $set: this.request.body })));
    } else if(this.request.method == 'DELETE') {
        this.response.end(JSON.stringify(Users.remove({ _id: new Meteor.Collection.ObjectID(this.params.userId) })));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "PUT, GET, DELETE, OPTIONS");
        this.response.end("OPTIONS Response");
    } else {
        this.responseCode = 405;
        this.response.end("Not Allowed");
    }
}, { where: 'server' });

Router.route('/hunts', function() {
    this.response.statusCode = 200;
    this.response.setHeader("Content-Type", "application/json");
    this.response.setHeader("Access-Control-Allow-Origin", "*");
    this.response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

    if(this.request.method == 'GET') {
        this.response.end(JSON.stringify(Hunts.find({}, { _id: 1 }).fetch().map(function(x) { return { id: x._id._str }})));
    } else if(this.request.method == 'POST') {
        this.response.end(JSON.stringify(Hunts.insert(this.request.body)));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "POST, GET, OPTIONS");
        this.response.end("OPTIONS Response");
    } else {
        this.responseCode = 405;
        this.response.end("Not Allowed");
    }
}, { where: 'server' });

Router.route('/hunts/:huntId', function() {
    this.response.statusCode = 200;
    this.response.setHeader("Content-Type", "application/json");
    this.response.setHeader("Access-Control-Allow-Origin", "*");
    this.response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

    if(this.request.method == 'GET') {
        this.response.end(JSON.stringify(Hunts.findOne({ _id: new Meteor.Collection.ObjectID(this.params.huntId) })));
    } else if(this.request.method == 'PUT') {
        this.response.end(JSON.stringify(Hunts.update({ _id: new Meteor.Collection.ObjectID(this.params.huntId) },
                { $set: this.request.body })));
    } else if(this.request.method == 'DELETE') {
        this.response.end(JSON.stringify(Hunts.remove({ _id: new Meteor.Collection.ObjectID(this.params.huntId) })));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "PUT, GET, DELETE, OPTIONS");
        this.response.end("OPTIONS Response");
    } else {
        this.response.statusCode = 405;
        this.response.end("Not Allowed");
    }

}, { where: 'server' });

objectMerge = function(obj1, obj2) {
    var obj3 = {};
    for(var attrname in obj1) { obj3[attrname] = obj1[attrname]; }
    for(var attrname in obj2) { obj3[attrname] = obj2[attrname]; }
    return obj3;
}

Router.route('/hunts/:huntId/tasks/', function() {
    this.response.statusCode = 200;
    this.response.setHeader("Content-Type", "application/json");
    this.response.setHeader("Access-Control-Allow-Origin", "*");
    this.response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

    if(this.request.method == 'GET') {
        this.response.end(JSON.stringify(Hunts.findOne({ _id: new Meteor.Collection.ObjectID(this.params.huntId) })['tasks']))
    } else if(this.request.method == 'DELETE') {
        this.response.end(JSON.stringify(
            Hunts.update({ _id: new Meteor.Collection.ObjectID(this.params.huntId) },
                { $pull: {
                    tasks: {}
                }})));
    } else if(this.request.method == 'POST') {
        this.response.end(JSON.stringify(Hunts.update({ _id: new Meteor.Collection.ObjectID(this.params.huntId) },
                    { $push: {
                        tasks: objectMerge({ _id: new Meteor.Collection.ObjectID() }, this.request.body)
                    }})));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "POST, GET, DELETE, OPTIONS");
        this.response.end("OPTIONS Response");
    } else {
        this.response.statusCode = 405;
        this.response.end("Not Allowed");
    }
}, { where: 'server' });

Router.route('/hunts/:huntId/tasks/:taskId/', function() {
    this.response.statusCode = 200;
    this.response.setHeader("Content-Type", "application/json");
    this.response.setHeader("Access-Control-Allow-Origin", "*");
    this.response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

    var taskId = this.params.taskId;

    if(this.request.method == 'GET') {
        this.response.end(JSON.stringify(
                Hunts.findOne({
                    _id: new Meteor.Collection.ObjectID(this.params.huntId),
                    'tasks._id': new Meteor.Collection.ObjectID(this.params.taskId)
                })['tasks'].filter(function(task) {
                    return task._id._str == taskId;
                })[0]
        ));
    } else if(this.request.method == 'DELETE') {
        this.response.end(JSON.stringify(
            Hunts.update({ _id: new Meteor.Collection.ObjectID(this.params.huntId) },
                { $pull: {
                    tasks: { _id: new Meteor.Collection.ObjectID(this.params.taskId) }
                }})
        ));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "GET, DELETE, OPTIONS");
        this.response.end("OPTIONS Response");
    } else {
        this.response.statusCode = 405;
        this.response.end("Not Allowed");
    }
}, { where: 'server' });

Router.route('/hunts/:huntId/reviews/', function() {
    this.response.statusCode = 200;
    this.response.setHeader("Content-Type", "application/json");
    this.response.setHeader("Access-Control-Allow-Origin", "*");
    this.response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

    if(this.request.method == 'GET') {
        this.response.end(JSON.stringify(Hunts.findOne({ _id: new Meteor.Collection.ObjectID(this.params.huntId) })['reviews']))
    } else if(this.request.method == 'DELETE') {
        this.response.end(JSON.stringify(
            Hunts.update({ _id: new Meteor.Collection.ObjectID(this.params.huntId) },
                { $pull: {
                    reviews: { id: this.request.body.id }
                }})));
    } else if(this.request.method == 'POST') {
        this.response.end(JSON.stringify(Hunts.update({ _id: new Meteor.Collection.ObjectID(this.params.huntId) },
                { $push: {
                    reviews: { id: this.request.body.id }
                }})));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "POST, GET, DELETE, OPTIONS");
        this.response.end("OPTIONS Response");
    } else {
        this.response.statusCode = 405;
        this.response.end("Not Allowed");
    }
}, { where: 'server' });

Router.route('/reviews/', function() {
    this.response.statusCode = 200;
    this.response.setHeader("Content-Type", "application/json");
    this.response.setHeader("Access-Control-Allow-Origin", "*");
    this.response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

    if(this.request.method == 'GET') {
        this.response.end(JSON.stringify(
            Reviews.find({}, { _id: 1 }).fetch().map(function(x) { return { id: x._id._str }; })
        ));
    } else if(this.request.method == 'POST') {
        this.response.end(JSON.stringify({ id: Reviews.insert(this.request.body)._str }));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "GET, POST, OPTIONS");
        this.response.end("OPTIONS Response");
    } else {
        this.response.statusCode = 405;
        this.response.end("Not Allowed");
    }
}, { where: 'server' });

Router.route('/reviews/byAuthor/:author', function() {
    this.response.statusCode = 200;
    this.response.setHeader("Content-Type", "application/json");
    this.response.setHeader("Access-Control-Allow-Origin", "*");
    this.response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

    if(this.request.method == 'GET') {
        this.response.end(JSON.stringify(Users.find({ author: this.params.author }).map(function(x) {
            return { id: x._id._str };
        })));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "GET, OPTIONS");
        this.response.end("OPTIONS Response");
    } else {
        this.response.statusCode = 405;
        this.response.end("Not Allowed");
    }
}, { where: 'server' });

Router.route('/reviews/:reviewId', function() {
    this.response.statusCode = 200;
    this.response.setHeader("Content-Type", "application/json");
    this.response.setHeader("Access-Control-Allow-Origin", "*");
    this.response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

    if(this.request.method == 'GET') {
        this.response.end(JSON.stringify(Reviews.findOne({ _id: new Meteor.Collection.ObjectID(this.params.reviewId) })));
    } else if(this.request.method == 'PUT') {
        this.response.end(JSON.stringify(Reviews.update({ _id: new Meteor.Collection.ObjectID(this.params.reviewId) },
                { $set: this.request.body })));
    } else if(this.request.method == 'DELETE') {
        this.response.end(JSON.stringify(Reviews.remove({ _id: new Meteor.Collection.ObjectID(this.params.reviewId) })));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "PUT, GET, DELETE, OPTIONS");
        this.response.end("OPTIONS Response");
    } else {
        this.response.statusCode = 405;
        this.response.end("Not Allowed");
    }

}, { where: 'server' });