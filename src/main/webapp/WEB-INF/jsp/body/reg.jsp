<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/2/27 0027
  Time: 19:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.staticfile.org/jquery/2.0.0/jquery.js"></script>
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
<style type="text/css">
    #tabs-508734 {
        width: 100%;
    margin-top: 100px;
    }
</style>
<script type="text/javascript">
    //用户名校验
    function checkUsername() {
        var username = document.getElementById('username1').value;
        console.log(username)
        if(username == ""){
            $('#messagePhone').html("<font color=\"#F60018\">* 请输入用户名!</font>");
            $('#regButtonPhone').attr("disabled", true);
            return false;
        }
        var url = "/u/checkUserName/" + this.value;
        $.getJSON(url,function(data){
            if(data.status == 200) {
                $('#regButtonPhone').attr("disabled", false);
                $("#messagePhone").html("<font color=\"#33CC00\">* 用户名可以注册</font>")
                return true;
            } else {
                $("#messagePhone").html("<font color=\"#E3001B\">* 用户名已经存在</font>");
                $('#regButtonPhone').attr("disabled", true);
                return false;
            }
        });
    }
    //密码格式校验
    function checkPassword() {
        var password = document.getElementById('password1').value;
        if (password =="") {
            $('#passwordMsg1').html("<font color=\"#F60018\">* 请输入密码!</font>");
            $('#regButtonPhone').attr("disabled", true);
            return false;
        } else if (password.length < 6 || password.length >16) {
            $('#passwordMsg1').html("<i class=\"fa fa-times-circle\" style=\"font-size:14px;color:red\"></i><font color=\"#F60018\"> 您的密码少于6位或多于16位!</font>");
            $('#regButtonPhone').attr("disabled", true);
            return false;
        } else {
            $('#passwordMsg1').html("<i class=\"fa fa-times-circle\" style=\"font-size:14px;color:green\"></i><font color=\"#33CC00\"> 格式正确</font>");
            $('#regButtonPhone').attr("disabled", false);
            return true;
        }
    }
    //手机格式校验
    function checkPhone()
    {
        var phone =document.getElementById('phone').value;
        console.log("手机号码 = "+phone)
        var partten = /^1[3,5,6,7,8]\d{9}$/;
        if(partten.test(phone))
        {
            $('#regButtonPhone').attr("disabled", false);
            $('#Phone').html("<i class=\"fa fa-times-circle\" style=\"font-size:14px;color:green\"></i><font color=\"#33CC00\"> 格式正确</font>");
            return true;
        }
        else
        {
            $('#regButtonPhone').attr("disabled", true);
            $('#Phone').html("<i class=\"fa fa-times-circle\" style=\"font-size:14px;color:red\"></i><font color=\"#F60018\"> 请输入正确的手机号码</font>");
            return false;
        }
    }
    //验证码非空判定
    function checkPhoneCode() {
        var code = $('#phoneCode').val();
        console.log(code);
        if (code==null||code=="") {
            $('#PhoneCode').html("<font color=\"#E3001B\">请输入验证码</font>");
            $('#regButtonPhone').attr("disabled", true);
            return false;
        } else
            $('#PhoneCode').html("<font color=\"#E3001B\"></font>");
        return true;
    }
    //验证码倒计时
    var InterValObj; //timer变量，控制时间
    var count = 60; //间隔函数，1秒执行
    var curCount; //当前剩余秒数
    var code = ""; //验证码
    function sendPhoneCode() {
        curCount = count;
        var phone = $("#phone").val()
        if(validatePhone(phone)) {
            return;
        }
        if(phone != "") {
            //设置button效果，开始计时
            $("#getPhoneCode").attr("disabled", "true");
            // $("#getPhoneCode").val(curCount + "秒后可重新发送");
            //向后台发送处理数据
            $.ajax({
                url: '/a/u/sendPhoneCode',
                type: 'post',
                dataType: 'text',
                data: {
                    'phone':phone
                },
                success: function(data) {
                    var json = data.toString()
                    console.log(data)
                    var msg = json.substr(21,4)
                    console.log(msg)
                    //验证验证码是否发送成功
                    if (msg == "true") {
                        console.log("验证码发送成功,请留意手机信息");
                    } else {
                        console.log("验证码发送失败,请重试");
                    }
                },
                error: function (data) {
                    var json = JSON.stringify(data)
                    console.log("发送失败"+json)
                }
            });
            InterValObj = window.setInterval(SetRemainTimes, 1000); //启动计时器，1秒执行一次
        } else {
            alert("手机号码不能为空！！！！！！");
        }
    }
    //timer处理函数
    function SetRemainTimes() {
        if(curCount == 0) {
            window.clearInterval(InterValObj); //停止计时器
            $("#getPhoneCode").removeAttr("disabled"); //启用按钮
            $("#getPhoneCode").html("重新发送验证码");
            code = ""; //清除验证码。如果不清除，过时间后，输入收到的验证码依然有效
        } else {
            curCount--;
            $("#getPhoneCode").html(curCount + "秒后可重新发送");
        }
    }
    //验证手机号
    function validatePhone(phone) {
        if(phone == '') {
            $("#errMsg1").html("  请先填写手机号");
            $("#errMsg1").show();
            return true;
        }
        var myreg = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
        if(!myreg.test(phone)) {
            $("#errMsg1").html(" 请输入有效的手机号");
            $("#errMsg1").show();
            return true;
        }
        $("#errMsg1").html("");
        $("#errMsg1").show();
        return false;
    }
    //表单提交
    function doSubmit(){
        var username = $('#username1').val();
        var password = $('#password1').val();
        var phone = $('#phone').val();
        var code = $('#phoneCode').val();
        // if (checkUsername() && checkPassword() && checkPhone() && sendPhoneCode()) {
        //
        // }
        //提交注册
        $.ajax({
            url: '/a/u/regPhone',
            type: 'post',
            dataType: 'text',
            data: {
                'username':username,
                'password':password,
                'phone':phone,
                'code':code
            },
            success: function(data) {
                console.log(data)
                var json = data.toString()
                var msg = json.substr(10,3)
                console.log(msg)
                //验证验证码是否发送成功
                if (msg == 200) {
                    $('#errorMsg').show();
                    $('#errorMsg').html("注册成功！3s后跳转到登陆页！");
                    setTimeout(function(){
                        window.location.href="/a/u/gologin";
                    },3000)
                } else if(msg == 401) {
                    $('#errorMsg').show();
                    $('#errorMsg').html("用户信息为空");
                }else if(msg == 402){
                    $('#errorMsg').show();
                    $('#errorMsg').html("验证码为空！");
                }else if(msg == 403) {
                    $('#errorMsg').show();
                    $('#errorMsg').html("该用户名已被注册");
                }else if(msg == 404) {
                    $('#errorMsg').show();
                    $('#errorMsg').html("验证码错误");
                }else if(msg == 405) {
                    $('#errorMsg').show();
                    $('#errorMsg').html("验证码过期");
                }
            },
            error: function (data) {
                var json = JSON.stringify(data)
                console.log("发送失败"+json)
            }
        });
    }

    function sendEmailCode() {
        var time = Date.now()
        console.log(time)
        var email = $("#email").val()
        if(email != "") {
            //设置button效果，开始计时
            // $("#getPhoneCode").attr("disabled", "true");
            // $("#getPhoneCode").val(curCount + "秒后可重新发送");
            //向后台发送处理数据
            $.ajax({
                url: '/a/u/sendEmailCode',
                type: 'post',
                dataType: 'text',
                data: {
                    'email':email,
                    'time':time
                },
                success: function(data) {
                    var json = data.toString()
                    console.log(data)
                    var msg = json.substr(21,4)
                    console.log(msg)
                    //验证验证码是否发送成功
                    if (msg == "true") {
                        console.log("验证码发送成功,请留意手机信息");
                    } else {
                        console.log("验证码发送失败,请重试");
                    }
                },
                error: function (data) {
                    var json = JSON.stringify(data)
                    console.log("发送失败"+json)
                }
            });

        } else {
            alert("邮箱不能为空！！！！！！");
        }
    }

    function doSubmitEmail(){
        var username = $('#username').val();
        var password = $('#password').val();
        var email = $('#email').val();
        var code = $('#emailCode').val();
        //提交注册
        $.ajax({
            url: '/a/u/regEmail',
            type: 'post',
            dataType: 'text',
            data: {
                'username':username,
                'password':password,
                'email':email,
                'code':code
            },
            success: function(data) {
                console.log(data)
                var json = data.toString()
                var msg = json.substr(10,3)
                console.log(msg)
                //验证验证码是否发送成功
                if (msg == 200) {
                    $('#errorMsg1').show();
                    $('#errorMsg1').html("注册成功！3s后跳转到登陆页！");
                    setTimeout(function(){
                        window.location.href="/a/u/gologin";
                    },3000)
                } else if(msg == 401) {
                    $('#errorMsg1').show();
                    $('#errorMsg1').html("用户信息为空");
                }else if(msg == 402){
                    $('#errorMsg1').show();
                    $('#errorMsg1').html("验证码为空！");
                }else if(msg == 403) {
                    $('#errorMsg1').show();
                    $('#errorMsg1').html("该用户名已被注册");
                }else if(msg == 404) {
                    $('#errorMsg1').show();
                    $('#errorMsg1').html("验证码错误");
                }else if(msg == 405) {
                    $('#errorMsg1').show();
                    $('#errorMsg1').html("验证码过期");
                }else if(msg == 406) {
                    $('#errorMsg1').show();
                    $('#errorMsg1').html("邮箱为空");
                }
            }
        });
    }
