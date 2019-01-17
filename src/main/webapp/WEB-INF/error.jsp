<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ja" prefix="og: http://ogp.me/ns#">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0">
<title>${enquete.title} アンケート</title>
<meta property="og:title" content="${enquete.title}"/>
<meta property="og:description" content="${enquete.description}"/>
<meta property="og:type" content="article" />
<meta property="og:url" content="/enquete?id=${enquete.id}" />
<!-- <meta property="og:image" content=" サムネイル画像の URL" /> -->
<meta property="og:site_name" content="継続的アンケートサイト：継問（つぐもん）" />
</head>
<body>
<div id="ipAddress">${request.remoteAddr}</div>
<a href="home.html">home</a>
</body>
</html>