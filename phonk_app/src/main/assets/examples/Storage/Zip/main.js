/*
 * \\\ Example:	Zip / unzip files
 */
ui.addTitle(app.name)
ui.addSubtitle('Zip and unzip files / folders')

fileio.zip('folder_to_zip', 'zip_file.zip', function (e) {
  console.log('file zipped')
})

// unzip the files in the current . directory
fileio.unzip('zip_file.zip', './unzipped', function (e) {
  console.log('file unzipped')
})
