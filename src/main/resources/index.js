angular.module('flinkApp').config(["$stateProvider", "$urlRouterProvider", function($stateProvider, $urlRouterProvider) {
  $stateProvider.state("admin", {
    url: "/admin",
    views: {
      main: {
        templateUrl: "partials/admin/alert.html",
        controller: "AdminController"
        }
      }
    });
  }]);
  
 angular.module('flinkApp').controller('AdminController', ["$scope", "AdminService", function($scope, AdminService) {
  return AdminService.loadAlertConfig().then(function(data) {
    if ($scope.alertmanager == null) {
      $scope.alertmanager = {};
    }
    return $scope.alertmanager['data'] = data;
  });
}]);

angular.module('flinkApp').service('AdminService', ["$http", "flinkConfig", "$q", function($http, flinkConfig, $q) {
  var data;
  data = {};
  this.loadAlertConfig = function() {
    var deferred;
    deferred = $q.defer();
    $http.get("http://<HOST>:<PORT>/admin/alertConfig").success(function(data, status, headers, config) {
      data = data;
      return deferred.resolve(data);
    });
    return deferred.promise;
  };
  return this;
}]);