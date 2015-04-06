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
        Users.update({ _id: new Meteor.Collection.ObjectID(this.params.userId) },
                { $set: this.request.body });
        this.response.end(JSON.stringify({}));
    } else if(this.request.method == 'DELETE') {
        Users.remove({ _id: new Meteor.Collection.ObjectID(this.params.userId) });
        this.response.end(JSON.stringify({}));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "PUT, GET, DELETE, OPTIONS");
        this.response.end("OPTIONS Response");
    } else {
        this.responseCode = 405;
        this.response.end("Not Allowed");
    }
}, { where: 'server' });
