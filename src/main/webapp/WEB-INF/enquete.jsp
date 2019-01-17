<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ja" prefix="og: http://ogp.me/ns#">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0">
<title>${enquete.title} / 継問個別ページ</title>
<meta property="og:title" content="${enquete.title} / 継問個別ページ"/>
<meta property="og:description" content="${enquete.descriptionEscaped}"/>
<meta property="og:type" content="article" />
<meta property="og:url" content="/enquete?id=${enquete.id}" />
<!-- <meta property="og:image" content=" サムネイル画像の URL" /> -->
<meta property="og:site_name" content="継続的アンケートサイト：継問（つぐもん）" />
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" />
<script async="async" src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="main.css"/>
</head>
<body class="container">
<header class="row">
<h1 id="logo" class="col-xs-6">継問</h1>
<p class="col-xs-6"><a href="app.html?enquete=${enquete.id}">アプリホーム</a></p>
</header>
<main class="row">
<div class="col-xs-12">IPアドレス：<span id="ipAddress"><% out.write(request.getRemoteAddr()); %></span></div>
<div class="col-xs-12">
<h2>アンケート</h2>
<div class="enquete">
<p>アンケートID:<span id="enqueteId">${enquete.id}</span> 作成日：${enquete.created}</p>
<p>${enquete.descriptionEscaped}</p>
<ol id="entries">
<c:forEach items="${enquete.entries}" var="entry" varStatus="v">
<li>${entry.stringEscaped}</li>
</c:forEach>
</ol>
<p>合計：${enquete.total}</p>
</div>
<span id="status"></span>
</div>
</main>

</body>
</html>