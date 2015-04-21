/**
 * Oxy namespace
 */
var oxy = {};

/**
 * Debug namespace
 */
var debug = {};

/**
 * Utils namespace
 */
var utils = {};

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
     * @param {String} content
     */
    print: function(content){},

    /**
     * @param {String} content
     */
    println: function(content){},

    /**
     * @param {String} content
     */
    printHtml: function(content){},

    /**
     * @param {String} content
     */
    printlnHtml: function(content){}
};

/**
 * @param {String} messageCode
 * @return String
 */
getMessage = function (messageCode) {};
