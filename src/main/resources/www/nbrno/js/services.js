'use strict';

/* Services */

var rapperServiceModule = angular.module('rapperServices', ['ngResource']).
    factory('Rapper', function($resource){
  return $resource('api/rappers', {}, {
    query: {method:'GET', isArray:true}
  });

});

var loginService =  angular.module('loginServices', ['ngResource']).
    factory('Login', function($resource){
        return $resource('api/login', {username: '@username', password: '@password'}, {
            save: {method:'POST', isArray: false  }
        });
    });

var signupService =  angular.module('signupServices', ['ngResource']).
    factory('Signup', function($resource){
        return $resource('api/signup', {username: '@username', email: '@email', password: '@password'}, {
            save: {method:'POST', isArray: false  }
        });
    });