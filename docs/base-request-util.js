/*
 * BaseRequest string parser utils (Vue2 friendly)
 * Source: BaseRequest#setSorts/setQueries
 *
 * 格式说明：
 * 1) sorts:   "field:asc,createdAt:desc"
 * 2) queries: "name:tom:1,age:18:4"
 *    - 第1段: filedName
 *    - 第2段: value
 *    - 第3段: type(QueryType code), 默认 1(EQ)
 */

var BASE_REQUEST_SEP = ',';
var BASE_REQUEST_TYPE_SEP = ':';

var QueryType = {
  EQ: 1,
  NE: 2,
  GT: 3,
  GE: 4,
  LT: 5,
  LE: 6,
  LIKE: 7,
  IN: 8,
  NOT_IN: 9,
  IS_NULL: 10,
  IS_NOT_NULL: 11
};

function safeString(input) {
  if (input === null || input === undefined) return '';
  return String(input).trim();
}

function parseSorts(sortsStr) {
  var raw = safeString(sortsStr);
  if (!raw) return [];

  return raw
    .split(BASE_REQUEST_SEP)
    .map(function (item) {
      return safeString(item);
    })
    .filter(Boolean)
    .map(function (item) {
      var parts = item.split(BASE_REQUEST_TYPE_SEP);
      var column = safeString(parts[0]);
      var sort = safeString(parts[1]).toLowerCase() || 'desc';
      return {
        column: column,
        sort: sort === 'asc' ? 'asc' : 'desc'
      };
    })
    .filter(function (it) {
      return !!it.column;
    });
}

function stringifySorts(sorts) {
  if (!Array.isArray(sorts) || sorts.length === 0) return '';

  return sorts
    .map(function (s) {
      var column = safeString(s && s.column);
      if (!column) return '';
      var sort = safeString(s && s.sort).toLowerCase() || 'desc';
      sort = sort === 'asc' ? 'asc' : 'desc';
      return column + BASE_REQUEST_TYPE_SEP + sort;
    })
    .filter(Boolean)
    .join(BASE_REQUEST_SEP);
}

function parseQueries(queriesStr) {
  var raw = safeString(queriesStr);
  if (!raw) return [];

  return raw
    .split(BASE_REQUEST_SEP)
    .map(function (item) {
      return safeString(item);
    })
    .filter(Boolean)
    .map(function (item) {
      var parts = item.split(BASE_REQUEST_TYPE_SEP);
      var filedName = safeString(parts[0]);
      var value = parts.length > 1 ? safeString(parts[1]) : '';
      var type = parts.length > 2 ? Number(safeString(parts[2])) : QueryType.EQ;

      if (!Number.isFinite(type)) {
        type = QueryType.EQ;
      }

      return {
        filedName: filedName,
        value: value,
        type: type
      };
    })
    .filter(function (q) {
      return !!q.filedName;
    });
}

function stringifyQueries(queries) {
  if (!Array.isArray(queries) || queries.length === 0) return '';

  return queries
    .map(function (q) {
      var filedName = safeString(q && q.filedName);
      if (!filedName) return '';

      var value = q && q.value !== undefined && q.value !== null ? String(q.value) : '';
      var type = Number(q && q.type);
      if (!Number.isFinite(type)) type = QueryType.EQ;

      return [filedName, value, type].join(BASE_REQUEST_TYPE_SEP);
    })
    .filter(Boolean)
    .join(BASE_REQUEST_SEP);
}

function buildBaseRequestPayload(options) {
  var opt = options || {};
  return {
    page: opt.page == null ? 1 : Number(opt.page),
    size: opt.size == null ? 10 : Number(opt.size),
    sorts: Array.isArray(opt.sorts) ? opt.sorts : parseSorts(opt.sortsStr),
    filedQueries: Array.isArray(opt.filedQueries) ? opt.filedQueries : parseQueries(opt.queriesStr)
  };
}

var BaseRequestUtil = {
  BASE_REQUEST_SEP: BASE_REQUEST_SEP,
  BASE_REQUEST_TYPE_SEP: BASE_REQUEST_TYPE_SEP,
  QueryType: QueryType,
  parseSorts: parseSorts,
  stringifySorts: stringifySorts,
  parseQueries: parseQueries,
  stringifyQueries: stringifyQueries,
  buildBaseRequestPayload: buildBaseRequestPayload
};

if (typeof module !== 'undefined' && module.exports) {
  module.exports = BaseRequestUtil;
}

if (typeof window !== 'undefined') {
  window.BaseRequestUtil = BaseRequestUtil;
}
