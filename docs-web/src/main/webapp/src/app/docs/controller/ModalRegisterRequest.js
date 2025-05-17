'use strict';

/**
 * Modal registration request controller.
 */
angular.module('docs').controller('ModalRegisterRequest', function($scope, $uibModalInstance, Restangular, $translate, $dialog) {
  $scope.user = {};
  
  /**
   * Submit the registration request.
   */
  $scope.submit = function() {
    if ($scope.user.password !== $scope.user.passwordconfirm) {
      var title = $translate.instant('validation.error');
      var msg = $translate.instant('validation.password_confirm');
      var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
      $dialog.messageBox(title, msg, btns);
      return;
    }
    
    Restangular.one('user/register_request').put({
        username: $scope.user.username,
        password: $scope.user.password,
        email: $scope.user.email
    }).then(function() {
      $uibModalInstance.close($scope.user.username);
    }, function(e) {
      if (e.data.type === 'AlreadyExistingUsername') {
        var title = $translate.instant('login.register_request_error_title');
        var msg = $translate.instant('login.register_request_error_message');
        var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
        $dialog.messageBox(title, msg, btns);
      }
    });
  };
  
  /**
   * Cancel the registration request.
   */
  $scope.close = function() {
    $uibModalInstance.dismiss('cancel');
  };
});