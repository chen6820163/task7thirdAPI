function scscms_alert(msg,sign,ok,can){
    var c_=false;//是否已经关闭窗口，解决自动关闭与手动关闭冲突
    sign=sign||"";
    var s="<div id='mask_layer'></div><div id='scs_alert'><div id='alert_top'></div><div id='alert_bg'><table width='260' align='center' border='0' cellspacing='0' cellpadding='1'><tr>";
    if (sign!="")s+="<td width='45'><div id='inco_"+sign+"'></div></td>";
    s+="<td id='alert_txt'>"+msg+"</td></tr></table>";
    if (sign=="confirm"){
        s+="<a href='javascript:void(0)' id='confirm_ok'>确 定</a><a href='javascript:void(0)' id='confirm_cancel'>取 消</a>";
    }else{
        s+="<a href='javascript:void(0)' id='alert_ok'>确 定</a>"
    }
    s+="</div><div id='alert_foot'></div></div>";
    $("body").append(s);
    $("#scs_alert").css("margin-top",-($("#scs_alert").height()/2)+"px"); //使其垂直居中
    $("#scs_alert").focus(); //获取焦点，以防回车后无法触发函数

    if (typeof can == "number"){
        //定时关闭提示
        setTimeout(function(){
            close_info();
        },can*1000);
    }
    function close_info(){
        //关闭提示窗口
        if(!c_){
            $("#mask_layer").fadeOut("fast",function(){
                $("#scs_alert").remove();
                $(this).remove();
            });
            c_=true;
        }
    }
    $("#alert_ok").click(function(){
        close_info();
        if(typeof(ok)=="function")ok();
    });
    $("#confirm_ok").click(function(){
        close_info();
        if(typeof(ok)=="function")ok();
    });
    $("#confirm_cancel").click(function(){
        close_info();
        if(typeof(can)=="function")can();
    });
    function modal_key(e){
        e = e||event;
        close_info();
        var code = e.which||event.keyCode;
        if (code == 13 || code == 32){if(typeof(ok)=="function")ok()}
        if (code == 27){if(typeof(can)=="function")can()}
    }
    //绑定回车与ESC键
    if (document.attachEvent)
        document.attachEvent("onkeydown", modal_key);
    else
        document.addEventListener("keydown", modal_key, true);
}

function checkEmail()
{
    var email =document.getElementById('email').value;
    console.log("邮箱 = "+email)
    var partten = /^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/;
    if(partten.test(email))
    {
        return true;
    }
    else
    {
        alert('邮箱格式不对');
    }
}

function checkRegPhone() {
    var temp;
    temp = new String(document.regPhone.password.value);
    if(document.regPhone.username.value == "") {
        $('#messagePhone').html("<font color=\"#F60018\">* 请输入用户名!</font>");
        return false;
    }
    if(document.regPhone.password.value == "") {
        $('#passwordPhone').html("<font color=\"#F60018\">* 请输入密码!</font>")
        return false;
    }else if(temp.length < 6 || temp.length > 8) {
        $('#passwordPhone').html("<i class=\"fa fa-times-circle\" style=\"font-size:14px;color:red\"></i><font color=\"#F60018\"> 您的密码少于6位或多于16位!</font>")
        return false;
    }
    $.post("http://localhost:8081/a/u/regPhone", $('#regPhone').serialize(), function (data) {
        if(data.status == 200){
            scscms_alert("注册成功，现在去登录？","confirm",function(){
                window.location.href="http://localhost:8081/a/u/gologin";
            });
        }else {
            scscms_alert("注册失败","error");
        }
    }, "json")
}