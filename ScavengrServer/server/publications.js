


Meteor.startup(function(){

  Meteor.publish("posts", function () {
    return Posts.find();
  });

  Meteor.publish("statistics", function () {
    try{
      return Statistics.find({}, {fields: {
        'total_count': true,
        'list_count': true,
        'update_count': true,
        'delete_count': true,
        'get_count': true,
        'insert_count': true
      }});
    }catch(error){
      console.log(error);
    }
  });

  Meteor.publish("hunts", function() {
    return Hunts.find();
  });

  Meteor.publish("reviews", function() {
    return Reviews.find();
  });

  Meteor.publish("users", function() {
    return Users.find();
  });


  if((Statistics.findOne({_id: 'configuration'}) == null)) {
    Statistics.insert({
      _id: 'configuration',
      'total_count': 0,
      'list_count': 0,
      'update_count': 0,
      'delete_count': 0,
      'get_count': 0,
      'insert_count': 0,
      'is_statistics_panel_visible': true,
      'is_interface_panel_visible': true,
      'is_database_panel_visible': true
    })
  };

  if((Posts.find().count() == 0)) {
    Posts.insert({
      title: "Red",
      text: "Lorem ipsum..."
    });
    Posts.insert({
      title: "Blue",
      text: "dolar set et..."
    });
    Posts.insert({
      title: "Green",
      text: "iple fessle prax."
    });
  };

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
          description: "This is totally not a Boston Hunt",
          estimatedTime: "100",
          estimatedTimeUnit: "YEARS",
          reviews: [
            { "_id" : new Meteor.Collection.ObjectID() },
            { "_id" : new Meteor.Collection.ObjectID() },
            { "_id" : new Meteor.Collection.ObjectID() }
          ],
          tasks: [
              {
                  _id: new Meteor.Collection.ObjectID(),
                  latitude: 10,
                  longitude: 10,
                  clue: "Go to Nigeria",
                  answer: "Its Nigeria"
                  radius: 1000.0,
              },
              {
                  _id: new Meteor.Collection.ObjectID(),
                  latitude: 10,
                  longitude: 11,
                  text: "Go to Somewhere Else",
                  answer: "Its not Nigeria"
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
  };
});
