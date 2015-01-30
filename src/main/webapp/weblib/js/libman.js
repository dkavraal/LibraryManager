var appPath = function(p) { if (APPPATH !== undefined && APPPATH != null && APPPATH != "") { return APPPATH + "/" + p; } else { return p } };
var app = angular.module("LibMan", ['ui.bootstrap', 'ngAnimate', 'ngDialog']);

app.controller("LibManList", function($scope, $rootScope, $http, ngDialog) {
	$scope.loadLibrary = function() {
		$scope.loading = true;
		$http.get(appPath('/books'))
			.success(
				function(data, status, headers, config) {
					$scope.loading = false;
					$scope.loadError = false;
					if (data['RESP_CODE'] == 'OK') {
						try {
							$scope.bookList = data['R'][0]['bookList'];
						} catch (e) {
							$scope.loading = false;
							$scope.loadError = true;
						}
					} else {
						$scope.loading = false;
						$scope.loadError = true;
					}
				}
			)
			.error(
				function(data, status, headers, config) {
					$scope.loading = false;
					$scope.loadError = true;
				}
			);
	};
	
	$scope.openDelDialog = function(book) {
		$scope.openedDialog = ngDialog.open({
			template: appPath('/weblib/template/removeItem.html'),
			controller: 'modalDelDialogCtrl',
			scope: $scope,
			data: {book: book},
		});
	};

	$scope.openUpdDialog = function(book) {
		$scope.updatebook = cloneObject(book);
		$scope.openedDialog = ngDialog.open({
			template: appPath('/weblib/template/editItem.html'),
			controller: 'modalUpdDialogCtrl',
			scope: $scope,
			data: {book: book},
		});
	};

	$rootScope.$on('reloadLibrary', function(event, args) { $scope.loadLibrary();});
	$scope.loadLibrary();
});

app.controller("modalDelDialogCtrl", function ($scope, $rootScope, $http, ngDialog) {
	$scope.ok = function(id) {
		$scope.deleteProcess = true;
		
		$http({
			headers : { 'Content-Type': 'application/json; charset=utf-8' },
	        url     : appPath('/deleteBook/'+id),
			method  : 'GET',
	    }).success(function(data) {
			if (data['RESP_CODE'] == 'OK') {
				$rootScope.$emit('showSuccessMessage', 'Great. Deleted, as you wished.');
			} else {
				$rootScope.$emit('showDangerMessage', 'There has been a problem. CODE:' + data['RESP_CODE']);
			}

			$rootScope.$emit('reloadLibrary');
      }).error(function(data, status, headers, config) {
    	  $rootScope.$emit('showDangerMessage', 'There has been a problem.');
      })['finally'](function() {
    	  $scope.deleteProcess = false;
    	  $scope.openedDialog.close();
      });
	};

	$scope.cancel = function () {
		$scope.openedDialog.close();
	};
});

app.controller("modalUpdDialogCtrl", function ($scope, $rootScope, $http, ngDialog) {
	$scope.ok = function(id, title, author) {
		$scope.updateProcess = true;
		var dataToSend = {
	          title:  $scope.updatebook.title,
	          author: $scope.updatebook.author,
	          verify: 'OK',
	       };
	
		$http({
			headers : { 'Content-Type': 'application/json; charset=utf-8' },
			data	: dataToSend,
	        url     : appPath('/updateBook/' + id),
			method  : 'POST',
	    }).success(function(data) {
			if (data['RESP_CODE'] == 'OK') {
				$rootScope.$emit('showSuccessMessage', 'Great. Updated, as you wished.');
			} else {
				$rootScope.$emit('showDangerMessage', 'There has been a problem. CODE:' + data['RESP_CODE']);
			};

			$rootScope.$emit('reloadLibrary');
      }).error(function(data, status, headers, config) {
    	  $rootScope.$emit('showDangerMessage', 'There has been a problem.');
      })['finally'](function() {
    	  $scope.updateProcess = false;
    	  $scope.openedDialog.close();
      });
	};

	$scope.cancel = function () {
		$scope.openedDialog.close();
	};
});

app.controller("LibManAdd", function($scope, $rootScope, $http) {
	$scope.showSuccessMessage = function(message, timeout_value) {
		if (timeout_value === undefined) timeout_value = 3000;
		
		try { clearTimeout($scope.success_dialog_timeout); } catch (e) {}
		$scope.message_success = message;
		$scope.show_message_success = true;
		$scope.success_dialog_timeout = setTimeout(function() {
			$scope.message_success = null;
			$scope.show_message_success = false;
		}, timeout_value);
	}
	
	$scope.showDangerMessage = function(message, timeout_value) {
		if (timeout_value === undefined) timeout_value = 3000;
		
		try { clearTimeout($scope.danger_dialog_timeout); } catch (e) {}
		$scope.message_danger = message;
		$scope.show_message_danger = true;
		$scope.danger_dialog_timeout = setTimeout(function() {
			$scope.message_danger = null;
			$scope.show_message_danger = false;
		}, timeout_value);
	}
	
	$scope.refreshCaptcha = function() {
		$scope.captcha_image = '#';
		$scope.captcha_image = appPath('/captcha.jpg') + '?' + new Date().getTime();
	};
	
	$scope.submitNewBook = function() {
		$rootScope.$broadcast('reloadLibrary', null);
		
		$scope.loading = true;
		var dataToSend = {
		          title:  $scope.title,
		          author: $scope.author,
		          verify: $scope.verify,
		       };
		
		$http({
			headers : { 'Content-Type': 'application/json; charset=utf-8' },
	        url     : appPath('/addNewBook'),
			data    : dataToSend,
			method  : 'POST',
	    }).success(function(data) {
            if (data['RESP_CODE'] == 'OK') {
                $scope.title = null;
                $scope.author = null;
                $scope.verify = null;
                $scope.showSuccessMessage('Great. Done.');
            } else {
            	$scope.showDangerMessage('There has been a problem. CODE:' + data['RESP_CODE']);
            }
            $scope.$emit('reloadLibrary');
        }).error(function(data, status, headers, config) {
        	if (status == 400) {
        		$scope.showDangerMessage('Please fill in the required fields.');
			} else {
				$scope.showDangerMessage('There was a problem.');
			}
        })['finally'](function() {
        	$scope.loading = false;
        	$scope.refreshCaptcha();
        });
	};
	
	$rootScope.$on('showDangerMessage', function(event, args) { $scope.showDangerMessage(args);});
	$rootScope.$on('showSuccessMessage', function(event, args) { $scope.showSuccessMessage(args);});
	
});

app.config(['ngDialogProvider', function (ngDialogProvider) {
	ngDialogProvider.setDefaults({
		className: 'ngdialog-theme-default',
		controller: 'InsideCtrl',
		plain: false,
		showClose: false,
		closeByDocument: true,
		closeByEscape: true,
		appendTo: false,
		/*preCloseCallback: function () {
			console.log('default pre-close callback');
		}*/
	});
}]);

function cloneObject(o) {
    function F() {}
    F.prototype = o;
    return new F();
}