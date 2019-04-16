<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/4/11 0011
  Time: 22:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
</head>
<main>
    <div align="center">
        <a href="/a/u/home" methods="get">首页</a>
    </div>
    <div align="center">
        <form id="userform" method="post" action="/u/upload" enctype="multipart/form-data">
            <input type="file" id="pictureImg" name="pictureImg" accept="image/jpeg" style="display: none;"  onchange="photoImgChange();">
            <%--检测是否有头像,无头像显示默认--%>
            <c:if test="${!empty user.head_img}">
                <img id="user_header" src="${user.head_img}" style="height:96px;">
            </c:if>
            <c:if test="${empty user.head_img}">
                <img id="user_header" src="http://pok55eseq.bkt.clouddn.com//default/all/0/ef7aaf2a1dbe46858e9c2b86cdb97e83.png" style="height:96px;">
            </c:if>
            <div  style="margin:15px 0px;" class="clearfix">
                <li >
                    <a align="center"  href="javascript:void(0);" onclick="doUpload();" >更换头像</a>
                    <span id="imgErrSpan" style="color:red;font-weight:normal;float:left;margin-left:10px;margin-top:5px;"></span></li>
            </div>
            <input type="hidden" name="id" id="id" value="${user.id}">
            <li><label>用户名</label>
                <input name="username" id="username"  value="${user.username}" type="text" >
            </li>
            <li><label>手机</label>
                <input name="phone" id="phone"  value="${user.phone}" type="text" >
            </li>
            <li><label>邮箱</label>
                <input name="email" id="email"  value="${user.email}" type="text" >
            </li>
            <li><label>qq</label>
                <input name="qq" id="qq"  value="${user.qq}" type="text" >
            </li>
            <li><label>地址</label>
                <input name="address" id="address"  value="${user.address}" type="text" >
            </li>
            <li >
                <div ><input type="submit" value="保存"></div>
            </li>
            <li>
                <div id="myAlert" class="alert alert-success" style="display: none;">
                    <span id="myAlert_msg" class="color-oc f-16">保存成功！</span>
                </div>
            </li>

        </form>
    </div>

    <script type="text/javascript">

        function doUpload(){
            $('#pictureImg').click();
        }
        function photoImgChange(){
            var img = $('#pictureImg').val();
            if(photoValid(img)){
                previewUploadImg('pictureImg','user_header');
                $('#user_header').show();
                $('#imgErrSpan').html('');
                return;
            }else{
                $('#imgErrSpan').html('&nbsp;请选择png,jpeg,jpg格式图片');
                $('#pictureImg').val('');
            }
        }

        function infoSubmit(){
            var id = $('#id').val();
            var formData = new FormData();
            formData.append('multipartFile', $('#pictureImg')[0].files[0]);

            $('#userform').ajaxSubmit({
                type: "post",
                url: '/u/upload/'+id,
                data:formData,
                processData: false,
                contentType: false,
                success : function(data) {
                    console.log(data)
                    var json = data.toString()
                    var msg = json.substr(10,3)
                    if (msg == 200) {
                        $("#myAlert").show().fadeOut(2500);//显示模态框
                    } else {
                        $("#myAlert").html("修改用户信息失败").fadeOut(2500);//显示模态框
                        $("#myAlert").show().fadeOut(2500);//显示模态框
                    }
                },
                error : function(xhr) {
                }
            });
        }

        //图片验证
        function photoValid(text){
            var photos = ['.jpg','.png','.jpeg'];
            var photoExt=text.substr(text.lastIndexOf(".")).toLowerCase();//获得文件后缀名
            var flag = false;
            for(var i = 0; i < photos.length; i++){
                if(photos[i] == photoExt){
                    flag = true;
                    break;
                }
            }
            return flag;
        }
        function previewUploadImg(fileElId,imgElId){
            var file = document.getElementById(fileElId);
            var pic = document.getElementById(imgElId);
            var isIE = navigator.userAgent.match(/MSIE/)!= null;
            var isIE6 = navigator.userAgent.match(/MSIE 6.0/)!= null;
            if(isIE) {
                file.select();
                var reallocalpath = document.selection.createRange().text;
                // IE6浏览器设置img的src为本地路径可以直接显示图片
                if (isIE6) {
                    pic.src = reallocalpath;
                }else {
                    // 非IE6版本的IE由于安全问题直接设置img的src无法显示本地图片，但是可以通过滤镜来实现
                    pic.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='image',src=\"" + reallocalpath + "\")";
                    // 设置img的src为base64编码的透明图片 取消显示浏览器默认图片
                    pic.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAP///wAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==';
                }
            }else{
                var reader = new FileReader();
                reader.readAsDataURL(file.files[0]);
                reader.onload = function(e){
                    pic.src = this.result;
                };
            }
        }
        $.fn.ajaxSubmit = function(options) {
            // fast fail if nothing selected (http://dev.jquery.com/ticket/2752)
            if (!this.length) {
                log('ajaxSubmit: skipping submit process - no element selected');
                return this;
            }

            if (typeof options == 'function')
                options = {success: options};

            var url = $.trim(this.attr('action'));
            if (url) {
                // clean url (don't include hash vaue)
                url = (url.match(/^([^#]+)/) || [])[1];
            }
            url = url || window.location.href || '';

            options = $.extend({
                url: url,
                type: this.attr('method') || 'GET',
                iframeSrc: /^https/i.test(window.location.href || '') ? 'javascript:false' : 'about:blank'
            }, options || {});

            // hook for manipulating the form data before it is extracted;
            // convenient for use with rich editors like tinyMCE or FCKEditor
            var veto = {};
            this.trigger('form-pre-serialize', [this, options, veto]);
            if (veto.veto) {
                log('ajaxSubmit: submit vetoed via form-pre-serialize trigger');
                return this;
            }
        }
    </script>

</main>



<main>
    <div align="center">
        <a href="/a/u/home" methods="get">首页</a>
    </div>
    <div align="center">
        <form id="userform" method="post" action="/u/upload" enctype="multipart/form-data">
            <input type="file" id="pictureImg" name="pictureImg" style="display: none;"  onchange="photoImgChange();">
            <%--检测是否有头像,无头像显示默认--%>
            <c:if test="${!empty user.head_img}">
                <img id="user_header" src="${user.head_img}" style="height:96px;">
            </c:if>
            <c:if test="${empty user.head_img}">
                <img id="user_header" src="http://pok55eseq.bkt.clouddn.com//default/all/0/ef7aaf2a1dbe46858e9c2b86cdb97e83.png" style="height:96px;">
            </c:if>
            <div  style="margin:15px 0px;" class="clearfix">
                <li >
                    <a align="center"  href="javascript:void(0);" onclick="doUpload();" >更换头像</a>
                    <span id="imgErrSpan" style="color:red;font-weight:normal;float:left;margin-left:10px;margin-top:5px;"></span></li>
            </div>
            <input type="hidden" name="id" id="id" value="${user.id}">
            <li><label>用户名</label>
                <input name="username" id="username"  value="${user.username}" type="text" >
            </li>
            <li><label>手机</label>
                <input name="phone" id="phone"  value="${user.phone}" type="text" >
            </li>
            <li><label>邮箱</label>
                <input name="email" id="email"  value="${user.email}" type="text" >
            </li>
            <li><label>qq</label>
                <input name="qq" id="qq"  value="${user.qq}" type="text" >
            </li>
            <li><label>地址</label>
                <input name="address" id="address"  value="${user.address}" type="text" >
            </li>
            <li >
                <div ><button onclick="infoSubmit();">保存</button>></div>
            </li>
            <li>
                <div id="myAlert" class="alert alert-success" style="display: none;">
                    <span id="myAlert_msg" class="color-oc f-16">保存成功！</span>
                </div>
            </li>

        </form>
    </div>

    <script type="text/javascript">

        function doUpload(){
            $('#pictureImg').click();
        }
        function photoImgChange(){
            var img = $('#pictureImg').val();
            if(photoValid(img)){
                previewUploadImg('pictureImg','user_header');
                $('#user_header').show();
                $('#imgErrSpan').html('');
                return;
            }else{
                $('#imgErrSpan').html('&nbsp;请选择png,jpeg,jpg格式图片');
                $('#pictureImg').val('');
            }
        }

        function infoSubmit(){
            // var username = $('#username').val();
            // var qq = $('#qq').val();
            // var email = $('#email').val();
            // var phone = $('#phone').val();
            // var address = $('#address').val();
            // var pictureImg = $('#pictureImg').val();
            // var id = $('#id').val();
            // var formData = new FormData();
            // formData.append('multipartFile', $('#pictureImg')[0].files[0]);

            $('#userform').ajaxSubmit({
                datatype : 'json',
                success : function(data) {
                    console.log(data)
                    var json = data.toString()
                    var msg = json.substr(10,3)
                    if (msg == 200) {
                        $("#myAlert").show().fadeOut(2500);//显示模态框
                    } else {
                        $("#myAlert").html("修改用户信息失败").fadeOut(2500);//显示模态框
                        $("#myAlert").show().fadeOut(2500);//显示模态框
                    }
                },
                error : function(xhr) {
                }
            });
        }

        //图片验证
        function photoValid(text){
            var photos = ['.jpg','.png','.jpeg'];
            var photoExt=text.substr(text.lastIndexOf(".")).toLowerCase();//获得文件后缀名
            var flag = false;
            for(var i = 0; i < photos.length; i++){
                if(photos[i] == photoExt){
                    flag = true;
                    break;
                }
            }
            return flag;
        }
        function previewUploadImg(fileElId,imgElId){
            var file = document.getElementById(fileElId);
            var pic = document.getElementById(imgElId);
            var isIE = navigator.userAgent.match(/MSIE/)!= null;
            var isIE6 = navigator.userAgent.match(/MSIE 6.0/)!= null;
            if(isIE) {
                file.select();
                var reallocalpath = document.selection.createRange().text;
                // IE6浏览器设置img的src为本地路径可以直接显示图片
                if (isIE6) {
                    pic.src = reallocalpath;
                }else {
                    // 非IE6版本的IE由于安全问题直接设置img的src无法显示本地图片，但是可以通过滤镜来实现
                    pic.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='image',src=\"" + reallocalpath + "\")";
                    // 设置img的src为base64编码的透明图片 取消显示浏览器默认图片
                    pic.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAP///wAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==';
                }
            }else{
                var reader = new FileReader();
                reader.readAsDataURL(file.files[0]);
                reader.onload = function(e){
                    pic.src = this.result;
                };
            }
        }
        $.fn.ajaxSubmit = function(options) {
            // fast fail if nothing selected (http://dev.jquery.com/ticket/2752)
            if (!this.length) {
                log('ajaxSubmit: skipping submit process - no element selected');
                return this;
            }

            if (typeof options == 'function')
                options = {success: options};

            var url = $.trim(this.attr('action'));
            if (url) {
                // clean url (don't include hash vaue)
                url = (url.match(/^([^#]+)/) || [])[1];
            }
            url = url || window.location.href || '';

            options = $.extend({
                url: url,
                type: this.attr('method') || 'GET',
                iframeSrc: /^https/i.test(window.location.href || '') ? 'javascript:false' : 'about:blank'
            }, options || {});

            // hook for manipulating the form data before it is extracted;
            // convenient for use with rich editors like tinyMCE or FCKEditor
            var veto = {};
            this.trigger('form-pre-serialize', [this, options, veto]);
            if (veto.veto) {
                log('ajaxSubmit: submit vetoed via form-pre-serialize trigger');
                return this;
            }
        }
    </script>

</main>