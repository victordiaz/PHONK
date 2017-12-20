var getKeys = function(obj){
   var keys = [];
   for(var key in obj){
      keys.push(key);
      console.log(key);
   }
   return keys;
}

getKeys(dashboard);

ui.yesnoDialog("hola", function(response) { 
    console.log(response);
});

editor.showEditor(true);

var count = 0;
util.loop(500, function() {
    console.log("hello " + count++);
})