'use strict';

/* Controllers */

function StatsCtrl($scope, sharedService, $http ) {
    $scope.numRappers;
    $scope.numUsers;
    $scope.numRatings;

    $http.get('api/stats').success(function(data){
        $scope.numRappers = data.numRappers;
        $scope.numUsers = data.numUsers;
        $scope.numRatings = data.numRatings;

    })
}

function RapperListCtrl($scope, sharedService, $http ) {
    $scope.rappers;
    $scope.predicate = ["score", "-name"];
    $scope.unauthorizedVote;

    $http.get('/api/rappers').
        success(function(data){
            $scope.rappers=data.rappers;
            if(data.votes.length > 0) updateRappersWithVotes($scope.rappers, data.votes);
        })

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
                if(rappers[i].id === votes[o].rapperId) rappers[i].rating = votes[o].rating;
            }
        }
        return rappers;
    }

    $scope.vote = function (rapperId, voteUp) {
        $http.post('/api/rappers/'+rapperId+'/vote', {voteUp: voteUp}).
            success(function(){
                for(var i = 0; i < $scope.rappers.length; i++){
                    if($scope.rappers[i].id == rapperId) $scope.rappers[i].rating = voteUp ? 1 : -1;
                }
                voted();
            }).error(function(){
                $scope.unauthorizedVote = {rapperId: rapperId, voteUp:voteUp}
                showLogin();
            });
    }

    function showLogin() {
        sharedService.prepForBroadcast("showLogin");
    };

    $scope.$on('handleBroadcast', function() {
        switch(sharedService.message)
        {
            case "loggedIn":
                if($scope.unauthorizedVote != null){
                    console.log("unauthorizedVote getting sent");
                    $scope.vote($scope.unauthorizedVote.rapperId, $scope.unauthorizedVote.voteUp)
                    $scope.unauthorizedVote = undefined;
                }
                $scope.rappers = updateRappersWithVotes($scope.rappers, sharedService.votes);
                break;
        }
    });
}

function LoginCtrl($scope, sharedService, $http, $cookies) {



    $scope.showLogin;
    $scope.loggedIn;

    $scope.loginUser = ""
    $scope.loginPassword = ""

    $scope.signupUser = ""
    $scope.signupEmail = ""
    $scope.signupPassword = ""

    $scope.error = ""

    //TODO: implement automatic login with cookie


    $scope.$on('handleBroadcast', function() {
        switch(sharedService.message)
        {
            case "showLogin":
                $scope.showLogin = true
                break;
        }
    });

    function isNotEmptyOrUndefined(text){
        return text != null && text != "" && text != undefined;
    }

    function loggedIn(data, status, header){
        console.log("logged in");

        $scope.showLogin = false;
        $scope.loggedIn = true;

        sharedService.votes = data;
        sharedService.prepForBroadcast("loggedIn")
    }

    function notLoggedIn(data, status, header){
        $scope.error = "Feil brukernavn eller passord"
    }

    function loggedOut(data, status, header){
        console.log("logged out")
        $scope.loggedIn = false;
    }

    function notLoggedOut(data, status, header){
        console.log("could not log out")
    }

    function notSignedup(data, status, header){
        $scope.error = "Feil brukernavn eller passord"
    }

    function loginAfterSignup(){
        $scope.loginUser = $scope.signupUser;
        $scope.loginPassword = $scope.signupPassword;
        $scope.login();
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
            success(loginAfterSignup).error(notSignedup);
    }

    if(isNotEmptyOrUndefined($cookies.SESSION_ID)){
        $http.post('/api/user/login/cookie',{SESSION_ID: $cookies.SESSION_ID}).
            success(loggedIn).error(notLoggedIn);
    }
}

