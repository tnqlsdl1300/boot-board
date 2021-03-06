var main = {
    init : function () {
        var _this = this;
        $('#btn-save').on('click', function () {
            _this.save();
        });

        $('#btn-update').on('click', function () {
            _this.update();
        });

        $('#btn-delete').on('click', function () {
            _this.delete();
        });

    },
    save : function () {
        var data = {
            title: $('#title').val(),
            author: $('#author').val(),
            content: $('#content').val()
        };

        $.ajax({
            type: 'POST',
            url: '/api/v1/posts',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            // JSON.stringify(data) -> 받은 데이터를 json 으로 변환시켜줌
            data: JSON.stringify(data)
        }).done(function() {
            alert('글이 등록되었습니다.');
            window.location.href = '/';
        }).fail(function (error) {
            if(error.readyState == 4 || error.readyState == 0){
                 alert("권한이 없습니다.");
            }
            alert(JSON.stringify(error));
        });
    },
    update : function(){
        var data = {
            title: $('#title').val(),
            content: $('#content').val(),
            author: $('#author').val()
        };

        var id = $('#id').val();

        $.ajax({
            type: 'PUT',
            url: '/api/v1/posts/'+id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function(json) {
            if(json == -999){
                // 게시글의 작성자가 아닐시(실패)
                alert("작성자가 아닙니다.");
            }else{
                // 게시글의 작성자 일시(성공)
                window.location.href = '/';
                alert('글이 수정되었습니다.');
            }
        }).fail(function (error) {
            if(error.readyState == 4 || error.readyState == 0){
                 alert("권한이 없습니다.");
            }
        });
    },
    delete : function(){
        var id = $('#id').val();

        $.ajax({
            type: 'DELETE',
            url: '/api/v1/posts/'+id,
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
        }).done(function() {
            alert('글이 삭제되었습니다.');
            window.location.href = '/';
        }).fail(function (error) {
            if(error.readyState == 4 || error.readyState == 0){
                 alert("권한이 없습니다.");
            }
        });
    }

};

main.init();