'use strict';

/**
 * Settings register reject controller.
 */
angular.module('docs').controller('SettingsRegisterReject', function($scope, $uibModalInstance, Restangular, request, $dialog, $translate) {
  $scope.request = request;
  $scope.reason = '';
  
  // Close the modal
  $scope.close = function() {
    $uibModalInstance.dismiss('cancel');
  };
  
  // Reject the registration request
  $scope.reject = function() {
    Restangular.one('user/register_request', request.id).post('reject', {
      reason: $scope.reason
    }).then(function() {
      $uibModalInstance.close();
    }, function(e) {
      if (e.data.type === 'AlreadyProcessed') {
        $uibModalInstance.close();
      } else {
        var title = $translate.instant('settings.register.error_title');
        var msg = $translate.instant('settings.register.error_reject');
        var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
        $dialog.messageBox(title, msg, btns);
      }
    });
  };
});