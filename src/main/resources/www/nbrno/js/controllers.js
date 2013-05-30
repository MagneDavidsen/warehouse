'use strict';

/* Controllers */

function RapperListCtrl($scope, Rapper) {
    $scope.rappers = Rapper.query();
}

function LoginCtrl($scope, Login, Signup) {

    $scope.loginUser = ""
    $scope.loginPassword = ""

    $scope.signupUser = ""
    $scope.signupEmail = ""
    $scope.signupPassword = ""

    $scope.login = function () {
        Login.save({
            username: $scope.loginUser, password: $scope.loginPassword});
    }

    $scope.signup = function () {
        Signup.save({
            username: $scope.signupUser, email: $scope.signupEmail, password: $scope.signupPassword});
    }


}