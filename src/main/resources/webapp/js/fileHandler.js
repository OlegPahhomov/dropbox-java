var fileHandler = {

    loadPictures: function loadFiles() {
        var showFilesElem = $("#show_files");
        $.ajax(serverConfig.url('files'))
            .done(function (response) {
                showFilesElem.html(tmpl("show_files_template",
                    {
                        files: response,
                        getRatioClass: function (image) {
                            var experimental_ratio = 1.45;
                            return image.ratio > experimental_ratio ? 'file bigfile' : 'file';
                        }
                    }
                ))
            }).fail(function (jqXHR, textStatus) {
                showFilesElem.html("Error occurred");
                console.log("Request failed: " + textStatus);
            });
    },
    deletePicture: function deletePicture(){
        //atm doesnt work, works in console, though
        console.log($("[id^=delete_file_]").length);
    },

    savePictures: function saveFiles() {
        var addPictureFormElem = $('#fileForm');
        addPictureFormElem.submit(function (e) {
            e.preventDefault();
            var data = new FormData($(this)[0]);

            console.log("got here");
            $.ajax({
                url: serverConfig.url('add'),
                data: data,
                cache: false,
                contentType: false,
                processData: false,
                type: 'POST'
            }).done(function () {
                location.reload();
            }).fail(function (jqXHR, textStatus) {
                alert("Error occurred");
                console.log("Request failed: " + textStatus);
            });
        });
    }
}