cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/org.bcsphere.pedometer/www/pedometer.js",
        "id": "org.bcsphere.pedometer.pedometer",
        "merges": [
            "navigator.pedometer"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "org.bcsphere.pedometer": "0.0.1"
}
// BOTTOM OF METADATA
});