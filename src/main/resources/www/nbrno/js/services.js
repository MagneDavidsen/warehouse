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
        return $resource('api/login', {}, {
            save: {method:'POST', isArray: false  }
        });
    });

var signupService =  angular.module('signupServices', ['ngResource']).
    factory('Signup', function($resource){
        return $resource('api/signup', {}, {
            save: {method:'POST', isArray: false  }
        });
    });

var voteService =  angular.module('voteServices', ['ngResource']).
    factory('Vote', function($resource){
        return $resource('api/rappers/:rapperId/vote', {rapperId: '@rapperId'}, {
            save: {method:'POST', isArray: false  }
        });
    });