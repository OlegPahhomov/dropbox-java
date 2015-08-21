var serverConfig = {

    SERVER: function(){
        return this.SPARK();
    },

    url: function (url) {
        return  serverConfig.SERVER() + url
    },






    LOCALHOST: "http://localhost:",
    SPARK_PORT: "4567/",

    SPARK: function () {
        return (this.LOCALHOST + this.SPARK_PORT)
    }
}
