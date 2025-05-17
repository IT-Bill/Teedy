'use strict';

/**
 * Settings register controller.
 */
angular.module('docs').controller('SettingsRegister', function($scope, $uibModal, $dialog, Restangular, $translate) {
  // Load registration requests
  $scope.loadRequests = function() {
    Restangular.one('user/register_request').get({
      sort_column: 1,
      asc: false
    }).then(function(data) {
      $scope.requests = data.requests;
    });
  };
  
  // Load registration requests on page load
  $scope.loadRequests();
  
  // Approve a registration request
  $scope.approve = function(request) {
    var title = $translate.instant('settings.register.approve_confirm_title');
    var msg = $translate.instant('settings.register.approve_confirm_message', { username: request.username });
    var btns = [
      { result:'cancel', label: $translate.instant('cancel') },
      { result:'ok', label: $translate.instant('ok'), cssClass: 'btn-primary' }
    ];

    $dialog.messageBox(title, msg, btns, function (result) {
      if (result === 'ok') {
        Restangular.one('user/register_request', request.id).post('approve').then(function() {
          $scope.loadRequests();
        }, function(e) {
          if (e.data.type === 'AlreadyExistingUsername') {
            var title = $translate.instant('settings.register.error_title');
            var msg = $translate.instant('settings.register.error_username_exists');
            var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
            $dialog.messageBox(title, msg, btns);
          } else if (e.data.type === 'AlreadyProcessed') {
            $scope.loadRequests();
          }
        });
      }
    });
  };
  
  // Reject a registration request
  $scope.reject = function(request) {
    $uibModal.open({
      templateUrl: 'partial/docs/settings.register.reject.html',
      controller: 'SettingsRegisterReject',
      resolve: {
        request: function() {
          return request;
        }
      }
    }).result.then(function() {
      $scope.loadRequests();
    });
  };
});