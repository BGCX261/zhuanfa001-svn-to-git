<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<title><sitemesh:write property='title'/></title>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta http-equiv="Cache-Control" content="no-store" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta property="wb:webmaster" content="e56d61995265a730" />

<link type="image/x-icon" href="/static/images/favicon.ico"
	rel="shortcut icon">
<!-- Le styles -->
<link href="/static/bootstrap/2.3.0/css/bootstrap.min.css"
	rel="stylesheet">
<style type="text/css">
body {
	padding-top: 20px;
	padding-bottom: 40px;
}

/* Custom container */
.container-narrow {
	margin: 0 auto;
	max-width: 700px;
}

.container-narrow>hr {
	margin: 30px 0;
}

/* Supporting marketing content */
.marketing {
	margin: 60px 0;
}

.marketing p+h4 {
	margin-top: 28px;
}
</style>
<link href="/static/bootstrap/2.3.0/css/bootstrap-responsive.min.css"
	rel="stylesheet">

<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="/static/bootstrap/2.3.0/js/html5shiv.js"></script>
    <![endif]-->
<link href="/static/jquery-validation/1.11.0/validate.css"
	type="text/css" rel="stylesheet" />

<sitemesh:write property='head' />
</head>

<body>
	<div class="container-narrow">

		<div class="masthead">
			<ul class="nav nav-pills pull-right">
				<li><a href="#">我要转发</a></li>
				<li><a href="#">帮我转发</a></li>
				<shiro:guest>
					<li><a
						href="https://api.weibo.com/oauth2/authorize?client_id=485670387&response_type=code&redirect_uri=http://www.51zhuanfa.com/login">微博登录</a>
					</li>
				</shiro:guest>
				<shiro:user>
					<li><a href="#">你好，<shiro:principal property="screenName" />！
					</a></li>
					<li><a href="/logout">退出</a></li>
				</shiro:user>
			</ul>
			<h3 class="muted">我要转发</h3>
		</div>

		<hr>

		<div class="row-fluid marketing">
			<sitemesh:write property='body' />
		</div>

		<hr>

		<div class="footer">
			<p>&copy; 51zhuanfa 2013</p>
		</div>

	</div>

	<script src="/static/bootstrap/2.3.0/js/bootstrap.min.js"
		type="text/javascript"></script>
	<script src="/static/jquery/jquery-1.9.1.min.js" type="text/javascript"></script>
	<script src="/static/jquery-validation/1.11.0/jquery.validate.min.js"
		type="text/javascript"></script>
	<script src="/static/jquery-validation/1.11.0/messages_bs_zh.js"
		type="text/javascript"></script>
	<script src="/static/js/json2.js" type="text/javascript"></script>
	<script
		src=" http://tjs.sjs.sinajs.cn/open/api/js/wb.js?appkey=485670387"
		type="text/javascript" charset="utf-8"></script>
</body>
</html>