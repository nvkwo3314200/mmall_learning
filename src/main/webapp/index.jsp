<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<body>
<h2>Hello World!</h2>
<form action="manage/product/upload.do" enctype="multipart/form-data" method="post">
	<input type="file" name="upload_file"/>
	<input type="submit" value="文件上传">
</form>

<form action="manage/product/richtext_img_upload.do" enctype="multipart/form-data" method="post">
	<input type="file" name="upload_file"/>
	<input type="submit" value="富文本文件上传">
</form>

</body>
</html>
