const urlParser = document.createElement('a')

export function domain (url) {
  urlParser.href = url
  return urlParser.hostname
}

export function faIcon (iconName) {
  return '<i class = "fa fa-' + iconName + '-o"></i>'
}
