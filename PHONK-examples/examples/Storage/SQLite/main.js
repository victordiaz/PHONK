/*
 * \\\ Example: SQLite database
 *
 * Use DataBase queries to add and retrieve content
 */

ui.addTitle(app.name)

var txt = ui.addText('stored and loaded data: \n\n', 0.1, 0.15, 0.8, 0.5)
txt.props.background = '#000000'
txt.props.padding = 20
txt.props.textSize = 15

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
txt.append('we got ' + c.getCount() + ' results \n\n');
while (c.moveToNext()) {
  txt.append(c.getString(0) + " " + c.getString(1) + '\n')
}

db.close()
