
export function trim_leading_char(str, char) {
  if (!str || !char) {
    return str
  }
  while (str.startsWith(char)) {
    str = str.substring(1)
  }
  return str
}
