function str4mat(str, args) {
  if (!str || str == "" || !args)
    return str;
  for (x in args)
    str = str.replace(new RegExp("([^\\\\]?(\\\\{2})*)\\{" + x + "\\}"), "$1" + args[x]);
  return str;
}
