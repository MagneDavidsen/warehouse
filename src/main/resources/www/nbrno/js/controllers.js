'use strict';

/* Controllers */

function RapperListCtrl($scope, sharedService, $http ) {
    $scope.rappers;
    $scope.predicate = ["score", "-name"];

    $http.get('/api/rappers').
        success(function(data){$scope.rappers=data})

    function voted(data){
        console.log("voted")
    }

    function novote(data){
        showLogin()
        console.log("not voted")
    }

    function updateRappersWithVotes(rappers, votes){
        for(var i = 0; i<rappers.length;i++){
            for(var o = 0; o<votes.length;o++){
                if(rappers[i].id === votes[o].rapperId){
                    console.log("rapper.id: " + rappers[i].id)
                    console.log("vote.rapperId: " + votes[o].rapperId)
                    rappers[i].rating = votes[o].rating;
                }
            }
        }
        return rappers;
    }

    $scope.vote = function (rapperId, voteUp) {
        $http.post('/api/rappers/'+rapperId+'/vote', {voteUp: voteUp}).
            success(voted).error(showLogin);
    }

    function showLogin() {
        sharedService.prepForBroadcast("showLogin");
    };

    $scope.$on('handleBroadcast', function() {
        switch(sharedService.message)
        {
            case "updateRappers":
                $scope.rappers = updateRappersWithVotes($scope.rappers, sharedService.votes);
                break;
        }
    });
}

function LoginCtrl($scope, sharedService, $http, $cookies) {

    $scope.showLogin = false;

    $scope.loginUser = ""
    $scope.loginPassword = ""

    $scope.signupUser = ""
    $scope.signupEmail = ""
    $scope.signupPassword = ""

    $scope.error = ""

    $scope.$on('handleBroadcast', function() {
        switch(sharedService.message)
        {
            case "showLogin":
                $scope.showLogin = true
                break;
        }
    });

    function loggedIn(data, status, header){
        console.log("logged in")
        $scope.showLogin = false

        sharedService.votes = data;
        sharedService.prepForBroadcast("updateRappers")
    }

    function notLoggedIn(data, status, header){
        $scope.error = "Feil brukernavn eller passord"
    }

    function loggedOut(data, status, header){
        console.log("logged out")
    }

    function notLoggedOut(data, status, header){
        console.log("could not log out")
    }

    function notSignedup(data, status, header){
        $scope.error = "Feil brukernavn eller passord"
    }

    $scope.login = function () {
        $http.post('/api/user/login', {username: $scope.loginUser, password: $scope.loginPassword}).
            success(loggedIn).error(notLoggedIn);
    }

    $scope.logout = function () {
        $http.post('/api/user/logout', {username: $scope.loginUser}  ).
            success(loggedOut).error(notLoggedOut);
    }

    $scope.signup = function () {
        $http.post('/api/user/signup', {username: $scope.signupUser, email: $scope.signupEmail, password: $scope.signupPassword}).
            success(loggedIn).error(notSignedup);
    }
}

