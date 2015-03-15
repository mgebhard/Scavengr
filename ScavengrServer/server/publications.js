


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
});

