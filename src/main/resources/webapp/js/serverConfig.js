var serverConfig = {
    LOCALHOST: "http://localhost:",
    SPARK_PORT: "4567/",
    SPARK: fileLoader.LOCALHOST + fileLoader.SPARK_PORT,
    SERVER: fileLoader.SPARK,

    url : function(url){
        return fileLoader.SERVER + url;
    }
}
