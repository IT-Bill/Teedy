'use strict';

/**
 * Main controller.
 */
angular.module('docs').controller('Main', function($scope, $rootScope, $state, User, Restangular) {
  User.userInfo().then(function(data) {
    if (data.anonymous) {
      $state.go('login', {}, {
        location: 'replace'
      });
    } else {
      $state.go('document.default', {}, {
        location: 'replace'
      });
    }

    // Load count of pending registration requests for admins
    if ($rootScope.userInfo && $rootScope.userInfo.base_functions && 
        $rootScope.userInfo.base_functions.indexOf('ADMIN') !== -1) {
      Restangular.one('user/register_request').get().then(function(data) {
        $rootScope.pendingRequestsCount = _.filter(data.requests, function(request) {
          return request.status === 'PENDING';
        }).length;
      });
    }
  });
});