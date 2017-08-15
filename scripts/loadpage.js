// From https://stackoverflow.com/questions/9966826/save-and-render-a-webpage-with-phantomjs-and-node-js
var page = require('webpage').create();
var fs = require('fs');
var system = require('system');
var args = system.args;

if(args.length === 2) {
    var url = args[1];
    page.open(url, function (status) {
        if (status !== 'success') {
            console.log('Unable to access network at ' + url);
        } else {
            console.log(page.content);
            // Use below line if you want to save the page content back to a a file
            // fs.write('test.html', page.content, 'w');
        }
        phantom.exit();
    });
}
else {
    // Echo out how this script should be used if there is not exactly 1 argument
    console.log("Usage: <phantomjs_executable> loadpage.js <URL_to_open>");
    phantom.exit();
}
