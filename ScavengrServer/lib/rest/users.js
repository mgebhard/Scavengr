// A simple REST API to access the MongoDB

Router.route('/users', function() {
    this.response.statusCode = 200;
    this.response.setHeader("Content-Type", "application/json");
    this.response.setHeader("Access-Control-Allow-Origin", "*");
    this.response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

    if(this.request.method == 'POST') {
        this.response.end(JSON.stringify(Users.insert(this.request.body)));
    } else if(this.request.method == 'DELETE') {
        this.response.end(JSON.stringify(Users.remove(this.request.body)));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "POST, DELETE, OPTIONS");
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
        this.response.end(JSON.stringify(Users.findOne({ name: this.params.name })));
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
                { $set: {
                    name: this.request.body.name,
                    email: this.request.body.email
                }})));
        this.response.end("UPDATE Response");
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
        this.response.end(JSON.stringify(Hunts.find().fetch()));
    } else if(this.request.method == 'POST') {
        this.response.end(JSON.stringify(Hunts.insert(this.request.body)));
    } else if(this.request.method == 'DELETE') {
        this.response.end(JSON.stringify(Hunts.remove(this.request.body)));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "POST, GET, DELETE, OPTIONS");
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
                { $set: {
                    name: this.request.body.name,
                }})));
        this.response.end("UPDATE Response");
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
                        tasks: {
                            _id: new Meteor.Collection.ObjectID(),
                            latitude: this.request.body.latitude,
                            longitude: this.request.body.longitude,
                        }
                    }})));
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

    if(this.request.method == 'GET') {
        this.response.end(JSON.stringify(
                Hunts.findOne({
                    _id: new Meteor.Collection.ObjectID(this.params.huntId),
                    'tasks._id': new Meteor.Collection.ObjectID(this.params.taskId)
                })
        ));
    } else {
        this.response.statusCode = 405;
        this.response.end("Not Allowed");
    }
}, { where: 'server' });

