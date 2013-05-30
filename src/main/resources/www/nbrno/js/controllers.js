'use strict';

/* Controllers */

function RapperListCtrl($scope, Rapper) {
  $scope.rappers = Rapper.query();
}