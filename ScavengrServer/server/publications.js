


Meteor.startup(function(){

  Meteor.publish("posts", function () {
    return Posts.find();
  });

  if((Users.find().count() == 0)) {
    Users.insert({
      name: "John Smith",
      email: "jsmith@example.com",
      googleplus: "115057029322489848133",
      createdHunts: [0, 1, 2],
    });
  };

  if((Hunts.find().count() == 0)) {
    Hunts.insert({
      name: "Boston Hunt 1",
      data: "data",
    });
  };
});