</script>

<main>



<div class="container-fluid">
    <div class="row">
        <div class="col-md-6" style="text-align: center">
            <div class="col-md-6" style="padding:100px">
                <img src="http://jns.img.bucket.ks3-cn-beijing.ksyun.com/skill/skill_html/images/login-ad_03.png">
            </div>
        </div>
        <div class="container"  >
            <div class="row clearfix">
                <div class="col-md-12 column">
                    <div class="tabbable" id="tabs-508734" >
                        <ul class="nav nav-tabs">
                            <li class="active">
                                <a href="#panel-788844" data-toggle="tab">邮箱注册</a>
                            </li>
                            <li>
                                <a href="#panel-854065" data-toggle="tab">手机注册</a>
                            </li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane active" id="panel-788844">
                                <form method="post" action="/a/u/regEmail" >
                                    <span id="errorMsg1"  font-size: x-small;></span><br>
                                    <label>用户名：</label>
                                    <input type="text" id="username" name="username" value=""><span id="messageEmail"><font color="#FF0000">*</font></span><br>
                                    <label>密&nbsp;&nbsp;&nbsp;码：</label>
                                    <input type="password" name="password" id="password" value="" ><span id="passwordMsgEmail"><font color="#FF0000">*</font></span><br>
                                    <label>邮&nbsp;&nbsp;&nbsp;箱：</label>
                                    <input type="text" name="email" id="email" value="" onchange="checkEmail()" id="email"><br>
                                    <input type="text" name="code" id="emailCode"  value="" placeholder="请输入邮箱验证码"> <br>
                                    <button type="button" class="btn btn-primary btn-lg active" onclick="sendEmailCode()">获取邮箱验证码</button><br>
                                    <input id="regButtonEmail" type="button" onclick="doSubmitEmail()" class="btn btn-orange btn-lg active" value="注册" ></input>
                                    <a class="tn btn-orange"  href="/a/u/gologin">已有账号？去登录</a>
                                </form>
                            </div>


                            <div class="tab-pane fade" id="panel-854065">
                                <form id="regPhoneForm" name="regPhone" method="post" action="/a/u/regPhone">
                                    <span id="errorMsg"  font-size: x-small;></span><br>
                                    <label>用户名：</label>
                                    <input type="text" id="username1" name="username" onblur="checkUsername()" value=""><span id="messagePhone"><font color="#FF0000">*</font></span><br>
                                    <label>密&nbsp;&nbsp;&nbsp;码：</label>
                                    <input type="password"  id="password1" name="password" onblur="checkPassword()" value="" ><span id="passwordMsg1"><font color="#FF0000">*</font></span><br>
                                    <label>手&nbsp;&nbsp;&nbsp;机：</label>
                                    <input type="text" name="phone"  value="" onblur="checkPhone()" id="phone" ><span id="Phone"><font color="#FF0000">*</font></span><br>
                                    <input type="text" name="code" id="phoneCode" onblur="checkPhoneCode()" value="" placeholder="请输入手机验证码"><span id="PhoneCode"><font color="#FF0000"></font></span><br>
                                    <button type="button" id="getPhoneCode" onclick="sendPhoneCode()" class="btn btn-primary btn-lg active" >获取手机验证码</button><span id="errMsg1"><font color="#FF0000"></font></span><br>
                                    <input id="regButtonPhone" type="button" onclick="doSubmit()" class="btn btn-orange btn-lg active" value="注册">
                                    <a class="tn btn-orange"  href="/a/u/gologin">已有账号？去登录</a>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</main>
