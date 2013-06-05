'use strict';

/* Controllers */

function RapperListCtrl($scope, Rapper, Vote) {
    $scope.rappers = Rapper.query();
    $scope.predicate = "score";

    var vote = function (rapperId, voteUp) {
        Vote.save({
            rapperId: rapperId, voteUp: voteUp});
    }

    $scope.vote = function (rapperId, voteUp) {
        var result = vote(rapperId, voteUp)
        console.log(result)
    }


}

function LoginCtrl($scope, $http, $cookies, Signup) {

    $scope.loginUser = ""
    $scope.loginPassword = ""

    $scope.signupUser = ""
    $scope.signupEmail = ""
    $scope.signupPassword = ""

    $scope.error = ""

    function loggedIn(data, status, header){
        $cookies.SESSION_ID = data.SESSION_ID

    }

    function notLoggedIn(data, status, header){
        $scope.error = "Feil brukernavn eller passord"
    }

    $scope.login = function () {
        $http.post('/api/user/login', {username: $scope.loginUser, password: $scope.loginPassword}).
            success(loggedIn).error(notLoggedIn);
    }

    $scope.signup = function () {
        Signup.save({
            username: $scope.signupUser, email: $scope.signupEmail, password: $scope.signupPassword});
    }


}