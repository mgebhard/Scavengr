Router.route('/stats', function(){
  this.render('homePage');
});

Router.route('/', function() {
  this.render('userLandingPage');
});
