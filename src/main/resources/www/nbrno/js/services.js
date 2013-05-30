'use strict';

/* Services */

angular.module('rapperServices', ['ngResource']).
    factory('Rapper', function($resource){
  return $resource('api/rappers', {}, {
    query: {method:'GET', isArray:true}
  });
});