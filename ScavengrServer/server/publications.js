
Meteor.startup(function(){

    Meteor.publish("posts", function () {
        return Posts.find();
    });

    if(Users.find().count() == 0) {
        Users.insert({
            name: "John Smith",
            email: "jsmith@example.com",
            googleplus: "115057029322489848133",
            createdHunts: [
                new Meteor.Collection.ObjectID(),
                new Meteor.Collection.ObjectID(),
                new Meteor.Collection.ObjectID(),
            ],
        });
    };

    if(Hunts.find().count() == 0) {
        Hunts.insert({
            name: "Boston Hunt 1",
            reviews: [
                new Meteor.Collection.ObjectID(),
                new Meteor.Collection.ObjectID(),
                new Meteor.Collection.ObjectID(),
            ],
            tasks: [
                {
                    _id: new Meteor.Collection.ObjectID(),
                    latitude: 10,
                    longitude: 10,
                    text: "Go to Nigeria",
                    radius: 1000.0,
                },
                {
                    _id: new Meteor.Collection.ObjectID(),
                    latitude: 10,
                    longitude: 11,
                    text: "Go to Somewhere Else",
                    radius: 1000.0,
                },
            ],
        });
    };

    if(Reviews.find().count() == 0) {
        Reviews.insert({
            text: "This is a fantastic hunt.",
            rating: 4.5,
            author: new Meteor.Collection.ObjectID(),
        });
    }
});

