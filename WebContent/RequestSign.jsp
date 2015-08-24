<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<h3>- In-Person Signing Demo!!!</h3>

	<a href="${pageContext.request.contextPath}/RequestSign">Start Signing With Default Params</a>
	<form action="${pageContext.request.contextPath}/RequestSign" method="get">
		<br> 
		<br> 
		<label>First Signer</label>
		<input type="text" name="signerName1" style='margin-left: 30px;'>
		 <br> 
		 <br>
		<label>Second Signer</label><input type="text" name="signerName2" style='margin-left: 13px;'> 
		<br> 
		<br> 
		<input type="submit">
	</form>

</body>
</html>