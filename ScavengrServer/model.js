
Users = new Meteor.Collection("users", { idGeneration :'MONGO' });
Users.allow({
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

Hunts = new Meteor.Collection("hunts", { idGeneration: 'MONGO' })
Hunts.allow({
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
