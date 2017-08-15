/*// Saves image render of website to example.png
var page = require('webpage').create();
page.open('https://www.rightstufanime.com/category/Blu~ray,DVD?show=96', function(status) {
  console.log("Status: " + status);
  if(status === "success") {
    page.render('example.png');
  }
  phantom.exit();
});*/

// From https://stackoverflow.com/questions/16856036/save-html-output-of-page-after-execution-of-the-pages-javascript
// Should save HTML output of page after execution of JavaScript on the page
// Does not save HTML after JavaScript (but before, where complains about no JavaScript)
/*var page = new WebPage()
var fs = require('fs');

page.onLoadFinished = function() {
  console.log("page load finished");
  //page.render('export.png');
  fs.write('example.html', page.content, 'w');
  phantom.exit();
};

page.open("https://www.rightstufanime.com/category/Blu~ray,DVD?show=96", function() {
  page.evaluate(function() {
  });
});*/

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
