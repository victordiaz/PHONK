/*
*	
*	Tiny example using SqlLite. 
*   
*
*/

var CREATE_TABLE = "CREATE TABLE veggies " + " ( type TEXT, color TEXT);" 
var DROP_TABLE = "DROP TABLE IF EXISTS veggies;";

//it opens the db if exists otherwise creates one 
var db = fileio.openSqlLite("mydb.db");

//if db exists drop the existing table (reset)
db.execSql(DROP_TABLE);

//create and insert data 
db.execSql(CREATE_TABLE);
db.execSql("INSERT INTO veggies (type, color) VALUES ('carrot','orange');");
db.execSql("INSERT INTO veggies (type, color) VALUES ('lettuce','green');");

//check results
var columns = ["type", "color"];
var c = db.query("veggies", columns); 
console.log("we got " + c.getCount() + " results"); // how many results

//go through results
while (c.moveToNext()) {
  console.log(c.getString(0) + " " + c.getString(1));
} 

db.close();