Deps.autorun(function(){
  Meteor.subscribe ('statistics');
  Meteor.subscribe ('posts');
  Meteor.subscribe ('users');
  Meteor.subscribe ('reviews');
  Meteor.subscribe ('hunts');
});
