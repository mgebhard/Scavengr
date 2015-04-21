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
        this.response.end(JSON.stringify(Reviews.insert(this.request.body)));
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
        Reviews.update({ _id: new Meteor.Collection.ObjectID(this.params.reviewId) },
                { $set: this.request.body });
        this.response.end(JSON.stringify({}));
    } else if(this.request.method == 'DELETE') {
        Reviews.remove({ _id: new Meteor.Collection.ObjectID(this.params.reviewId) });
        this.response.end(JSON.stringify({}));
    } else if(this.request.method == 'OPTIONS') {
        this.response.setHeader('Access-Control-Allow-Methods', "PUT, GET, DELETE, OPTIONS");
        this.response.end("OPTIONS Response");
    } else {
        this.response.statusCode = 405;
        this.response.end("Not Allowed");
    }

}, { where: 'server' });
