
Statistics = new Meteor.Collection("statistics", {idGeneration : 'MONGO'});
Statistics.allow({
  insert: function(){
    return true;
  },
  update: function () {
    return true;
  },
  remove: function(){
    return true;
  }
});


Posts = new Meteor.Collection("posts", {idGeneration : 'MONGO'});
Posts.allow({
  insert: function(){
    return true;
  },
  update: function () {
    return true;
  },
  remove: function(){
    return true;
  }
});

Users = new Meteor.Collection("users", {idGeneration: 'MONGO'});
Users.allow({
  insert: function() {
    return true;
  },
  update: function() {
    return true;
  },
  remove: function() {
    return true;
  }
});

Hunts = new Meteor.Collection("hunts", {idGeneration: 'MONGO'});
Hunts.allow({
  insert: function() {
    return true;
  },
  update: function() {
    return true;
  },
  remove: function() {
    return true;
  }
});

Reviews = new Meteor.Collection("reviews", {idGeneration: 'MONGO'});
Reviews.allow({
  insert: function() {
    return true;
  },
  update: function() {
    return true;
  },
  remove: function() {
    return true;
  }
});

HuntsBackup = new Meteor.Collection("huntBackup", {idGeneration: 'MONGO'});
HuntsBackup.allow({
  insert: function() {
    return true;
  },
  update: function() {
    return true;
  },
  remove: function() {
    return false;
  }
});
