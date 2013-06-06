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

    $scope.vote = function (rapperId, voteUp) {
        $http.post('/api/rappers/'+rapperId+'/vote', {voteUp: voteUp}).
            success(voted).error(showLogin);
    }

    function showLogin() {
        sharedService.prepForBroadcast("showLogin");
    };


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

    }

    function notLoggedIn(data, status, header){
        $scope.error = "Feil brukernavn eller passord"
    }

    function notSignedup(data, status, header){
        $scope.error = "Feil brukernavn eller passord"
    }

    $scope.login = function () {
        $http.post('/api/user/login', {username: $scope.loginUser, password: $scope.loginPassword}).
            success(loggedIn).error(notLoggedIn);
    }

    $scope.signup = function () {

        $http.post('/api/user/signup', {username: $scope.signupUser, email: $scope.signupEmail, password: $scope.signupPassword}).
            success(loggedIn).error(notSignedup);
    }

}

