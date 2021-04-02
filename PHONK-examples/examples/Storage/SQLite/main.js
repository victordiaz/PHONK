/*
 * PHONK Example: SQLite database
 *
 * Use DataBase queries to add and retrieve content
 */

ui.addTitle(app.name)
ui.addSubtitle('Store and load content using SQLite')

var txt = ui.addTextList(0.1, 0.25, 0.8, 0.5).autoScroll(true)

// opens the db if exists otherwise creates one
var db = fileio.openSqlLite('mydb.db')

// if db exists drop the existing table (reset)
db.execSql('DROP TABLE IF EXISTS veggies;')

// create and insert data
db.execSql('CREATE TABLE veggies ' + ' ( type TEXT, color TEXT);')
db.execSql('INSERT INTO veggies (type, color) VALUES ("carrot","orange");')
db.execSql('INSERT INTO veggies (type, color) VALUES ("lettuce","green");')

// check results
var columns = ['type', 'color']
var c = db.query('veggies', columns)

// go through results
txt.add('we got ' + c.getCount() + ' results');
while (c.moveToNext()) {
  txt.add(c.getString(0) + " " + c.getString(1))
}

db.close()
