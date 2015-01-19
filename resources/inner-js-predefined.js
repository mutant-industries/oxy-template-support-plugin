/**
 * Oxy namespace
 */
var oxy = {};

/**
 * Debug namespace
 */
var debug = {};

/**
 * Název registru maker, do kterého může skript přidávat makra.
 */
var macros = {
    /**
     * Oxy namespace
     */
    oxy: {}
};

/**
 * Proměnná, která obsahuje PrintWriter pro výstup.
 */
var out = {
    /**
     * @param {String}content
     */
    print: function(content){},

    println: function(){},
    /**
     * @param {String}content
     */
    println: function(content){},
    /**
     * @param {String}content
     */
    printHtml: function(content){},
    /**
     * @param {String}content
     */
    printlnHtml: function(content){}
};

/**
 @param {String} messageCode
 @return String
 */
getMessage = function (messageCode) {};
