$("#msg-file-input").change(function(){
    var ofReader = new FileReader();
    var file = document.getElementById('msg-file-input').files[0];
    var total = file.size;
    let isImg = file.type.startsWith("image/");
    ofReader.readAsDataURL(file);
    ofReader.onloadend = function(ofrEvent){
        var src = ofrEvent.target.result;
        // console.log(src);
        $(".sending-img-on").removeClass("d-none");
        if(isImg){
            // $(".typing-img-show").removeClass("d-none");
            // $(".typing-video-show").addClass("d-none");
            $(".sending-img").attr("src",src);
            // $(".typing-img-show img").load();
        } else {
            // $(".typing-video-show").removeClass("d-none");
            // $(".typing-img-show").addClass("d-none");
            $(".typing-video-show video source").attr("src",src);
            $(".typing-video-show video")[0].load();
        }
    }
    ofReader.onprogress = function(e) {

    }
    ofReader.onload = function() {

    }
});