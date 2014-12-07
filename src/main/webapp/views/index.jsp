<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Library Manger</title>
	<link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css" rel="stylesheet">
	<style>
		.footer { position: fixed; bottom: 0; width: 100%; height: 17px; background-color: #f5f5f5; }
	</style>
</head>
<body ng-app="LibMan">
	<div class="container">
		<div class="jumbotron">
			<h1>Library Manager</h1>
		</div>

		<div class="page-header">
			<h1>Add a Book</h1>
		</div>
		
		<div class="container col-md-12" ng-controller="LibManAdd">
			<form role="form" ng-submit="submitNewBook()" class="form-horizontal" id="newbook_form" name="newbook_form">
				<div class="col-md-4">
					<label for="newbook-title">Title</label>
					<input type="text" class="form-control" id="newbook-title" placeholder="Title" ng-model="title" required />
				</div>
				
				<div class="col-md-3">
					<label for="newbook-title">Author</label>
					<input type="text" class="form-control" id="newbook-author" placeholder="Author" ng-model="author" required />
				</div>
				
				<div class="col-md-4">
					<img src="" ng-model="captcha" ng-src="{{captcha_image}}" data-ng-init="refreshCaptcha()" class="pull-left" title="Click to change for another" alttext="Security Code Captcha Image" ng-click="refreshCaptcha()" style="cursor:pointer; width: 200px; height: 70px; border:1px solid #CCD0E8; border-radius:4px;" />
					<input type="text" class="form-control" id="newbook-captcha" placeholder="Code" size="5" style="width: 85px;" ng-model="verify" required/>
					<small>To change, click the image</small> 
				</div>
				
				<div class="col-md-1">
					<button type="submit" class="btn btn-success" id="submit" ng-disabled="loading">
						<span ng-hide="loading"><span class="glyphicon glyphicon-plus"></span> Add</span>
						<span ng-show="loading">Posting...</span>
					</button>
					<div id="message_s_cont" class="alert alert-success" data-ng-show="message_success" data-ng-bind="message_success"></div>
					<div id="message_d_cont" class="alert alert-danger"  data-ng-show="message_danger" data-ng-bind="message_danger"></div>
				</div>
			</form>
		</div>
	</div>
	
	<div class="container">
		<div class="page-header">
			<h1>Library</h1>
		</div>

		<div class="container col-md-10">
			<table class="table table-striped" ng-controller="LibManList">
				<thead>
					<tr>
						<th>#</th>
						<th>Title</th>
						<th>Author</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody ng-repeat="book in bookList">
					<tr>
						<td>{{book.id}}</td>
						<td>{{book.title}}</td>
						<td>{{book.author}}</td>
						<td>Upd</td>
						<td>Del</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	
	<footer class="footer">
		<div class="container">
			<small>Library Manager ${version}</small>
		</div>
	</footer>

	<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.3.5/angular.min.js"></script>
	<script type="text/javascript">
		var app = angular.module("LibMan", []);

		app.controller("LibManList", function($scope, $http) {
			$http.get('${pageContext.servletContext.contextPath}/books').success(
				function(data, status, headers, config) {
					if (data['RESP_CODE'] == 'OK') {
						try {
							$scope.bookList = data['R'][0]['bookList'];
						} catch (e) {
							//TODO server problem. db? tomcat? etc (see: data['RESP_CODE'])
						}
					}
				}).error(function(data, status, headers, config) {
					//TODO resp unavailable -- server down? user network down? timeout? 
				});
		});
		
		app.controller("LibManAdd", function($scope, $http) {
			$scope.refreshCaptcha = function() {
				$scope.captcha_image = '${pageContext.servletContext.contextPath}/captcha.jpg?' + new Date().getTime();;
				try { $scope.$apply(); } catch(e) {}
			};
			
			$scope.submitNewBook = function() {
				$scope.loading = true;
				var dataToSend = {
				          title:  $scope.title,
				          author: $scope.author,
				          verify: $scope.verify,
				       };
				
				$http({
					headers : { 'Content-Type': 'application/json; charset=utf-8' }, //application/x-www-form-urlencoded
			        url     : '${pageContext.servletContext.contextPath}/addNewBook',
					data    : dataToSend,
					method  : 'POST',
			    }).success(function(data) {
		            if (!data['RESP_CODE'] == 'OK') {
		            	//$scope.errorName = data.errors.name;
		                //$scope.errorSuperhero = data.errors.superheroAlias;
		                $scope.message_success = 'Added.';
		                $scope.title = null;
		                $scope.author = null;
		                $scope.verify = null;
		                $scope.$setPristine();
		                refreshCaptcha();
		            } else {
		            	console.log(data['RESP_CODE']);
		            	$scope.message_danger = 'There was a problem.';
		            }
		        }).finally(function() {
		        	//$scope.loading = false;
		        	try {
			        	$timeout.cancel(scope.success_dialog_timeout);
			        	$timeout.cancel(scope.danger_dialog_timeout);
		        	} catch (e) {}
		        	
		        	$scope.success_dialog_timeout = $timeout(function() {
		        		$scope.message_success = null;
		        	}, 3000);
		        	$scope.danger_dialog_timeout = $timeout(function() {
		        		$scope.message_danger = null;
		        	}, 3000);
		        });
			};
		});
		
		
	</script>
</body>
</html>