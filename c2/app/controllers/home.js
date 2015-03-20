
/*!
 * Module dependencies.
 */
var tail = require('../../util/tail')


exports.index = function (req, res) {
  res.render('home/index', {
    title: 'Metaliquid Command and Control'
  });
};

exports.console = function (req, res) {
  res.render('home/console', {
    title: 'Metaliquid Command and Control'
  });
};

exports.forever = function (req, res) {
  res.render('home/forever', {
    title: 'Metaliquid running apps'
  });
};

exports.ide = function (req, res) {
  res.render('home/ide', {
    title: 'Metaliquid IDE'
  });
};

exports.scripts = function (req, res){
    
    res.json({"foo":"bar"})
}

