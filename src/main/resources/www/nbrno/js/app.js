'use strict';

/* App Module */

var theapp = angular.module('nbrno', ['ngCookies']).
  config(['$routeProvider', function($routeProvider) {

}]);

theapp.factory('sharedService', function($rootScope) {
    var sharedService = {};

    sharedService.message = '';
    sharedService.votes= '';
    sharedService.unauthorizedVote;

    sharedService.updateRappers = function(votes){
        sharedService.votes = votes;
        this.broadcastItem();
    }

    sharedService.prepForBroadcast = function(msg) {
        this.message = msg;
        this.broadcastItem();
    };

    sharedService.broadcastItem = function() {
        $rootScope.$broadcast('handleBroadcast');
    };

    return sharedService;
});