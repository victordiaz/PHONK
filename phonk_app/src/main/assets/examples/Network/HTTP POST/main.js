/*
 * \\\ Example: HTTP_POST
 *
 * With this code you are able to upload multipart content to a webserver
 * An example of a php file that will handle the upload is included
 *
 * The type of each data part can be text / json / file
 * If type === file then it will automatically load from the folder
 * and transfer
 *
 */

ui.addTitle(app.name)

var url = 'http://www.protocoder.org/p/post.php'

var data = [
  { 'name' : 'field_1', 'content' : 'hello', 'type' : 'text' },
  { 'name' : 'field_2', 'content' : 'world', 'type' : 'text' },
  { 'name' : 'userfile', 'content' : 'patata2.png', 'type' : 'file', 'mediaType': 'image/png'}
]

network.httpPost(url, data, function (e) {
  console.log(e)
})
