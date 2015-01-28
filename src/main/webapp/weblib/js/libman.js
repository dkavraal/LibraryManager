var app = angular.module("LibMan", ['dialogs']);

app.controller("LibManList", function($scope, $http) {
	$scope.loadLibrary = function() {
		$http.get(APPPATH + '/books')
			.success(
				function(data, status, headers, config) {
					if (data['RESP_CODE'] == 'OK') {
						try {
							$scope.bookList = data['R'][0]['bookList'];
						} catch (e) {
							//TODO server problem. db? tomcat? etc (see: data['RESP_CODE'])
							//put a refresh button
						}
					}
				}
			)
			.error(
				function(data, status, headers, config) {
					//TODO resp unavailable -- server down? user network down? timeout?
					//put a refresh button
				}
			);
	};

	$scope.$on('reloadLibrary', function(event, args) { $scope.loadLibrary();});
	
 	$scope.loadLibrary();
});

app.controller("LibManAdd", function($scope, $http) {
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
		$scope.captcha_image = APPPATH + '/captcha.jpg?' + new Date().getTime();
	};
	
	$scope.submitNewBook = function() {
		$scope.$broadcast('reloadLibrary', null);
		
		$scope.loading = true;
		var dataToSend = {
		          title:  $scope.title,
		          author: $scope.author,
		          verify: $scope.verify,
		       };
		
		$http({
			headers : { 'Content-Type': 'application/json; charset=utf-8' },
	        url     : APPPATH + '/addNewBook',
			data    : dataToSend,
			method  : 'POST',
	    }).success(function(data) {
            if (data['RESP_CODE'] == 'OK') {
                $scope.title = null;
                $scope.author = null;
                $scope.verify = null;
                $scope.refreshCaptcha();
                $scope.showSuccessMessage('Great. Done.');
                //TODO refresh list // or easily add the last added (returned info)
            } else {
            	$scope.showDangerMessage('There has been a problem. CODE:' + data['RESP_CODE']);
            }
            $scope.$emit('reloadLibrary');
        }).error(function(data, status, headers, config) {
        	$scope.showDangerMessage('There was a problem.');
        }).finally(function() {
        	$scope.loading = false;
        });
	};
});
