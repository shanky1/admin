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
  
  angular.module('flinkApp').controller('AdminController', ["$scope", "JobSubmitService", "$interval", "flinkConfig", "$state", "$location", function($scope, JobSubmitService, $interval, flinkConfig, $state, $location) {
  $scope.yarn = $location.absUrl().indexOf("/proxy/application_") !== -1;
  return $scope.loadList = function() {
    return JobSubmitService.loadJarList().then(function(data) {
      $scope.address = data.address;
      $scope.noaccess = data.error;
      return $scope.jars = data.files;
    });
  };
}]);

angular.module('flinkApp').service('AdminService', ["$http", "flinkConfig", "$q", function($http, flinkConfig, $q) {
  this.loadAlerList = function() {
    var deferred;
    deferred = $q.defer();
    $http.get(flinkConfig.jobServer + "jars/").success(function(data, status, headers, config) {
      return deferred.resolve(data);
    });
    return deferred.promise;
  };
  return this;
}]);
