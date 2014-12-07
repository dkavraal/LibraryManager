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
<body>
	<div class="container" ng-app="LibMan">
		<div class="jumbotron">
			<h1>Library Manager</h1>
		</div>

		<div class="page-header">
			<h1>Add a Book</h1>
		</div>

		<div class="page-header">
			<h1>Library</h1>
		</div>

		<div class="container col-md-10">
			<table class="table table-striped" ng-controller="LibManLibrary">
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

		app.controller("LibManLibrary", function($scope, $http) {
			$http.get('/LibraryManager/books').success(
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
	</script>
</body>
</html>