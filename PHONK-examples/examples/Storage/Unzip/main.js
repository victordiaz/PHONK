/*
 * \\\ Example:	Unzip files
 */

// unzip the files in the current . directory
fileio.unzip('zip_file.zip', '.', function (data) {
  console.log(data)
})
