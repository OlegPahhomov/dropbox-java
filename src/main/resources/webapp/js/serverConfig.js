var serverConfig = {
    LOCALHOST: "http://localhost:",
    SPARK_PORT: "4567/",
    SPARK: fileHandler.LOCALHOST + fileHandler.SPARK_PORT,
    SERVER: fileHandler.SPARK,

    url : function(url){
        return fileHandler.SERVER + url;
    }
}
