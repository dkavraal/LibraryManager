<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Library Manger</title>
	<link href="<c:url value="/weblib/css/bootstrap.min.css" />"  rel="stylesheet" />
	<link href="<c:url value="/weblib/css/libman.css" />"  rel="stylesheet" />
	<script type="text/javascript">var APPPATH="${pageContext.servletContext.contextPath}";</script>
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
					<img id="captchaImg" src="" ng-model="captcha" ng-src="{{captcha_image}}" data-ng-init="refreshCaptcha()" class="pull-left" title="Click to change for another" alttext="Security Code Captcha Image" ng-click="refreshCaptcha()" />
					<input type="text" class="form-control" id="newbook-captcha-input" placeholder="Code" size="5" ng-model="verify" required />
					<small>To change, click the image</small> 
				</div>
				
				<div class="col-md-1">
					<button type="submit" class="btn btn-success" id="submit" ng-disabled="loading">
						<span ng-hide="loading"><span class="glyphicon glyphicon-plus"></span> Add</span>
						<span ng-show="loading">Posting...</span>
					</button>
				</div>
			</form>
			<div class="col-md-offset-1 col-md-10">
				<div id="message_s_cont" class="alert alert-success text-center" ng-show="show_message_success" data-ng-show="message_success" data-ng-bind="message_success"></div>
				<div id="message_d_cont" class="alert alert-danger text-center" ng-show="show_message_danger" data-ng-show="message_danger" data-ng-bind="message_danger"></div>
			</div>
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

	<script src="<c:url value="/weblib/js/angular.min.js"/>"></script>
	<script src="<c:url value="/weblib/js/jquery.min.js"/>"></script>
	<script src="<c:url value="/weblib/js/bootstrap.min.js"/>"></script>
	<script src="<c:url value="/weblib/js/ui-bootstrap-tpls.js"/>"></script>
	<script src="<c:url value="/weblib/js/dialogs.min.js"/>"></script>
	<script src="<c:url value="/weblib/js/libman.js"/>"></script>
</body>
</html>