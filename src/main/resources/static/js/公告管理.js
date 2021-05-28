
$('#registSubmit').on('submit', function () {
    registPost()
    event.preventDefault() //阻止form表单默认提交
})
$('#submit').click(function() {
    var formData = new FormData();
    formData.append("file", $('#image')[0].files[0]);
    formData.append("name", $("#name").val());
    formData.append("number",$("#number").val());
    formData.append("content", $("#content").val());

    $.ajax({
        url:'Notice/insert',
        dataType:'json',
        type:'POST',
        async: false,
        data: formData,
        processData : false, // 使数据不做处理
        contentType : false, // 不要设置Content-Type请求头
        success: function(data){
            var {data,message,success} = data
            if (success) {
                alert('添加成功');
            }else{
                alert(message.message)
            }
        },
        error:function(response){
            console.log(response);
        }
    });
})
$("#addnotice").click(function(){
    $.ajax({
        url:'Notice/selectAll2',
        dataType:'json',
        type:'POST',
        async: false,
        data: {},
        processData : false, // 使数据不做处理
        contentType : false, // 不要设置Content-Type请求头
        success: function(data){
            console.log(data);
            var {data,message,success} = data
            if(success){

                if(data.length>0){
                    $("#number").val(data.length+1)
                }else{
                    $("#number").val(1)
                }
            }else{
                alert(message.message)
            }
        },
        error:function(response){
            console.log(response);
        }
    });
})

$("#loadnotice").click(load)
function load(){
    $.ajax({
        url:'Notice/selectAll',
        dataType:'json',
        type:'POST',
        async: false,
        data: {},
        processData : false, // 使数据不做处理
        contentType : false, // 不要设置Content-Type请求头
        success: function(data){
            console.log(data);
            var {data,message,success} = data
            if(success){
                Date.prototype.Format = function (fmt) { //author: meizz

                    var o = {
                        "M+": this.getMonth() + 1, //月份
                        "d+": this.getDate(), //日
                        "h+": this.getHours(), //小时
                        "m+": this.getMinutes(), //分
                        "s+": this.getSeconds(), //秒
                        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
                        "S": this.getMilliseconds() //毫秒
                    };
                    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
                    for (var k in o)
                        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
                    return fmt;

                }
                if(data!=null){
                    tbodyAppend(data)

                }
            }else{
                alert(message.message)
            }
        },
        error:function(response){
            console.log(response);
        }
    });
}
var btn_delete = function (id) {
    let param={
        "id":id
    }
    $.jq_Confirm({
        message: "您确定要删除吗?",
        btnOkClick: function () {
            $.ajax({
                url:'Notice/delete',
                dataType:'json',
                type:'POST',
                async: false,
                data: param,
                success: function(data){
                    console.log(data);
                    var {data,message,success} = data
                    if(success){
                        alert("删除成功")
                        tbodyAppend(data)
                    }else{
                        alert(message.message)
                    }
                },
                error:function(response){
                    console.log(response);
                }
            });
        }
    });
}
function tbodyAppend(data){
    $("#tbody").empty()
    let i = 1;
    data.map(item=>{
        let id = item.id.toString()
        let array = item.ts.split("+")
        console.log("1234")
        let date = new Date(array[0]).Format("yyyy-MM-dd")
        $("#tbody").append('<tr>'
        +'<td>'+i+++'</td><td>'+item.name+'</td><td>'+date+'</td><td>'+item.content+'</td><td><img src="'+item.url+'" width="50px;" height="50px;"/></td>'
        +'<td class="edit"><a href="./新闻公告修改.html?flag=2&number='+item.number+'"><button onclick="btn_edit('+item.number+')"><i class="icon-edit bigger-120"></i>修改</button></a></td>'
        +"<td class='delete'><button onclick=btn_delete("+'"'+id+'"'+")"
        +'\><i class="icon-trash bigger-120"></i>删除</button></td>'
        +'</tr>'
    )
})
}