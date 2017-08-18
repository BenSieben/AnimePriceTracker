// From https://stackoverflow.com/questions/9966826/save-and-render-a-webpage-with-phantomjs-and-node-js and https://stackoverflow.com/questions/32531881/retrieve-fully-populated-dynamic-content-with-phantomjs
var page = require('webpage').create();
var fs = require('fs');
var system = require('system');
var args = system.args;

if(args.length === 3) {
    // Give descriptive names to our arguments
    var url = args[1];
    var waitTime = parseInt(args[2]);
    
    // Set wait time to 0 if it is a negative number / invalid number
    if(isNaN(waitTime) ||waitTime < 0) {
        waitTime = 0;
    }
    
    // Open the given url, and once connection is made, wait 
    //   specified amount of time for JavaScript to fully load the page
    page.open(url, function (status) {
        if (status !== 'success') {
            console.log('Unable to access URL ' + url);
            phantom.exit();
        } else {
            window.setTimeout(function() {
                console.log(page.content);
                // Use below line if you want to save the page content back to a a file as well
                // fs.write('test.html', page.content, 'w');
                phantom.exit();
            }, waitTime);
        }
    });
}
else {
    // Echo out how this script should be used if there is not exactly 1 argument
    console.log("Usage: <phantomjs_executable> loadpage.js <URL_to_open> <milliseconds_to_wait_for_javascript_to_load>");
    phantom.exit();
}
