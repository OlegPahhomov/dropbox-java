var fileHandler = {

    loadPictures: function loadFiles() {
        var showFilesElem = $("#show_files");
        $.ajax("http://localhost:4567/files")
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
    }
}